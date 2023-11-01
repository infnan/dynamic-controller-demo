package com.ruoyi.web.controller.dyninterface;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.ruoyi.system.domain.vo.InterfaceDebugVO;
import com.ruoyi.system.service.IInterfaceService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.InterfaceItem;
import com.ruoyi.system.service.IInterfaceItemService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 接口项目Controller
 * 
 * @author ruoyi
 * @date 2023-10-31
 */
@RestController
@RequestMapping("/system/interfaceitem")
public class InterfaceItemController extends BaseController
{
    @Autowired
    private IInterfaceItemService interfaceItemService;

    @Autowired
    private IInterfaceService interfaceService;

    /**
     * 查询接口项目列表
     */
    @PreAuthorize("@ss.hasPermi('system:interfaceitem:list')")
    @GetMapping("/list")
    public TableDataInfo list(InterfaceItem interfaceItem)
    {
        startPage();
        List<InterfaceItem> list = interfaceItemService.selectInterfaceItemList(interfaceItem);
        return getDataTable(list);
    }

    /**
     * 导出接口项目列表
     */
    @PreAuthorize("@ss.hasPermi('system:interfaceitem:export')")
    @Log(title = "接口项目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, InterfaceItem interfaceItem)
    {
        List<InterfaceItem> list = interfaceItemService.selectInterfaceItemList(interfaceItem);
        ExcelUtil<InterfaceItem> util = new ExcelUtil<InterfaceItem>(InterfaceItem.class);
        util.exportExcel(response, list, "接口项目数据");
    }

    /**
     * 获取接口项目详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:interfaceitem:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(interfaceItemService.selectInterfaceItemById(id));
    }

    /**
     * 新增接口项目
     */
    @PreAuthorize("@ss.hasPermi('system:interfaceitem:add')")
    @Log(title = "接口项目", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody InterfaceItem interfaceItem)
    {
        return toAjax(interfaceItemService.insertInterfaceItem(interfaceItem));
    }

    /**
     * 修改接口项目
     */
    @PreAuthorize("@ss.hasPermi('system:interfaceitem:edit')")
    @Log(title = "接口项目", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody InterfaceItem interfaceItem)
    {
        return toAjax(interfaceItemService.updateInterfaceItem(interfaceItem));
    }

    /**
     * 删除接口项目
     */
    @PreAuthorize("@ss.hasPermi('system:interfaceitem:remove')")
    @Log(title = "接口项目", businessType = BusinessType.DELETE)
	@DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(interfaceItemService.deleteInterfaceItemById(id));
    }

    @PostMapping(
            value = "/debug",
            produces = {MediaType.TEXT_PLAIN_VALUE}
    )
    @ApiOperation("接口调试")
    @PreAuthorize("@ss.hasPermi('system:interfaceitem:edit')")
    @ResponseBody
    public String debug(@Valid @RequestBody InterfaceDebugVO debugVO) {
        return interfaceService.debugInterface(debugVO);
    }
}
