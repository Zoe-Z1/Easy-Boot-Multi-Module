package cn.easy.boot3.core.saToken;

import cn.easy.boot3.core.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(title = "部门ID")
    private Long departmentId;

    @Schema(title = "头像")
    private String avatar;

    @Schema(title = "账号")
    private String username;

    @Schema(title = "密码")
    private String password;

    @Schema(title = "昵称")
    private String name;

    @Schema(title = "性别 0：保密 1：男 2：女")
    private Integer sex;

    @Schema(title = "邮箱")
    private String email;

    @Schema(title = "手机号码")
    private String mobile;

    @Schema(title = "密码盐")
    private String salt;

    @Schema(title = "账号状态 1：正常，2：禁用")
    private Integer status;

    @Schema(title = "排序")
    private Integer sort;

}
