package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.InterfaceItem;
import com.ruoyi.system.mapper.InterfaceItemMapper;
import com.ruoyi.system.service.IInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.InterfaceGroupMapper;
import com.ruoyi.system.domain.InterfaceGroup;
import com.ruoyi.system.service.IInterfaceGroupService;

import javax.annotation.Resource;

/**
 * 接口分组Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-10-31
 */
@Service
public class InterfaceGroupServiceImpl implements IInterfaceGroupService 
{
    @Autowired
    private InterfaceGroupMapper interfaceGroupMapper;

    @Autowired
    private InterfaceItemMapper interfaceItemMapper;

    @Autowired
    private IInterfaceService interfaceService;

    /**
     * 查询接口分组
     * 
     * @param id 接口分组主键
     * @return 接口分组
     */
    @Override
    public InterfaceGroup selectInterfaceGroupById(Long id)
    {
        return interfaceGroupMapper.selectInterfaceGroupById(id);
    }

    /**
     * 查询接口分组列表
     * 
     * @param interfaceGroup 接口分组
     * @return 接口分组
     */
    @Override
    public List<InterfaceGroup> selectInterfaceGroupList(InterfaceGroup interfaceGroup)
    {
        return interfaceGroupMapper.selectInterfaceGroupList(interfaceGroup);
    }

    /**
     * 新增接口分组
     * 
     * @param interfaceGroup 接口分组
     * @return 结果
     */
    @Override
    public int insertInterfaceGroup(InterfaceGroup interfaceGroup)
    {
        // 校验是否有重复的group code
        InterfaceGroup vo = new InterfaceGroup();
        vo.setCode(interfaceGroup.getCode());
        vo.setParentId(interfaceGroup.getParentId());
        List<InterfaceGroup> list = interfaceGroupMapper.selectInterfaceGroupList(vo);
        if (!list.isEmpty()) {
            throw new ServiceException("分组代码重复");
        }

        interfaceGroup.setCreateTime(DateUtils.getNowDate());
        interfaceGroup.setUpdateTime(DateUtils.getNowDate());
        return interfaceGroupMapper.insertInterfaceGroup(interfaceGroup);
    }

    /**
     * 修改接口分组
     * 
     * @param interfaceGroup 接口分组
     * @return 结果
     */
    @Override
    public int updateInterfaceGroup(InterfaceGroup interfaceGroup)
    {
        interfaceGroup.setUpdateTime(DateUtils.getNowDate());
        return interfaceGroupMapper.updateInterfaceGroup(interfaceGroup);
    }

    /**
     * 删除接口分组信息
     * 
     * @param id 接口分组主键
     * @return 结果
     */
    @Override
    public int deleteInterfaceGroupById(Long id)
    {
        // 校验该分组下面是否有接口
        InterfaceGroup groupDO = interfaceGroupMapper.selectInterfaceGroupById(id);
        if (groupDO == null) {
            throw new ServiceException("接口分组不存在");
        }

        InterfaceItem vo = new InterfaceItem();
        vo.setGroupId(id);
        List<InterfaceItem> list = interfaceItemMapper.selectInterfaceItemList(vo);
        if (!list.isEmpty()) {
            throw new ServiceException("该分组中存在接口，请先删除接口再删除分组");
        }

        // 清理缓存
        String groupPath = getGroupPath(id);
        interfaceService.cleanCache(groupPath, "");

        return interfaceGroupMapper.deleteInterfaceGroupById(id);
    }

    /**
     * 获取分组路径
     * @param groupId
     * @return
     */
    @Override
    public String getGroupPath(Long groupId) {
        if (groupId == null) {
            return "";
        }
        List<InterfaceGroup> groupList = interfaceGroupMapper.selectInterfaceGroupList(new InterfaceGroup());
        Map<Long, InterfaceGroup> groupMap = groupList.stream().collect(Collectors.toMap(InterfaceGroup::getId, Function.identity()));

        Stack<String> path = new Stack<>();
        Set<Long> groupSet = new HashSet<>();

        Long id = groupId;
        while (id != null && !groupSet.contains(id)) {
            InterfaceGroup g = groupMap.get(id);
            groupSet.add(id);
            if (g != null) {
                path.push(g.getCode());
                id = g.getParentId();
            } else {
                id = null;
            }
        }

        List<String> pathList = new ArrayList<>();
        while (!path.isEmpty()) {
            pathList.add(path.pop());
        }
        return String.join("/", pathList);
    }
}
