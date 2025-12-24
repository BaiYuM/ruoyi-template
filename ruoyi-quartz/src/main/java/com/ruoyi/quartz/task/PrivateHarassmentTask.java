package com.ruoyi.quartz.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.system.service.IFaPrivateHarassmentRecordService;

/**
 * 私信追杀定时任务
 * 
 * @author ruoyi
 */
@Component("privateHarassmentTask")
public class PrivateHarassmentTask
{
    @Autowired
    private IFaPrivateHarassmentRecordService faPrivateHarassmentRecordService;

    /**
     * 执行私信追杀任务
     */
    public void execute()
    {
        faPrivateHarassmentRecordService.processHarassmentTask();
    }
}
