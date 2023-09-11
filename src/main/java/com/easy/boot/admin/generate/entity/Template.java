package com.easy.boot.admin.generate.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author zoe
 * @date 2023/9/7
 * @description
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("代码生成模板参数对象")
public class Template {

    @ApiModelProperty("是否生成")
    private Boolean enable = true;

    @ApiModelProperty("是否覆盖")
    private Boolean isOverride = true;

    public Template(Boolean isOverride) {
        this.isOverride = isOverride;
    }
}
