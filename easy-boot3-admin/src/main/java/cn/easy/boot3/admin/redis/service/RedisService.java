package cn.easy.boot3.admin.redis.service;

import cn.easy.boot3.admin.redis.entity.RedisVO;

/**
 * @author zoe
 * @date 2023/8/13
 * @description 缓存监控
 */
public interface RedisService {

    /**
     * 获取缓存信息
     * @return
     */
    RedisVO getRedisInfo();
}
