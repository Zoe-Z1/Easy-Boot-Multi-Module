package com.easy.boot.common.generator.template;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.easy.boot.common.excel.ImportExcelError;
import com.easy.boot.common.generator.DataMap;
import com.easy.boot.common.generator.GenConstant;
import com.easy.boot.common.generator.config.GlobalConfig;
import com.easy.boot.common.generator.config.TemplateConfig;
import com.easy.boot.common.generator.db.MetaTable;
import lombok.*;

import java.util.*;

/**
 * @author zoe
 * @date 2023/8/15
 * @description 服务层模板配置
 */
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ServiceTemplate extends AbstractTemplate {

    private String remarks;

    private String moduleName;

    private Class<?> superClass;

    private String templateName;

    private String fileName;

    private Boolean enable;

    private Boolean isOverride;

    @Override
    protected String getRemarks(String tableRemarks) {
        if (StrUtil.isNotEmpty(remarks)) {
            return remarks;
        }
        return tableRemarks;
    }

    @Override
    protected String getModuleName() {
        if (StrUtil.isEmpty(moduleName)) {
            moduleName = GenConstant.MODULE_SERVICE;
        }
        return moduleName;
    }

    @Override
    protected Class<?> getSuperClass() {
        if (superClass == null) {
            return IService.class;
        } else {
            return superClass;
        }
    }

    @Override
    protected String getTemplateName() {
        return GenConstant.SERVICE_TEMPLATE_NAME;
    }

    @Override
    protected String getFileName(String javaName) {
        if (StrUtil.isNotEmpty(this.fileName)) {
            return this.fileName + GenConstant.SUFFIX;
        }
        return "I" + javaName + GenConstant.SERVICE + GenConstant.SUFFIX;
    }

    @Override
    public Boolean isEnable() {
        if (enable == null) {
            enable = true;
        }
        return enable;
    }

    @Override
    protected Boolean getIsOverride() {
        return isOverride;
    }

    @Override
    public DataMap buildDataMap(DataMap dataMap) {
        DataMap buildDataMap = super.buildDataMap(dataMap);
        // 构建类名
        buildClassName(buildDataMap);
        // 构建需要导入的包
        buildPkgDataMap(buildDataMap);
        return buildDataMap;
    }

    /**
     * 构建类名称
     * @param buildDataMap 已构建过的参数
     */
    private void buildClassName(DataMap buildDataMap) {
        TemplateConfig template = buildDataMap.getTemplateConfig();
        MetaTable metaTable = buildDataMap.getMetaTable();
        String javaName = metaTable.getBeanName();
        String className = getFileName(javaName).replace(GenConstant.SUFFIX, "");
        String entityName = template.getEntity().getFileName(javaName).replace(GenConstant.SUFFIX, "");
        String createDTOName = template.getCreateDTO().getFileName(javaName).replace(GenConstant.SUFFIX, "");
        String updateDTOName = template.getUpdateDTO().getFileName(javaName).replace(GenConstant.SUFFIX, "");
        String queryName = template.getQuery().getFileName(javaName).replace(GenConstant.SUFFIX, "");
        buildDataMap.put(GenConstant.DATA_MAP_KEY_CLASS_NAME, className);
        buildDataMap.put(GenConstant.DATA_MAP_KEY_ENTITY_NAME, entityName);
        buildDataMap.put(GenConstant.DATA_MAP_KEY_CREATE_DTO_NAME, createDTOName);
        buildDataMap.put(GenConstant.DATA_MAP_KEY_UPDATE_DTO_NAME, updateDTOName);
        buildDataMap.put(GenConstant.DATA_MAP_KEY_QUERY_NAME, queryName);
        if (getSuperClass() != null) {
            buildDataMap.put(GenConstant.DATA_MAP_KEY_SUPER_NAME, getSuperClass().getName());
        }
        if (template.getEnableImport()) {
            buildDataMap.put(GenConstant.DATA_MAP_KEY_IMPORT_EXCEL_ERROR_NAME, ImportExcelError.class.getSimpleName());
        }
    }

    /**
     * 构建代码生成需要导入的包
     * @param buildDataMap 已构建过的参数
     */
    private void buildPkgDataMap(DataMap buildDataMap) {
        GlobalConfig global = buildDataMap.getGlobalConfig();
        TemplateConfig template = buildDataMap.getTemplateConfig();
        MetaTable metaTable = (MetaTable) buildDataMap.get(GenConstant.DATA_MAP_KEY_TABLE);
        Set<String> pkgs = new HashSet<>();
        if (getSuperClass() != null) {
            pkgs.add(getSuperClass().getName());
        }
        if (template.getEnableImport()) {
            pkgs.add(ImportExcelError.class.getName());
        }
        pkgs.add(IPage.class.getName());
        pkgs.add(List.class.getName());
        String pkgName = String.join(".", global.getPackageName(), metaTable.getModuleName());
        String entityName = buildDataMap.getString(GenConstant.DATA_MAP_KEY_ENTITY_NAME);
        String createDTOName = buildDataMap.getString(GenConstant.DATA_MAP_KEY_CREATE_DTO_NAME);
        String updateDTOName = buildDataMap.getString(GenConstant.DATA_MAP_KEY_UPDATE_DTO_NAME);
        String queryName = buildDataMap.getString(GenConstant.DATA_MAP_KEY_QUERY_NAME);
        EntityTemplate entityTemplate = buildDataMap.getTemplateConfig().getEntity();
        CreateDTOTemplate createDTOTemplate = buildDataMap.getTemplateConfig().getCreateDTO();
        UpdateDTOTemplate updateDTOTemplate = buildDataMap.getTemplateConfig().getUpdateDTO();
        QueryTemplate queryTemplate = buildDataMap.getTemplateConfig().getQuery();
        String entityPkgTemplate = String.join(".", pkgName, entityTemplate.getModuleName(), entityName);
        String createDTOPkgName = String.join(".", pkgName, createDTOTemplate.getModuleName(), createDTOName);
        String updateDTOPkgName = String.join(".", pkgName, updateDTOTemplate.getModuleName(), updateDTOName);
        String queryPkgName = String.join(".", pkgName, queryTemplate.getModuleName(), queryName);
        pkgs.add(entityPkgTemplate);
        pkgs.add(createDTOPkgName);
        pkgs.add(updateDTOPkgName);
        pkgs.add(queryPkgName);

        List<String> list = new ArrayList<>(pkgs);
        Collections.sort(list);
        buildDataMap.put(GenConstant.DATA_MAP_KEY_PKGS, list);
    }
}
