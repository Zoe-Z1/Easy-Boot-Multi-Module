package cn.easy.boot.admin.login.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.easy.boot.admin.blacklist.entity.Blacklist;
import cn.easy.boot.admin.blacklist.service.IBlacklistService;
import cn.easy.boot.admin.login.service.CheckLoginHandler;
import cn.easy.boot.admin.onlineUser.service.IOnlineUserService;
import cn.easy.boot.core.exception.BusinessException;
import cn.easy.boot.core.exception.enums.SystemErrorEnum;
import cn.easy.boot.core.saToken.UserContext;
import cn.hutool.core.date.DateUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author zoe
 * @date 2023/10/24
 * @description 登录状态检查器
 */
@Service
public class CheckLoginHandlerUserBlacklistImpl implements CheckLoginHandler {

    @Resource
    private IBlacklistService blacklistService;

    @Resource
    private IOnlineUserService onlineUserService;

    @Override
    public void check(Long id) {
        String username = UserContext.getUsername();
        // 查询用户黑名单
        Blacklist blacklist = blacklistService.getByUsername(username);
        if (Objects.nonNull(blacklist)) {
            if (blacklist.getEndTime() == 0) {
                throw new BusinessException(SystemErrorEnum.USER_IS_BLACKLIST);
            }
            // 计算拉黑结束时间 大于当前时间则代表拉黑中
            if (blacklist.getEndTime() > DateUtil.current()) {
                String token = StpUtil.getTokenValue();
                UserContext.kickout(token);
                onlineUserService.deleteByToken(token);
                throw new BusinessException(SystemErrorEnum.USER_IS_BLACKLIST);
            }
        }
    }
}