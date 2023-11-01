package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.service.IInterfaceGroupService;
import com.ruoyi.system.service.IInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.InterfaceItemMapper;
import com.ruoyi.system.domain.InterfaceItem;
import com.ruoyi.system.service.IInterfaceItemService;

/**
 * 接口项目Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-10-31
 */
@Service
public class InterfaceItemServiceImpl implements IInterfaceItemService 
{
    @Autowired
    private InterfaceItemMapper interfaceItemMapper;

    @Autowired
    private IInterfaceGroupService interfaceGroupService;

    @Autowired
    private IInterfaceService interfaceService;

    /**
     * 查询接口项目
     * 
     * @param id 接口项目主键
     * @return 接口项目
     */
    @Override
    public InterfaceItem selectInterfaceItemById(Long id)
    {
        return interfaceItemMapper.selectInterfaceItemById(id);
    }

    /**
     * 查询接口项目列表
     * 
     * @param interfaceItem 接口项目
     * @return 接口项目
     */
    @Override
    public List<InterfaceItem> selectInterfaceItemList(InterfaceItem interfaceItem)
    {
        return interfaceItemMapper.selectInterfaceItemList(interfaceItem);
    }

    /**
     * 新增接口项目
     * 
     * @param interfaceItem 接口项目
     * @return 结果
     */
    @Override
    public int insertInterfaceItem(InterfaceItem interfaceItem)
    {
        // 判断是否重复
        InterfaceItem vo = new InterfaceItem();
        vo.setCode(interfaceItem.getCode());
        vo.setGroupId(interfaceItem.getGroupId());
        List<InterfaceItem> list = interfaceItemMapper.selectInterfaceItemList(vo);
        if (list.size() > 0) {
            throw new ServiceException("已存在同名接口，请更换名称");
        }

        // 插入
        interfaceItem.setGroupCode(interfaceGroupService.getGroupPath(interfaceItem.getGroupId()));
        interfaceItem.setCreateTime(DateUtils.getNowDate());
        interfaceItem.setUpdateTime(DateUtils.getNowDate());
        return interfaceItemMapper.insertInterfaceItem(interfaceItem);
    }

    /**
     * 修改接口项目
     * 
     * @param interfaceItem 接口项目
     * @return 结果
     */
    @Override
    public int updateInterfaceItem(InterfaceItem interfaceItem)
    {
        // 判断是否重复
        InterfaceItem vo = new InterfaceItem();
        vo.setCode(interfaceItem.getCode());
        vo.setGroupId(interfaceItem.getGroupId());
        List<InterfaceItem> list = interfaceItemMapper.selectInterfaceItemList(vo)
                .stream().filter(item -> !item.getId().equals(interfaceItem.getId()))
                .collect(Collectors.toList());
        if (list.size() > 0) {
            throw new ServiceException("已存在同名接口，请更换名称");
        }

        // 校验存在，如存在则清空缓存
        InterfaceItem oldObj = interfaceItemMapper.selectInterfaceItemById(interfaceItem.getId());
        if (oldObj != null) {
            interfaceService.cleanCache(oldObj.getGroupCode(), oldObj.getCode());
        }

        // 更新
        interfaceItem.setGroupCode(interfaceGroupService.getGroupPath(interfaceItem.getGroupId()));

        // 更新
        interfaceItem.setUpdateTime(DateUtils.getNowDate());
        return interfaceItemMapper.updateInterfaceItem(interfaceItem);
    }

    /**
     * 删除接口项目信息
     * 
     * @param id 接口项目主键
     * @return 结果
     */
    @Override
    public int deleteInterfaceItemById(Long id)
    {
        // 清除缓存
        InterfaceItem oldObj = interfaceItemMapper.selectInterfaceItemById(id);
        if (oldObj != null) {
            interfaceService.cleanCache(oldObj.getGroupCode(), oldObj.getCode());
        }

        return interfaceItemMapper.deleteInterfaceItemById(id);
    }
}
