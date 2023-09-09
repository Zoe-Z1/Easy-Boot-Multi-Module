package com.easy.boot.common.generator.config;

import com.easy.boot.exception.GeneratorException;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zoe
 * @date 2023/8/13
 * @description 代码生成配置
 */
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratorConfig {

    @ApiModelProperty(value = "全局参数配置")
    private GlobalConfig global;

    @ApiModelProperty(value = "数据库参数配置")
    private DataSourceConfig dataSource;

    @ApiModelProperty(value = "注解参数配置")
    private AnnotationConfig annotation;

    @ApiModelProperty(value = "模板配置")
    private TemplateConfig template;

    @ApiModelProperty(value = "过滤配置")
    private FilterConfig filter;

    public GlobalConfig getGlobalConfig() {
        if (global == null) {
            throw new GeneratorException("全局参数配置不能为空");
        }
        return global;
    }

    public DataSourceConfig getDataSourceConfig() {
        if (dataSource == null) {
            throw new GeneratorException("数据库参数配置不能为空");
        }
        return dataSource;
    }

    public AnnotationConfig getAnnotationConfig() {
        if (annotation == null) {
            throw new GeneratorException("注解参数配置不能为空");
        }
        return annotation;
    }

    public TemplateConfig getTemplateConfig() {
        if (template == null) {
            template = new TemplateConfig();
        }
        return template;
    }

    public FilterConfig getFilterConfig() {
        if (filter == null) {
            filter = new FilterConfig();
        }
        return filter;
    }
}
