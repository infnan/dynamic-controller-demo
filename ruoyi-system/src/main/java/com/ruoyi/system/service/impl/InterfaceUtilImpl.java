package com.ruoyi.system.service.impl;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.ruoyi.system.service.InterfaceUtil;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InterfaceUtilImpl implements InterfaceUtil {

    @Resource
    private SqlSessionTemplate sqlSessionTemplate;

    @Resource
    private ApplicationContext applicationContext;

    private static int TIMEOUT = 30;

    @Override
    public List<Map<String, Object>> executeQuery(String sql, Object... params) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();

        SqlSession session = SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory(),
                sqlSessionTemplate.getExecutorType(), sqlSessionTemplate.getPersistenceExceptionTranslator());

        PreparedStatement ptst = null;
        ResultSet result = null;
        try {
            ptst = session.getConnection().prepareStatement(sql);

            for (int i = 1; i <= params.length; i++) {
                setStatementParam(ptst, i, params[i - 1]);
            }

            ptst.setQueryTimeout(TIMEOUT);
            result = ptst.executeQuery();
            ResultSetMetaData md = result.getMetaData();
            int columnCount = md.getColumnCount();
            int n = 0;
            while (result.next()) {
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
            SqlSessionUtils.closeSqlSession(session, sqlSessionTemplate.getSqlSessionFactory());
        }

        return list;
    }

    @Override
    public boolean executeSql(String sql, Object... params) throws SQLException {
        SqlSession session = SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory(),
                sqlSessionTemplate.getExecutorType(), sqlSessionTemplate.getPersistenceExceptionTranslator());

        PreparedStatement ptst = null;
        boolean result = false;
        try {
            ptst = session.getConnection().prepareStatement(sql);

            for (int i = 1; i <= params.length; i++) {
                setStatementParam(ptst, i, params[i - 1]);
            }

            ptst.setQueryTimeout(TIMEOUT);
            result = ptst.execute();
        } finally {
            if (ptst != null) {
                try {
                    ptst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            SqlSessionUtils.closeSqlSession(session, sqlSessionTemplate.getSqlSessionFactory());
        }

        return result;
    }

    @Override
    public int[] executeBatch(String... sql) throws SQLException {
        SqlSession session = SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory(),
                sqlSessionTemplate.getExecutorType(), sqlSessionTemplate.getPersistenceExceptionTranslator());

        Statement st = null;
        int[] result = null;
        try {
            st = session.getConnection().createStatement();
            for (String s : sql) {
                st.addBatch(s);
            }
            result = st.executeBatch();
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            SqlSessionUtils.closeSqlSession(session, sqlSessionTemplate.getSqlSessionFactory());
        }
        return result;
    }

    @Override
    public <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    @Override
    public Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    @Override
    public void DS(String dataSource) {
        DynamicDataSourceContextHolder.push(dataSource);
    }

    @Override
    public void DS_done() {
        DynamicDataSourceContextHolder.poll();
    }

    private void setStatementParam(PreparedStatement ptst, int i, Object item) throws SQLException {
        if (item == null) {
            ptst.setNull(i, Types.NULL);
        } else if (item instanceof Integer) {
            ptst.setInt(i, (int) item);
        } else if (item instanceof Long) {
            ptst.setLong(i, (long) item);
        } else if (item instanceof Float) {
            ptst.setFloat(i, (float) item);
        } else if (item instanceof Double) {
            ptst.setDouble(i, (float) item);
        } else if (item instanceof String) {
            ptst.setString(i, (String) item);
        } else if (item instanceof Boolean) {
            ptst.setBoolean(i, (Boolean) item);
        }
    }
}
