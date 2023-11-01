package com.ruoyi.framework.web.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.InterfaceItem;
import com.ruoyi.system.domain.vo.InterfaceDebugVO;
import com.ruoyi.system.mapper.InterfaceItemMapper;
import com.ruoyi.system.service.IInterfaceService;
import com.ruoyi.system.service.InterfaceUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InterfaceServiceImpl implements IInterfaceService {
    @Autowired
    private InterfaceItemMapper interfaceItemMapper;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    private PermissionService ss;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private InterfaceUtil interfaceUtil;

    @Autowired
    private RedisCache redisCache;

    private final static InterfaceItem nullItem = new InterfaceItem();

    private final static int TYPE_SQL = 1;
    private final static int TYPE_JS = 2;
    private final static int RS_LIMIT = 100000;
    private final static int TIMEOUT = 30;

    private static final Logger log = LoggerFactory.getLogger(InterfaceServiceImpl.class);

    @Override
    @PostConstruct
    public void initLocalCache() {
        // 占位用，因需要确保RedisUtils在InterfaceService加载之前加载
        redisCache.hashCode();

        // 加载本地缓存
        InterfaceItem vo = new InterfaceItem();
        vo.setIsEnable(1L);
        List<InterfaceItem> interfaceList = interfaceItemMapper.selectInterfaceItemList(vo);
        for (InterfaceItem item : interfaceList) {
            String group, subgroup = "";
            String[] p = ObjectUtils.defaultIfNull(item.getGroupCode(), "").split("/");
            group = p[0];
            if (p.length > 1) {
                subgroup = p[1];
                for (int i = 2; i < p.length; i++) {
                    subgroup += "/" + p[i];
                }
            }

            setInterfaceInfoToCache(group, subgroup, item.getCode(), item);
        }
        log.info("[init][初始化动态接口成功，接口数量=" + interfaceList.size() + "]");
    }

    @Override
    public Object invokeInterface(String group, String subgroup, String code, Map<String, Object> paramMap, String method) {
        // 记录开始时间
        Date startTime = new Date();

        // 获取接口信息
        InterfaceItem info = getInterfaceInfo(group, subgroup, code);
        if (info == null || info.getIsEnable() != 1) {
            throw new ServiceException("接口不存在", HttpStatus.NOT_FOUND);
        }

        // 校验接口认证及权限
        ServiceException check = checkPermission(info.getPermission());
        if (check != null) {
            throw check;
        }

        // 校验请求方式限制
        if (!checkMethod(method, info.getMethod())) {
            throw new ServiceException("接口不存在", HttpStatus.NOT_FOUND);
        }

        // 判断类型并执行
        Object result = null;
        Exception e = null;
        if (info.getType() == TYPE_SQL) {
            try {
                result = executeSql(info.getProgram(), paramMap, info.getDatasource());
            } catch (SQLException ex) {
                e = ex;
            }
        } else if (info.getType() == TYPE_JS) {
            try {
                result = executeJs(info.getProgram(), paramMap);
            } catch (ScriptException ex) {
                e = ex;
            }
        } else {
            throw new ServiceException("无效的接口类型");
        }

        // TODO 接口调用日志
        if (info.getIsLog() == 1) {
            // doLog(info, startTime, paramMap, result, e);
        }

        // 调用失败时直接抛出异常
        if (e != null) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public InterfaceItem getInterfaceInfo(String group, String subgroup, String code) {
        // 先从缓存获取信息，如获取不到则从数据库获取，并进行缓存
        InterfaceItem info = getInterfaceInfoFromCache(group, subgroup, code);

        if (info == nullItem) {
            return null;
        } else if (info == null) {
            String groupCode = "";
            if (StringUtils.isNotEmpty(group)) {
                groupCode = group;
            }
            if (StringUtils.isNotEmpty(subgroup)) {
                groupCode = group + "/" + subgroup;
            }

            InterfaceItem vo = new InterfaceItem();
            vo.setCode(code);
            vo.setIsEnable(1L);
            vo.setGroupCode(groupCode);
            List<InterfaceItem> list = interfaceItemMapper.selectInterfaceItemList(vo);
            if (!list.isEmpty()) {
                info = list.get(0);
            } else {
                info = null;
            }

            // 获取成功后，进行缓存
            setInterfaceInfoToCache(group, subgroup, code, info);
        }

        return info;
    }

    @Override
    public void cleanCache(String groupPath, String code) {
        String group, subgroup = "";
        String[] p = groupPath.split("/");
        group = p[0];
        if (p.length > 1) {
            subgroup = p[1];
            for (int i = 2; i < p.length; i++) {
                subgroup += "/" + p[i];
            }
        }

        if (StringUtils.isNotEmpty(group) && StringUtils.isEmpty(code)) {
            Collection<String> keys = redisCache.keys(getCacheKey(group, subgroup, "*"));
            redisCache.deleteObject(keys);
        } else {
            redisCache.deleteObject(getCacheKey(group, subgroup, code));
        }
    }

    @Override
    public String debugInterface(InterfaceDebugVO debugVO) {
        try {
            AjaxResult result;

            // 判断类型并执行
            if (debugVO.getType() == TYPE_SQL) {
                result = AjaxResult.success(executeSql(debugVO.getCode(), debugVO.getParamMap(), debugVO.getDatasource()));
            } else if (debugVO.getType() == TYPE_JS) {
                result = AjaxResult.success(executeJs(debugVO.getCode(), debugVO.getParamMap()));
            } else {
                throw new ServiceException("无效的接口类型");
            }

            return JSON.toJSONString(result, JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.PrettyFormat);
        } catch (Exception e) {
            // 发生异常时，将报错信息直接当作接口返回结果
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        }
    }

    private List<Map<String, Object>> executeSql(String rawSql, Map<String, Object> paramMap, String dataSource) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();

        Connection conn = null;
        SqlSession session = null;

        // 切换数据源
        if (StringUtils.isNotEmpty(dataSource)) {
            DynamicDataSourceContextHolder.push(dataSource);
        }

        // 获取数据库会话
        session = SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory(),
                sqlSessionTemplate.getExecutorType(), sqlSessionTemplate.getPersistenceExceptionTranslator());
        conn = session.getConnection();

        if (conn == null) {
            throw new SQLException("数据源错误: " + dataSource);
        }

        PreparedStatement ptst = null;
        try {
            // 利用MyBatis的框架解析SQL模板
            SqlSource sqlSource = sqlSessionTemplate.getConfiguration().getLanguageDriver(XMLLanguageDriver.class).createSqlSource(sqlSessionTemplate.getConfiguration(), rawSql, Map.class);
            BoundSql boundSql = sqlSource.getBoundSql(paramMap);
            String parsedSql = boundSql.getSql();

            ptst = conn.prepareStatement(parsedSql);

            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

            if (parameterMappings != null) {
                for (int i = 0; i < parameterMappings.size(); i++) {
                    ParameterMapping parameterMapping = parameterMappings.get(i);
                    if (parameterMapping.getMode() != ParameterMode.OUT) {
                        Object value = null;
                        String propertyName = parameterMapping.getProperty();
                        value = paramMap.get(propertyName);

                        TypeHandler typeHandler = parameterMapping.getTypeHandler();
                        JdbcType jdbcType = parameterMapping.getJdbcType();
                        if (value == null && jdbcType == null) {
                            jdbcType = sqlSessionTemplate.getConfiguration().getJdbcTypeForNull();
                        }
                        typeHandler.setParameter(ptst, i + 1, value, jdbcType);
                    }
                }
            }

            ptst.setQueryTimeout(TIMEOUT);
            ResultSet result = ptst.executeQuery();
            ResultSetMetaData md = result.getMetaData();
            int columnCount = md.getColumnCount();
            int n = 0;
            while (result.next() && n++ < RS_LIMIT) {
                Map<String, Object> rowData = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnLabel(i), result.getObject(i));
                }
                list.add(rowData);
            }
        } finally {
            if (ptst != null) {
                try {
                    ptst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (session != null) {
                SqlSessionUtils.closeSqlSession(session, sqlSessionTemplate.getSqlSessionFactory());
            }
            if (StringUtils.isNotEmpty(dataSource)) {
                DynamicDataSourceContextHolder.poll();
            }
        }

        return list;
    }

    private boolean checkMethod(String method, String allowMethod) {
        if ("any".equals(allowMethod) || StringUtils.isEmpty(allowMethod)) {
            return true;
        }
        return StringUtils.equals(method, allowMethod);
    }

    private ServiceException checkPermission(String permission) {
        // 免登录
        if ("*".equals(permission)) {
            return null;
        }

        // 校验是否登录
        HttpServletRequest request = ServletUtils.getRequest();
        LoginUser user = tokenService.getLoginUser(request);
        if (user != null) {
            // 设置为当前用户
            tokenService.setLoginUser(user);
        } else {
            return new ServiceException("认证失败", HttpStatus.UNAUTHORIZED);
        }

        // 接口不限制权限
        if (StringUtils.isEmpty(permission)) {
            return null;
        }

        // 校验是否有接口权限
        boolean hasPermission = ss.hasPermi(permission);
        if (hasPermission) {
            return null;
        } else {
            return new ServiceException("无访问权限", HttpStatus.FORBIDDEN);
        }
    }

    private Object executeJs(String code, Map<String, Object> paramMap) throws ScriptException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

        // 将InterfaceUtil传入到JS环境中，这样就可以通过AppUtil来调用Java的方法
        engine.put("AppUtil", interfaceUtil);

        // 将代码封装至 Main 函数中，在JS代码中可通过 paramMap 获取参数。
        code = "load(\"nashorn:mozilla_compat.js\"); function Main(paramMap) {\n" + code + "\n}";

        // 编译并运行脚本
        CompiledScript script = ((Compilable) engine).compile(code);
        script.eval();

        Invocable inv2 = (Invocable) engine;
        try {
            return inv2.invokeFunction("Main", paramMap);
        } catch (NoSuchMethodException e) {
            throw new ScriptException(e);
        }
    }

    private InterfaceItem getInterfaceInfoFromCache(String group, String subgroup, String code) {
        String key = getCacheKey(group, subgroup, code);
        if (!redisCache.hasKey(key)) {
            return null;
        }
        String val = redisCache.getCacheObject(key);
        if ("NULL".equals(val)) {
            return nullItem;
        }
        return JSON.parseObject(val, InterfaceItem.class);
    }

    private void setInterfaceInfoToCache(String group, String subgroup, String code, InterfaceItem info) {
        String key = getCacheKey(group, subgroup, code);
        if (info == null) {
            redisCache.setCacheObject(key, "NULL");
        } else {
            redisCache.setCacheObject(key, JSON.toJSONString(info, JSONWriter.Feature.WriteMapNullValue));
        }
    }

    private String getCacheKey(String group, String subgroup, String code) {
        if (StringUtils.isEmpty(group)) {
            return "interface:" + code;
        } else if (StringUtils.isEmpty(subgroup)) {
            return "interface:" + group + ":" + code;
        } else {
            return "interface:" + group + ":" + subgroup.replace("/", ":") + ":" + code;
        }
    }

}
