package com.ruoyi.system.service;

import com.ruoyi.system.domain.InterfaceItem;
import com.ruoyi.system.domain.vo.InterfaceDebugVO;

import java.util.Map;

public interface IInterfaceService {
    void initLocalCache();

    Object invokeInterface(String group, String subgroup, String code, Map<String, Object> paramMap, String method);

    InterfaceItem getInterfaceInfo(String group, String subgroup, String code);

    void cleanCache(String groupPath, String code);

    String debugInterface(InterfaceDebugVO debugVO);
}
