package com.easy.boot.common.generator.template;

import cn.hutool.core.util.StrUtil;
import com.easy.boot.common.base.BaseEntity;
import com.easy.boot.common.generator.DataMap;
import com.easy.boot.common.generator.GenConstant;
import com.easy.boot.common.generator.config.AnnotationConfig;
import com.easy.boot.common.generator.config.GeneratorConfig;
import com.easy.boot.common.generator.config.GlobalConfig;
import com.easy.boot.common.generator.config.TemplateConfig;
import com.easy.boot.common.generator.db.MetaTable;
import lombok.*;

import java.util.*;

/**
 * @author zoe
 * @date 2023/8/15
 * @description 实体类模板配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EntityTemplate extends AbstractTemplate {

    private String moduleName;

    public Class<?> superClass;

    public String templateName;

    public String fileName;

    public Boolean enable;

    public Boolean isOverride;

    @Override
    protected String getModuleName() {
        if (StrUtil.isEmpty(moduleName)) {
            moduleName = "entity";
        }
        return moduleName;
    }

    @Override
    public Class<?> getSuperClass() {
        return BaseEntity.class;
    }

    @Override
    public String getTemplateName() {
        return "entity.ftl";
    }

    @Override
    protected String getFileName(String javaName) {
        if (StrUtil.isNotEmpty(this.fileName)) {
            return this.fileName + GenConstant.SUFFIX;
        }
        return javaName + GenConstant.SUFFIX;
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
        dataMap =  super.buildDataMap(dataMap);
        // 构建类名
        buildClassName(dataMap);
        // 构建父类名
        buildSuperClassName(dataMap);
        // 构建需要导入的包
        buildPkgDataMap(dataMap);
        return dataMap;
    }

    /**
     * 构建类名称
     * @param buildDataMap 已构建过的参数
     */
    private void buildClassName(DataMap buildDataMap) {
        GeneratorConfig generator = (GeneratorConfig) buildDataMap.get(GenConstant.DATA_MAP_KEY_CONFIG);
        MetaTable metaTable = (MetaTable) buildDataMap.get(GenConstant.DATA_MAP_KEY_TABLE);
        String javaName = metaTable.getBeanName();
        String className = generator.getTemplateConfig().getEntity().getFileName(javaName).replace(GenConstant.SUFFIX, "");
        buildDataMap.put("className", className);
    }

    /**
     * 构建父类名称
     * @param buildDataMap 已构建过的参数
     */
    private void buildSuperClassName(DataMap buildDataMap) {
        GeneratorConfig generator = (GeneratorConfig) buildDataMap.get(GenConstant.DATA_MAP_KEY_CONFIG);
        TemplateConfig template = generator.getTemplateConfig();
        if (template.getEntity().superClass != null) {
            buildDataMap.put("superName", template.getEntity().getSuperClass().getName());
        }
    }

    /**
     * 构建代码生成需要导入的包
     * @param buildDataMap 已构建过的参数
     */
    private void buildPkgDataMap(DataMap buildDataMap) {
        GeneratorConfig generator = (GeneratorConfig) buildDataMap.get(GenConstant.DATA_MAP_KEY_CONFIG);
        GlobalConfig global = generator.getGlobalConfig();
        AnnotationConfig annotation = generator.getAnnotationConfig();
        TemplateConfig template = generator.getTemplateConfig();
        MetaTable metaTable = (MetaTable) buildDataMap.get(GenConstant.DATA_MAP_KEY_TABLE);
        String pkg = global.getPackageName() + "." + metaTable.getModuleName();
        Set<String> pkgs = new HashSet<>();
        if (template.getEntity().superClass != null) {
            pkgs.add(template.getEntity().getSuperClass().getPackage().getName());
        }
        if (annotation.getEnableBuilder()) {
            pkgs.add(Builder.class.getPackage().getName());
        }
        List<String> list = new ArrayList<>(pkgs);
        Collections.sort(list);
        buildDataMap.put("pkgs", list);
        pkg = pkg + "." + template.getEntity().getModuleName();
        buildDataMap.put("pkg", pkg);
    }
}
