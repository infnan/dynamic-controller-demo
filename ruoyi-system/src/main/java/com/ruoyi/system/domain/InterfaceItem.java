package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 接口项目对象 interface_item
 * 
 * @author ruoyi
 * @date 2023-10-31
 */
public class InterfaceItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 接口名 */
    @Excel(name = "接口名")
    private String code;

    /** 类型，1-sql，2-js */
    @Excel(name = "类型，1-sql，2-js")
    private Long type;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 请求方式,GET/POST */
    @Excel(name = "请求方式,GET/POST")
    private String method;

    /** 分组 */
    @Excel(name = "分组")
    private Long groupId;

    /** 分组 */
    @Excel(name = "分组")
    private String groupCode;

    /** 数据源 */
    @Excel(name = "数据源")
    private String datasource;

    /** 是否启用 */
    @Excel(name = "是否启用")
    private Long isEnable;

    /** 是否记录调用日志 */
    @Excel(name = "是否记录调用日志")
    private Long isLog;

    /** 权限标识,*表示免登录 */
    @Excel(name = "权限标识,*表示免登录")
    private String permission;

    /** 输入参数 */
    @Excel(name = "输入参数")
    private String param;

    /** 代码内容 */
    @Excel(name = "代码内容")
    private String program;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setCode(String code) 
    {
        this.code = code;
    }

    public String getCode() 
    {
        return code;
    }
    public void setType(Long type) 
    {
        this.type = type;
    }

    public Long getType() 
    {
        return type;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setMethod(String method) 
    {
        this.method = method;
    }

    public String getMethod() 
    {
        return method;
    }
    public void setGroupId(Long groupId) 
    {
        this.groupId = groupId;
    }

    public Long getGroupId() 
    {
        return groupId;
    }
    public void setGroupCode(String groupCode) 
    {
        this.groupCode = groupCode;
    }

    public String getGroupCode() 
    {
        return groupCode;
    }
    public void setDatasource(String datasource) 
    {
        this.datasource = datasource;
    }

    public String getDatasource() 
    {
        return datasource;
    }
    public void setIsEnable(Long isEnable) 
    {
        this.isEnable = isEnable;
    }

    public Long getIsEnable() 
    {
        return isEnable;
    }
    public void setIsLog(Long isLog) 
    {
        this.isLog = isLog;
    }

    public Long getIsLog() 
    {
        return isLog;
    }
    public void setPermission(String permission) 
    {
        this.permission = permission;
    }

    public String getPermission() 
    {
        return permission;
    }
    public void setParam(String param) 
    {
        this.param = param;
    }

    public String getParam() 
    {
        return param;
    }
    public void setProgram(String program) 
    {
        this.program = program;
    }

    public String getProgram() 
    {
        return program;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("code", getCode())
            .append("type", getType())
            .append("description", getDescription())
            .append("method", getMethod())
            .append("groupId", getGroupId())
            .append("groupCode", getGroupCode())
            .append("datasource", getDatasource())
            .append("isEnable", getIsEnable())
            .append("isLog", getIsLog())
            .append("permission", getPermission())
            .append("param", getParam())
            .append("program", getProgram())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
