package cn.easy.boot3.admin.templateConfig.entity;

import cn.easy.boot3.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;


/**
 * @author zoe
 * @date 2023/11/09
 * @description 模板配置查询实体
 */
@Schema(title = "模板配置查询实体")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TemplateConfigQuery extends BasePageQuery {

    @Schema(title = "模板配置状态 #1：正常， 2：禁用")
    private Integer status;

}
