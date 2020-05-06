package com.jinghui.callback.service;

/**
 * @Description: 业务方实现接口
 * @Author: JingHui Lin
 * @Date: 2020/5/6 10:23
 * @Version V1.0
 */
public interface Task {

    /**
     * 任务的执行逻辑
     *
     * @param taskParameter 执行参数
     */
    void execute(String taskParameter);
}
