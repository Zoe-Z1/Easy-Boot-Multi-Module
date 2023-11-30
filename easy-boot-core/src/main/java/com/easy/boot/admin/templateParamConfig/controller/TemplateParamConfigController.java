package com.easy.boot.admin.templateParamConfig.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.easy.boot.core.log.enums.OperateTypeEnum;
import com.easy.boot.admin.templateParamConfig.entity.TemplateParamConfig;
import com.easy.boot.admin.templateParamConfig.entity.TemplateParamConfigCreateDTO;
import com.easy.boot.admin.templateParamConfig.entity.TemplateParamConfigQuery;
import com.easy.boot.admin.templateParamConfig.entity.TemplateParamConfigUpdateDTO;
import com.easy.boot.admin.templateParamConfig.service.ITemplateParamConfigService;
import com.easy.boot.core.base.BaseController;
import com.easy.boot.core.base.Result;
import com.easy.boot.core.log.EasyLog;
import com.easy.boot.core.noRepeatSubmit.EasyNoRepeatSubmit;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zoe
 * @date 2023/11/09
 * @description 模板参数配置接口
 */
@Slf4j
@Api(tags = "模板参数配置接口")
@RestController
@RequestMapping("/admin/templateParamConfig")
public class TemplateParamConfigController extends BaseController {

    @Resource
    private ITemplateParamConfigService templateParamConfigService;


    @SaCheckPermission(value = "system:template:param:config:page")
    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "分页获取模板参数配置列表")
    @EasyLog(module = "分页获取模板参数配置列表", operateType = OperateTypeEnum.SELECT)
    @GetMapping("/page")
    public Result<IPage<TemplateParamConfig>> page(@Validated TemplateParamConfigQuery query) {
        return Result.success(templateParamConfigService.selectPage(query));
    }

    @EasyNoRepeatSubmit
    @SaCheckPermission(value = "system:template:param:config:create")
    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "创建模板参数配置")
    @EasyLog(module = "创建模板参数配置", operateType = OperateTypeEnum.CREATE)
    @PostMapping(value = "/create")
    public Result create(@Validated @RequestBody TemplateParamConfigCreateDTO dto) {
        return Result.r(templateParamConfigService.create(dto));
    }

    @SaCheckPermission(value = "system:template:param:config:update")
    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "编辑模板参数配置")
    @EasyLog(module = "编辑模板参数配置", operateType = OperateTypeEnum.UPDATE)
    @PostMapping(value = "/update")
    public Result update(@Validated @RequestBody TemplateParamConfigUpdateDTO dto) {
        return Result.r(templateParamConfigService.updateById(dto));
    }

    @SaCheckPermission(value = "system:template:param:config:del")
    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "删除模板参数配置")
    @EasyLog(module = "删除模板参数配置", operateType = OperateTypeEnum.DELETE)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        return Result.r(templateParamConfigService.deleteById(id));
    }

    @SaCheckPermission(value = "system:template:param:config:batch:del")
    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "批量删除模板参数配置")
    @EasyLog(module = "批量删除模板参数配置", operateType = OperateTypeEnum.DELETE)
    @PostMapping("/batchDel")
    public Result batchDel(@RequestBody List<Long> ids) {
        return Result.r(templateParamConfigService.deleteBatchByIds(ids));
    }

}