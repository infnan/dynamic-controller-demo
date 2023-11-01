package com.ruoyi.web.controller.dyninterface;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.InterfaceGroup;
import com.ruoyi.system.service.IInterfaceGroupService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 接口分组Controller
 * 
 * @author ruoyi
 * @date 2023-10-31
 */
@RestController
@RequestMapping("/system/interfacegroup")
public class InterfaceGroupController extends BaseController
{
    @Autowired
    private IInterfaceGroupService interfaceGroupService;

    /**
     * 查询接口分组列表
     */
    @PreAuthorize("@ss.hasPermi('system:interfacegroup:list')")
    @GetMapping("/list")
    public AjaxResult list(InterfaceGroup interfaceGroup)
    {
        List<InterfaceGroup> list = interfaceGroupService.selectInterfaceGroupList(interfaceGroup);
        return success(list);
    }

    /**
     * 导出接口分组列表
     */
    @PreAuthorize("@ss.hasPermi('system:interfacegroup:export')")
    @Log(title = "接口分组", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, InterfaceGroup interfaceGroup)
    {
        List<InterfaceGroup> list = interfaceGroupService.selectInterfaceGroupList(interfaceGroup);
        ExcelUtil<InterfaceGroup> util = new ExcelUtil<InterfaceGroup>(InterfaceGroup.class);
        util.exportExcel(response, list, "接口分组数据");
    }

    /**
     * 获取接口分组详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:interfacegroup:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(interfaceGroupService.selectInterfaceGroupById(id));
    }

    /**
     * 新增接口分组
     */
    @PreAuthorize("@ss.hasPermi('system:interfacegroup:add')")
    @Log(title = "接口分组", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody InterfaceGroup interfaceGroup)
    {
        return toAjax(interfaceGroupService.insertInterfaceGroup(interfaceGroup));
    }

    /**
     * 修改接口分组
     */
    @PreAuthorize("@ss.hasPermi('system:interfacegroup:edit')")
    @Log(title = "接口分组", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody InterfaceGroup interfaceGroup)
    {
        return toAjax(interfaceGroupService.updateInterfaceGroup(interfaceGroup));
    }

    /**
     * 删除接口分组
     */
    @PreAuthorize("@ss.hasPermi('system:interfacegroup:remove')")
    @Log(title = "接口分组", businessType = BusinessType.DELETE)
	@DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(interfaceGroupService.deleteInterfaceGroupById(id));
    }
}
