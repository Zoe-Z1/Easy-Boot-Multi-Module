package cn.easy.boot3.admin.roleMenu.controller;

import cn.easy.boot3.admin.roleMenu.service.IRoleMenuService;
import cn.easy.boot3.core.base.BaseController;

import jakarta.annotation.Resource;

/**
 * @author zoe
 * @date 2023/07/30
 * @description 角色菜单关联 前端控制器
 */
//@Tag(name = "角色菜单关联接口")
//@RestController
//@RequestMapping("/admin/roleMenu")
public class RoleMenuController extends BaseController {

    @Resource
    private IRoleMenuService roleMenuService;



}
