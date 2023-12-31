package cn.easy.boot3.task;

import cn.easy.boot3.admin.blacklist.service.IBlacklistService;
import cn.easy.boot3.admin.scheduledTask.entity.ScheduledTask;
import cn.easy.boot3.quartz.EasyJobTaskInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * @author zoe
 * 
 * @describe quartz 取消拉黑定时任务
 * @date 2023/8/5
 */
@Slf4j
@Component
public class CancelBlacklistTaskManager implements EasyJobTaskInterface {

    @Resource
    private IBlacklistService blacklistService;


    @Override
    public String getKey() {
        return "CancelBlacklist";
    }

    /**
     * 执行定时任务
     *
     * @param task 任务参数
     */
    @Override
    public void execute(ScheduledTask task) {
        log.info("取消拉黑定时任务，task： {} ", task);
        blacklistService.updateBlacklistStatus();
    }
}
