package cn.easy.boot3.admin.login.service.impl;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cn.dev33.satoken.stp.StpUtil;
import cn.easy.boot3.admin.dataDict.entity.DataDict;
import cn.easy.boot3.admin.dataDictDomain.service.IDataDictDomainService;
import cn.easy.boot3.admin.sysConfig.entity.SysConfig;
import cn.easy.boot3.admin.sysConfig.enums.SysConfigCodeEnum;
import cn.easy.boot3.admin.sysConfigDomain.enums.SysConfigDomainCodeEnum;
import cn.easy.boot3.admin.sysConfigDomain.service.ISysConfigDomainService;
import cn.easy.boot3.admin.user.entity.AdminUser;
import cn.easy.boot3.admin.user.service.AdminUserService;
import cn.easy.boot3.core.exception.BusinessException;
import cn.easy.boot3.core.log.enums.OperateStatusEnum;
import cn.easy.boot3.core.redis.EasyRedisManager;
import cn.easy.boot3.core.redis.RedisKeyEnum;
import cn.easy.boot3.core.saToken.SaCache;
import cn.easy.boot3.core.saToken.UserContext;
import cn.easy.boot3.core.utils.BeanUtil;
import cn.easy.boot3.core.utils.IpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.easy.boot3.admin.login.entity.LoginDTO;
import cn.easy.boot3.admin.login.entity.LoginHandlerAfterDO;
import cn.easy.boot3.admin.login.service.AdminLoginService;
import cn.easy.boot3.admin.login.service.CheckLoginHandler;
import cn.easy.boot3.admin.login.service.LoginAfterHandler;
import cn.easy.boot3.admin.loginLog.entity.LoginLogCreateDTO;
import cn.easy.boot3.admin.loginLog.service.ILoginLogService;
import cn.easy.boot3.admin.onlineUser.service.IOnlineUserService;
import cn.easy.boot3.captcha.application.ImageCaptchaApplication;
import cn.easy.boot3.captcha.vo.CaptchaResponse;
import cn.easy.boot3.captcha.vo.ImageCaptchaVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author zoe
 * @date 2023/7/23
 * @description
 */
@Slf4j
@Service
public class AdminLoginServiceImpl implements AdminLoginService {

    @Resource
    private AdminUserService adminUserService;

    @Resource
    private ILoginLogService loginLogService;

    @Resource
    private IOnlineUserService onlineUserService;

    @Resource
    private HttpServletRequest request;

    @Autowired
    private List<LoginAfterHandler> loginAfterHandlers;

    @Autowired
    private List<CheckLoginHandler> checkLoginHandlers;

    @Resource
    private ImageCaptchaApplication application;

    @Resource
    private ISysConfigDomainService sysConfigDomainService;

    @Value("${sa-token.timeout:1800}")
    private Long timeout;

    @Resource
    private EasyRedisManager easyRedisManager;

    @Resource
    private IDataDictDomainService dataDictDomainService;



    @Override
    public AdminUser login(LoginDTO dto) {
        // 登录
        AdminUser user = adminUserService.login(dto.getUsername(), dto.getPassword());
        // 解析登录信息
        String ip = IpUtil.getIp(request);
        String userAgent = request.getHeader("User-Agent");
        LoginLogCreateDTO loginLog = LoginLogCreateDTO.builder()
                .username(dto.getUsername())
                .ip(ip)
                .userId(user == null ? null : user.getId())
                .userAgent(userAgent)
                .build();
        for (LoginAfterHandler loginAfterHandler : loginAfterHandlers) {
            LoginHandlerAfterDO afterDO = loginAfterHandler.handler(user, dto);
            if (!afterDO.getStatus()) {
                loginLog.setStatus(String.valueOf(OperateStatusEnum.FAIL));
                loginLog.setRemarks(afterDO.getMessage());
                loginLogService.asyncCreate(loginLog);
                throw new BusinessException(afterDO.getMessage());
            }
        }
        SaCache saCache = BeanUtil.copyBean(user, SaCache.class);
        UserContext.login(saCache);
        loginLog.setToken(StpUtil.getTokenValue());
        loginLog.setStatus(String.valueOf(OperateStatusEnum.SUCCESS));
        loginLog.setRemarks("登录成功");
        loginLogService.asyncCreate(loginLog);
        return user;
    }

    @Override
    public void logout() {
        if (UserContext.isLogin()) {
            String token = StpUtil.getTokenValue();
            onlineUserService.deleteByToken(token);
        }
        UserContext.logout();
    }

    @Override
    public void checkLogin() {
        UserContext.checkAdminUserLogin();
        Long id = UserContext.getId();
        for (CheckLoginHandler checkLoginHandler : checkLoginHandlers) {
            checkLoginHandler.check(id);
        }
        // 续签Token
        StpUtil.renewTimeout(timeout);
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> getCode() {
//        RANDOM (随机)、SLIDER (滑块验证码)、ROTATE (旋转验证码)、CONCAT (滑动还原验证码)、WORD_IMAGE_CLICK (文字点选验证码)
        SysConfig sysConfig = sysConfigDomainService.getNotDisabledByDomainCodeAndConfigCode(
                SysConfigDomainCodeEnum.GLOBAL.getCode(),
                SysConfigCodeEnum.CAPTCHA_TYPE.getCode()
        );
        if (sysConfig == null) {
            sysConfig = new SysConfig();
            // 不设置则默认为滑块验证码
            sysConfig.setValue(CaptchaTypeConstant.SLIDER);
        }
        // 随机验证码
        String random = "RANDOM";
        if (sysConfig.getValue().equals(random)) {
            List<DataDict> dataDicts = dataDictDomainService.selectListByDomainCode(SysConfigCodeEnum.CAPTCHA_TYPE.getCode());
            if (CollUtil.isNotEmpty(dataDicts)) {
                Collections.shuffle(dataDicts);
                sysConfig.setValue(dataDicts.get(0).getCode());
            }
        }
        return application.generateCaptcha(sysConfig.getValue());
    }

    @Override
    public ApiResponse<?> validateCode(String id, ImageCaptchaTrack track) {
        ApiResponse<?> response = application.matching(id, track);
        if (response.isSuccess()) {
            String key = RedisKeyEnum.ADMIN_LOGIN_CAPTCHA.getKey(id);
            easyRedisManager.put(key, id, 5L);
        } else {
            log.error("validateCode code -> {}, msg -> {} ", response.getCode(), response.getMsg());
        }
        return response;
    }
}
