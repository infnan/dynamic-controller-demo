package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.InterfaceItem;

/**
 * 接口项目Service接口
 * 
 * @author ruoyi
 * @date 2023-10-31
 */
public interface IInterfaceItemService 
{
    /**
     * 查询接口项目
     * 
     * @param id 接口项目主键
     * @return 接口项目
     */
    public InterfaceItem selectInterfaceItemById(Long id);

    /**
     * 查询接口项目列表
     * 
     * @param interfaceItem 接口项目
     * @return 接口项目集合
     */
    public List<InterfaceItem> selectInterfaceItemList(InterfaceItem interfaceItem);

    /**
     * 新增接口项目
     * 
     * @param interfaceItem 接口项目
     * @return 结果
     */
    public int insertInterfaceItem(InterfaceItem interfaceItem);

    /**
     * 修改接口项目
     * 
     * @param interfaceItem 接口项目
     * @return 结果
     */
    public int updateInterfaceItem(InterfaceItem interfaceItem);

    /**
     * 删除接口项目信息
     * 
     * @param id 接口项目主键
     * @return 结果
     */
    public int deleteInterfaceItemById(Long id);
}
