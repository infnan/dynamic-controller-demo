package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.InterfaceGroup;

/**
 * 接口分组Service接口
 * 
 * @author ruoyi
 * @date 2023-10-31
 */
public interface IInterfaceGroupService 
{
    /**
     * 查询接口分组
     * 
     * @param id 接口分组主键
     * @return 接口分组
     */
    public InterfaceGroup selectInterfaceGroupById(Long id);

    /**
     * 查询接口分组列表
     * 
     * @param interfaceGroup 接口分组
     * @return 接口分组集合
     */
    public List<InterfaceGroup> selectInterfaceGroupList(InterfaceGroup interfaceGroup);

    /**
     * 新增接口分组
     * 
     * @param interfaceGroup 接口分组
     * @return 结果
     */
    public int insertInterfaceGroup(InterfaceGroup interfaceGroup);

    /**
     * 修改接口分组
     * 
     * @param interfaceGroup 接口分组
     * @return 结果
     */
    public int updateInterfaceGroup(InterfaceGroup interfaceGroup);

    /**
     * 删除接口分组信息
     * 
     * @param id 接口分组主键
     * @return 结果
     */
    public int deleteInterfaceGroupById(Long id);

    /**
     * 获取分组路径
     * @param groupId
     * @return
     */
    String getGroupPath(Long groupId);
}
