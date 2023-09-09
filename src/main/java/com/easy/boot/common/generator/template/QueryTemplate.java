package com.easy.boot.common.generator.template;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easy.boot.common.base.BasePageQuery;
import com.easy.boot.common.generator.DataMap;
import com.easy.boot.common.generator.GenConstant;
import com.easy.boot.common.generator.config.AnnotationConfig;
import com.easy.boot.common.generator.config.FilterConfig;
import com.easy.boot.common.generator.db.Field;
import com.easy.boot.common.generator.db.MetaTable;
import com.easy.boot.utils.JsonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zoe
 * @date 2023/8/26
 * @description Query模板配置
 */
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class QueryTemplate extends AbstractTemplate {

    private String remarks;

    private String moduleName;

    private Class<?> superClass;

    private String templateName;

    private String fileName;

    private Boolean enable;

    private Boolean isOverride;

    /**
     * 是否生成@TableField
     */
    private Boolean enableTableField;

    /**
     * 忽略父类已存在的属性
     * 开启此配置后，父类存在的属性，子类不再生成
     */
    private Boolean enableExcludeSuperField;

    @Override
    protected String getRemarks(String tableRemarks) {
        if (StrUtil.isNotEmpty(remarks)) {
            return remarks;
        }
        return tableRemarks + "查询";
    }

    @Override
    protected String getModuleName() {
        if (StrUtil.isEmpty(moduleName)) {
            moduleName = GenConstant.MODULE_ENTITY;
        }
        return moduleName;
    }

    @Override
    protected Class<?> getSuperClass() {
        if (superClass == null) {
            superClass = BasePageQuery.class;
        }
        return superClass;
    }

    @Override
    protected String getTemplateName() {
        return GenConstant.ENTITY_TEMPLATE_NAME;
    }

    @Override
    protected String getFileName(String javaName) {
        if (StrUtil.isNotEmpty(this.fileName)) {
            return this.fileName + GenConstant.SUFFIX;
        }
        return javaName + GenConstant.QUERY + GenConstant.SUFFIX;
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

    public Boolean getEnableTableField() {
        if (enableTableField == null) {
            enableTableField = false;
        }
        return enableTableField;
    }

    public Boolean getEnableExcludeSuperField() {
        if (enableExcludeSuperField == null) {
            enableExcludeSuperField = true;
        }
        return enableExcludeSuperField;
    }

    @Override
    public DataMap buildDataMap(DataMap dataMap) {
        DataMap buildDataMap = super.buildDataMap(dataMap);
        // 构建类名
        buildClassName(buildDataMap);
        // 处理实体类属性
        handleField(buildDataMap);
        // 构建需要导入的包
        buildPkgDataMap(buildDataMap);
        return buildDataMap;
    }

    /**
     * 构建类名称
     * @param buildDataMap 已构建过的参数
     */
    private void buildClassName(DataMap buildDataMap) {
        MetaTable metaTable = buildDataMap.getMetaTable();
        String javaName = metaTable.getBeanName();
        String className = getFileName(javaName).replace(GenConstant.SUFFIX, "");
        buildDataMap.put(GenConstant.DATA_MAP_KEY_CLASS_NAME, className);
        if (getSuperClass() != null) {
            buildDataMap.put(GenConstant.DATA_MAP_KEY_SUPER_NAME, getSuperClass().getSimpleName());
        }
    }

    /**
     * 处理实体类属性
     * @param buildDataMap 已构建过的参数
     */
    private void handleField(DataMap buildDataMap) {
        MetaTable metaTable = buildDataMap.getMetaTable();
        FilterConfig filter = buildDataMap.getFilterConfig();
        List<Field> fields = JsonUtil.copyList(metaTable.getFields(), Field.class);
        fields.removeIf(item -> filter.getExcludeField().contains(item.getJavaName()));
        Class<?> clazz = getSuperClass();
        if (getEnableExcludeSuperField() && clazz != null) {
            java.lang.reflect.Field[] superFields = clazz.getDeclaredFields();
            Set<String> superFieldSet = Arrays.stream(superFields).map(java.lang.reflect.Field::getName).collect(Collectors.toSet());
            fields.removeIf(item -> superFieldSet.contains(item.getJavaName()));
        }
        buildDataMap.put(GenConstant.DATA_MAP_KEY_FIELDS, fields);
        buildDataMap.put(GenConstant.DATA_MAP_KEY_ENABLE_TABLE_FIELD, getEnableTableField());
    }

    /**
     * 构建代码生成需要导入的包
     * @param buildDataMap 已构建过的参数
     */
    private void buildPkgDataMap(DataMap buildDataMap) {
        AnnotationConfig annotation = buildDataMap.getAnnotationConfig();
        MetaTable metaTable = buildDataMap.getMetaTable();
        Set<String> pkgs = new HashSet<>();
        if (getSuperClass() != null) {
            pkgs.add(getSuperClass().getName());
        }
        if (annotation.getEnableBuilder()) {
            pkgs.add(SuperBuilder.class.getName());
        }
        pkgs.add(TableName.class.getName());
        pkgs.add(ApiModel.class.getName());
        pkgs.add(ApiModelProperty.class.getName());
        pkgs.addAll(metaTable.getFields().stream().map(Field::getJavaTypePackageName).collect(Collectors.toSet()));
        pkgs.add("lombok.*");
        List<String> list = new ArrayList<>(pkgs);
        Collections.sort(list);
        buildDataMap.put(GenConstant.DATA_MAP_KEY_PKGS, list);
    }
}
