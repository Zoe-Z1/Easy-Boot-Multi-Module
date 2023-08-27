package com.easy.boot.admin.post.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.easy.boot.admin.operationLog.enums.OperateTypeEnum;
import com.easy.boot.admin.post.entity.Post;
import com.easy.boot.admin.post.entity.PostCreateDTO;
import com.easy.boot.admin.post.entity.PostQuery;
import com.easy.boot.admin.post.entity.PostUpdateDTO;
import com.easy.boot.admin.post.service.IPostService;
import com.easy.boot.common.base.BaseController;
import com.easy.boot.common.base.Result;
import com.easy.boot.common.excel.ImportExcelError;
import com.easy.boot.common.excel.ImportVO;
import com.easy.boot.common.excel.UploadDTO;
import com.easy.boot.common.excel.handler.ImportErrorCellWriteHandler;
import com.easy.boot.common.log.EasyLog;
import com.easy.boot.exception.FileException;
import com.easy.boot.utils.FileUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zoe
 * @date 2023/07/29
 * @description 岗位 前端控制器
 */
@Slf4j
@Api(tags = "岗位接口")
@RestController
@RequestMapping("/admin/post")
public class PostController extends BaseController {

    @Resource
    private IPostService postService;



    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "获取岗位列表")
    @EasyLog(module = "获取岗位列表", operateType = OperateTypeEnum.SELECT)
    @GetMapping("/page")
    public Result<IPage<Post>> page(@Validated PostQuery query) {
        return Result.success(postService.selectPage(query));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "获取岗位详情")
    @EasyLog(module = "获取岗位详情", operateType = OperateTypeEnum.SELECT)
    @GetMapping("/detail/{id}")
    public Result<Post> detail(@PathVariable Long id) {
        return Result.success(postService.detail(id));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "创建岗位")
    @EasyLog(module = "创建岗位", operateType = OperateTypeEnum.CREATE)
    @PostMapping(value = "/create")
    public Result create(@Validated @RequestBody PostCreateDTO dto) {
        return Result.r(postService.create(dto));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "编辑岗位")
    @EasyLog(module = "编辑岗位", operateType = OperateTypeEnum.UPDATE)
    @PostMapping(value = "/update")
    public Result update(@Validated @RequestBody PostUpdateDTO dto) {
        return Result.r(postService.updateById(dto));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "删除岗位")
    @EasyLog(module = "删除岗位", operateType = OperateTypeEnum.DELETE)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        return Result.r(postService.deleteById(id));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "批量删除岗位")
    @EasyLog(module = "批量删除岗位", operateType = OperateTypeEnum.DELETE)
    @PostMapping("/batchDel/{ids}")
    public Result batchDel(@PathVariable List<Long> ids) {
        return Result.r(postService.deleteBatchByIds(ids));
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "导入岗位")
    @EasyLog(module = "导入岗位", operateType = OperateTypeEnum.IMPORT)
    @PostMapping("/import")
    public Result<ImportVO> importExcel(UploadDTO dto) {
        Assert.notNull(dto.getFile(), "文件不能为空");
        try {
            List<Post> list = EasyExcel.read(dto.getFile().getInputStream())
                    .head(Post.class)
                    .excelType(FileUtil.getExcelType(dto.getFile()))
                    .sheet()
                    .doReadSync();
            List<ImportExcelError> errors = new ArrayList<>();
            List<Post> errorList = new ArrayList<>();
            // 导入Excel处理
            postService.importExcel(list, errorList, errors);
            String filePath = "";
            if (!errorList.isEmpty()) {
                // 将错误数据写到Excel文件
                filePath = FileUtil.getFullPath(easyFile.getFilePath(), "岗位导入错误信息");
                EasyExcel.write(filePath).head(Post.class)
                        .sheet().registerWriteHandler(new ImportErrorCellWriteHandler(errors))
                        .doWrite(errorList);
                filePath = FileUtil.getMapPath(filePath, easyFile.getFilePath(), easyFile.getFileMapPath());
            }
            ImportVO importVO = ImportVO.builder()
                    .count(list.size())
                    .errorCount(errorList.size())
                    .errorFilePath(filePath)
                    .build();
            return Result.success(importVO);
        } catch (IOException e) {
            log.error("导入Excel失败 e -> ", e);
            throw new FileException("导入Excel失败");
        }
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "导出岗位")
    @EasyLog(module = "导出岗位", operateType = OperateTypeEnum.EXPORT)
    @PostMapping("/export")
    public void exportExcel(@Validated @RequestBody PostQuery query) {
        String filePath = FileUtil.getFullPath(easyFile.getExcelPath(), "岗位");
        query.setPageNum(1);
        query.setPageSize(maxLimit);
        ExcelWriter build = EasyExcel.write(filePath, Post.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("岗位").build();
        while (true) {
            IPage<Post> page = postService.selectPage(query);
            build.write(page.getRecords(), writeSheet);
            if (page.getCurrent() >= page.getPages()) {
                break;
            }
            query.setPageNum(query.getPageNum() + 1);
        }
        build.finish();
        try {
            FileUtil.downloadAndDelete(filePath, response);
        } catch (IOException e) {
            log.error("导出Excel失败 e -> ", e);
            throw new FileException("导出Excel失败");
        }
    }

    @ApiOperationSupport(author = "zoe")
    @ApiOperation(value = "下载岗位导入模板")
    @EasyLog(module = "下载岗位导入模板", operateType = OperateTypeEnum.DOWNLOAD)
    @PostMapping("/download")
    public void downloadTemplate() {
        String filePath = FileUtil.getFullPath(easyFile.getExcelPath(), "岗位导入模板");
        EasyExcel.write(filePath, Post.class)
                .excludeColumnFieldNames(Collections.singletonList("createTime"))
                .sheet("岗位导入模板")
                .doWrite(new ArrayList<>());
        try {
            FileUtil.downloadAndDelete(filePath, response);
        } catch (IOException e) {
            log.error("下载岗位导入模板失败 e -> ", e);
            throw new FileException("下载岗位导入模板失败");
        }
    }
}
