package cn.easy.boot3.admin.dataDictDomain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
* @author zoe
* @date 2023/08/01
* @description 数据字典域 DTO
*/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "DataDictDomain对象", description = "数据字典域")
public class DataDictDomainCreateDTO {

    @Length(min = 1, max = 50, message = "字典域编码在{min}-{max}个字符之间")
    @NotBlank(message = "字典域编码不能为空")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, title = "字典域编码")
    private String code;

    @Length(min = 1, max = 50, message = "字典域名称在{min}-{max}个字符之间")
    @NotBlank(message = "字典域名称不能为空")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, title = "字典域名称")
    private String name;

    @Range(min = 1, max = 2, message = "字典域状态不正确")
    @NotNull(message = "字典域状态不能为空")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, title = "字典域状态 1：正常 2：禁用")
    private Integer status;

    @Schema(title = "备注")
    private String remarks;

    @Schema(title = "排序")
    private Integer sort;
}
