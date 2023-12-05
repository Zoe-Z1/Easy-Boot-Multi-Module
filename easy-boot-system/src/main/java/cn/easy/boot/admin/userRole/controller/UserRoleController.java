package cn.easy.boot.admin.userRole.controller;

import cn.easy.boot.admin.userRole.service.IUserRoleService;
import cn.easy.boot.core.base.BaseController;

import javax.annotation.Resource;

/**
 * @author zoe
 * @date 2023/07/30
 * @description 用户角色关联 前端控制器
 */
//@Api(tags = "用户角色关联接口")
//@RestController
//@RequestMapping("/admin/userRole")
public class UserRoleController extends BaseController {

    @Resource
    private IUserRoleService userRoleService;


}
