package com.ruoyi.web.controller.dyninterface;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IInterfaceService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/webapi")
@Validated
public class InterfaceController extends BaseController {

    @Resource
    private IInterfaceService interfaceService;

    @GetMapping("/{code}")
    @ResponseBody
    public AjaxResult processGet(@PathVariable("code") String code, @RequestParam Map<String, Object> paramMap) {
        return invoke("", "", code, paramMap, "GET");
    }

    @GetMapping("/{group}/{code}")
    @ResponseBody
    public AjaxResult processGet(@PathVariable("group") String group, @PathVariable("code") String code, @RequestParam Map<String, Object> paramMap) {
        return invoke(group, "", code, paramMap, "GET");
    }

    @GetMapping("/{group}/{subgroup}/{code}")
    @ResponseBody
    public AjaxResult processGet(@PathVariable("group") String group, @PathVariable("subgroup") String subgroup, @PathVariable("code") String code, @RequestParam Map<String, Object> paramMap) {
        return invoke(group, subgroup, code, paramMap, "GET");
    }

    @GetMapping("/{group}/{subgroup}/{subgroup2}/{code}")
    @ResponseBody
    public AjaxResult processGet(@PathVariable("group") String group, @PathVariable("subgroup") String subgroup, @PathVariable("subgroup2") String subgroup2, @PathVariable("code") String code, @RequestParam Map<String, Object> paramMap) {
        return invoke(group, subgroup + "/" + subgroup2, code, paramMap, "GET");
    }

    @PostMapping(value = "/{code}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public AjaxResult processPost(@PathVariable("code") String code, @RequestBody JSONObject body) {
        Map<String, Object> params = new HashMap<>();
        for (String key : body.keySet()) {
            params.put(key, body.get(key));
        }
        return invoke("", "", code, params, "POST+json");
    }

    @PostMapping(value = "/{code}",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public AjaxResult processPost(@PathVariable("code") String code, @RequestParam Map<String, Object> params) {
        return invoke("", "", code, params, "POST+form");
    }

    @PostMapping(value = "/{group}/{code}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public AjaxResult processPost(@PathVariable("group") String group, @PathVariable("code") String code, @RequestBody JSONObject body) {
        Map<String, Object> params = new HashMap<>();
        for (String key : body.keySet()) {
            params.put(key, body.get(key));
        }
        return invoke(group, "", code, params, "POST+json");
    }

    @PostMapping(value = "/{group}/{code}",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public AjaxResult processPost(@PathVariable("group") String group, @PathVariable("code") String code, @RequestParam Map<String, Object> params) {
        return invoke(group, "", code, params, "POST+form");
    }

    @PostMapping(value = "/{group}/{subgroup}/{code}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public AjaxResult processPost(@PathVariable("group") String group, @PathVariable("subgroup") String subgroup, @PathVariable("code") String code, @RequestBody JSONObject body) {
        Map<String, Object> params = new HashMap<>();
        for (String key : body.keySet()) {
            params.put(key, body.get(key));
        }
        return invoke(group, subgroup, code, params, "POST+json");
    }

    @PostMapping(value = "/{group}/{subgroup}/{code}",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public AjaxResult processPost(@PathVariable("group") String group, @PathVariable("subgroup") String subgroup, @PathVariable("code") String code, @RequestParam Map<String, Object> params) {
        return invoke(group, subgroup, code, params, "POST+form");
    }

    @PostMapping(value = "/{group}/{subgroup}/{subgroup2}/{code}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public AjaxResult processPost(@PathVariable("group") String group, @PathVariable("subgroup") String subgroup, @PathVariable("subgroup2") String subgroup2, @PathVariable("code") String code, @RequestBody JSONObject body) {
        Map<String, Object> params = new HashMap<>();
        for (String key : body.keySet()) {
            params.put(key, body.get(key));
        }
        return invoke(group, subgroup + "/" + subgroup2, code, params, "POST+json");
    }

    @PostMapping(value = "/{group}/{subgroup}/{subgroup2}/{code}",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public AjaxResult processPost(@PathVariable("group") String group, @PathVariable("subgroup") String subgroup, @PathVariable("subgroup2") String subgroup2, @PathVariable("code") String code, @RequestParam Map<String, Object> params) {
        return invoke(group, subgroup + "/" + subgroup2, code, params, "POST+form");
    }

    private AjaxResult invoke(String group, String subgroup, String code, Map<String, Object> params, String method) {
        Object result = interfaceService.invokeInterface(group, subgroup, code, params, method);
        return success(result);
    }

}
