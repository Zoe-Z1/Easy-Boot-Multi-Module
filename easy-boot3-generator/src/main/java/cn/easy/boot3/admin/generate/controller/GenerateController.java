package cn.easy.boot3.admin.generate.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.easy.boot3.admin.generate.entity.GeneratePreviewVO;
import cn.easy.boot3.admin.generate.service.GenerateService;
import cn.hutool.core.collection.CollUtil;
import cn.easy.boot3.admin.generate.entity.DatabaseTable;
import cn.easy.boot3.admin.generate.entity.GenerateCode;
import cn.easy.boot3.admin.generate.entity.GenerateTableQuery;
import cn.easy.boot3.core.base.BaseController;
import cn.easy.boot3.core.base.Page;
import cn.easy.boot3.core.base.Result;
import cn.easy.boot3.core.exception.GeneratorException;
import cn.easy.boot3.core.log.EasyLog;
import cn.easy.boot3.core.log.enums.OperateTypeEnum;
import cn.easy.boot3.core.noRepeatSubmit.EasyNoRepeatSubmit;
import cn.easy.boot3.core.utils.JsonUtil;
import cn.easy.boot3.util.FileUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zoe
 * @date 2023/9/7
 * @description
 */
@Slf4j
@Tag(name = "代码生成接口")
@Validated
@RestController
@RequestMapping("/admin/generate")
public class GenerateController extends BaseController {

    @Resource
    private GenerateService generateService;


    @SaCheckPermission(value = "dev:gen:page")
    @ApiOperationSupport(author = "zoe")
    @Operation(summary = "分页获取代码生成Table列表")
    @EasyLog(module = "分页获取代码生成Table列表", operateType = OperateTypeEnum.SELECT)
    @GetMapping(value = "/page")
    public Result<Page<DatabaseTable>> page(@Validated GenerateTableQuery query) {
        return Result.success(generateService.selectPage(query));
    }

    @SaCheckPermission(value = "dev:gen:batch:del")
    @ApiOperationSupport(author = "zoe")
    @Operation(summary = "批量重置代码生成配置")
    @EasyLog(module = "批量重置代码生成配置", operateType = OperateTypeEnum.DELETE)
    @PostMapping("/batchDel")
    public Result batchDel(@RequestBody List<String> tableNames) {
        generateService.deleteBatchByTableNames(tableNames);
        return Result.success();
    }

    @SaCheckPermission(value = "dev:gen:preview")
    @ApiOperationSupport(author = "zoe")
    @Operation(summary = "代码生成预览")
    @EasyLog(module = "代码生成预览", operateType = OperateTypeEnum.SELECT)
    @PostMapping(value = "/preview/{tableName}")
    public Result<List<GeneratePreviewVO>> preview(@PathVariable String tableName) throws Exception {
        List<GenerateCode> codes = generateService.preview(tableName);
        return Result.success(JsonUtil.copyList(codes, GeneratePreviewVO.class));
    }

    @EasyNoRepeatSubmit
    @SaCheckPermission(value = "dev:gen:batch:code")
    @ApiOperationSupport(author = "zoe")
    @Operation(summary = "批量生成代码")
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
        generateService.runSql(tableNames, codes);
        FileUtil.downloadZip(response, codes);
    }

}
