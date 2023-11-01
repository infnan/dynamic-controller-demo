package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.InterfaceGroup;

/**
 * 接口分组Mapper接口
 * 
 * @author ruoyi
 * @date 2023-10-31
 */
public interface InterfaceGroupMapper 
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
     * 删除接口分组
     * 
     * @param id 接口分组主键
     * @return 结果
     */
    public int deleteInterfaceGroupById(Long id);

    /**
     * 批量删除接口分组
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteInterfaceGroupByIds(Long[] ids);
}
