/*
 * Copyright (C), 2005-2020, 深圳市珍爱网信息技术有限公司
 */
package com.jinghui.callback.service;

import com.alibaba.fastjson.JSONObject;
import com.jinghui.callback.utils.CallBackUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @Description: 业务方代码
 * @Author: JingHui Lin
 * @Date: 2020/5/6 10:28
 * @Version V1.0
 */
@Slf4j
public class ScheduleTaskTest implements Task {

    @Resource
    private ScheduleService scheduleService;

    /**
     * 延时组件回调方法
     * @param taskParameter 执行参数
     */
    @Override
    public void execute(String taskParameter) {
        log.info("receive on callback taskParameter:[{}]", taskParameter);
        JSONObject param = JSONObject.parseObject(taskParameter);
        Integer memberId = Integer.valueOf(param.get("memberId").toString());
        // todo business code
    }

    /**
     * 添加到延时任务中
     */
    @Test
    public void addSchedule() {
        JSONObject param = new JSONObject();
        param.put("memberId", 11111);
        long addScheduleResult = scheduleService.addSchedule("scheduleTaskTest", CallBackUtils.getBeanName(this.getClass().getSimpleName()),
                param.toJSONString(), CallBackUtils.getDelayTime());
        if (addScheduleResult > 0) {
            log.info("addSchedule:[{}] is success", addScheduleResult);
        } else {
            log.error("addSchedule:[{}] is fail, has error", addScheduleResult);
        }
    }
}
