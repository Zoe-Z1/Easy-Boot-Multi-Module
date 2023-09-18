package com.easy.boot.admin.generate.controller;

import cn.hutool.core.collection.CollUtil;
import com.easy.boot.admin.generate.entity.DatabaseTable;
import com.easy.boot.admin.generate.entity.GenerateCode;
import com.easy.boot.admin.generate.entity.GeneratePreviewVO;
import com.easy.boot.admin.generate.entity.GenerateTableQuery;
import com.easy.boot.admin.generate.service.GenerateService;
import com.easy.boot.admin.operationLog.enums.OperateTypeEnum;
import com.easy.boot.common.base.BaseController;
import com.easy.boot.common.base.Page;
import com.easy.boot.common.base.Result;
import com.easy.boot.common.log.EasyLog;
import com.easy.boot.exception.GeneratorException;
import com.easy.boot.utils.FileUtil;
import com.easy.boot.utils.JsonUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zoe
 * @date 2023/9/7
 * @description
 */
@Slf4j
@Api(tags = "代码生成接口")
@Validated
@RestController
@RequestMapping("/admin/generate")
public class GenerateController extends BaseController {

    @Resource
    private GenerateService generateService;


    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "获取代码生成Table列表")
    @EasyLog(module = "获取代码生成Table列表", operateType = OperateTypeEnum.SELECT)
    @GetMapping(value = "/page")
    public Result<Page<DatabaseTable>> page(@Validated GenerateTableQuery query) {
        log.warn("aaaaaa");
        return Result.success(generateService.selectPage(query));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "批量重置代码生成配置")
    @EasyLog(module = "批量重置代码生成配置", operateType = OperateTypeEnum.DELETE)
    @PostMapping("/batchDel")
    public Result batchDel(@RequestBody List<String> tableNames) {
        generateService.deleteBatchByTableNames(tableNames);
        return Result.success();
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "代码生成预览")
    @EasyLog(module = "代码生成预览", operateType = OperateTypeEnum.SELECT)
    @PostMapping(value = "/preview/{tableName}")
    public Result<List<GeneratePreviewVO>> preview(@PathVariable String tableName) throws Exception {
        List<GenerateCode> codes = generateService.preview(tableName);
        return Result.success(JsonUtil.copyList(codes, GeneratePreviewVO.class));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "批量生成代码")
    @EasyLog(module = "批量生成代码", operateType = OperateTypeEnum.GENERATE)
    @PostMapping(value = "/batch/code")
    public void batchGenerateCode(@RequestBody List<String> tableNames) throws Exception {
        if (CollUtil.isEmpty(tableNames)) {
            throw new GeneratorException("要生成的表名不能为空");
        }
        List<GenerateCode> codes = new ArrayList<>();
        for (String tableName : tableNames) {
            codes.addAll(generateService.preview(tableName));
        }
        if (codes.isEmpty()) {
            return;
        }
        FileUtil.downloadZip(response, codes);
    }

}
