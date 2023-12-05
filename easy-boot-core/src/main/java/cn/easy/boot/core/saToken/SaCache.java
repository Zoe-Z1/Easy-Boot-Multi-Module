package cn.easy.boot.core.saToken;

import cn.easy.boot.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author zoe
 * @date 2023/7/21
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class SaCache extends BaseEntity {

    @ApiModelProperty("部门ID")
    private Long departmentId;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "账号")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "昵称")
    private String name;

    @ApiModelProperty(value = "性别 0：保密 1：男 2：女")
    private Integer sex;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "手机号码")
    private String mobile;

    @ApiModelProperty(value = "密码盐")
    private String salt;

    @ApiModelProperty(value = "账号状态 1：正常，2：禁用")
    private Integer status;

    @ApiModelProperty(value = "排序")
    private Integer sort;

}
