package com.easy.boot3.admin.server.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import com.easy.boot3.admin.server.entity.Project;
import com.easy.boot3.admin.server.entity.ServerVO;
import com.easy.boot3.admin.server.service.ServerService;
import com.easy.boot3.core.redis.EasyRedisManager;
import com.easy.boot3.core.redis.RedisKeyConstant;
import com.easy.boot3.util.ServerUtil;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * @author zoe
 * @date 2023/8/13
 * @description
 */
@Service
public class ServerServiceImpl implements ServerService {

    @Resource
    private EasyRedisManager easyRedisManager;

    @Override
    public ServerVO getServerInfo() {
        Long startTime = (Long) easyRedisManager.get(RedisKeyConstant.PROJECT_START_TIME);
        if (startTime == null) {
            // 保底机制 保证缓存被清除了不会报错
            startTime = System.currentTimeMillis();
            easyRedisManager.put(RedisKeyConstant.PROJECT_START_TIME, startTime);
        }
        Long runningTime = System.currentTimeMillis() - startTime;
        String runningTimeStr = DateUtil.formatBetween(runningTime, BetweenFormatter.Level.SECOND);
        return ServerVO.builder()
                .project(
                        Project.builder()
                                .userDir(System.getProperties().getProperty("user.dir"))
                                .startTime(startTime)
                                .runningTime(runningTime)
                                .runningTimeStr(runningTimeStr)
                        .build()
                )
                .cpu(ServerUtil.getMyCpu())
                .os(ServerUtil.getMyOs())
                .memory(ServerUtil.getMyMemory())
                .disk(ServerUtil.getMyDisk())
                .jvm(ServerUtil.getMyJvm())
                .network(ServerUtil.getMyNetwork())
                .build();
    }
}
