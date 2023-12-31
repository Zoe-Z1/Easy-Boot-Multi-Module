package cn.easy.boot3.admin.department.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.easy.boot3.core.base.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
* @author zoe
* @date 2023/07/29
* @description 部门 实体
*/
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("department")
@Schema(title = "Department对象", description = "部门")
public class Department extends BaseEntity {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(title = "父级部门ID，为0则代表最上级部门")
    @TableField("parent_id")
    private Long parentId;

    @Schema(title = "部门名称")
    @TableField("name")
    private String name;

    @Schema(title = "部门状态 1：正常 2：禁用")
    @TableField("status")
    private Integer status;

    @Schema(title = "排序")
    @TableField("sort")
    private Integer sort;

    @Schema(title = "部门备注")
    @TableField("remarks")
    private String remarks;
}
