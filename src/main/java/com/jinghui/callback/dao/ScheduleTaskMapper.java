package com.jinghui.callback.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.jinghui.callback.entity.ScheduleTaskDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: DAO
 * @Author: JingHui Lin
 * @Date: 2020/5/6 10:21
 * @Version V1.0
 */
@Mapper
public interface ScheduleTaskMapper extends BaseMapper<ScheduleTaskDO> {

    /**
     * 根据状态和加锁的所有者找到第一个任务
     *
     * @param status 执行状态
     * @param locker 版本号
     * @return DO
     */
    ScheduleTaskDO findFirstByStatusAndLocker(@Param("status") int status, @Param("locker") String locker);

    /**
     * 锁定执行任务
     *
     * @param locker 版本号
     * @param now    当前时间
     * @return 获取锁结果
     */
    Integer lockTask(@Param("locker") String locker, @Param("now") LocalDateTime now);

    /**
     * 找到执行任务
     *
     * @param scheduleName 任务名
     * @return DO
     */
    ScheduleTaskDO findByScheduleName(@Param("scheduleName") String scheduleName);

    /**
     * 删除执行任务
     *
     * @param scheduleName 任务名
     */
    void deleteByScheduleNameLike(@Param("scheduleName") String scheduleName);

    /**
     * 解锁任务
     *
     * @param nowAfterFiveSeconds 5秒后
     * @return 操作结果
     */
    int unlockTasks(@Param("nowAfterFiveSeconds") LocalDateTime nowAfterFiveSeconds);

    /**
     * 修改最后执行时间
     *
     * @param id id
     * @return 操作结果
     */
    int modifyLastExecTime(@Param("id") long id);

    /**
     * 根据状态和加锁的所有者找到所有的任务
     *
     * @param statusExecuting 状态
     * @param locker          版本
     * @return list
     */
    List<ScheduleTaskDO> findByStatusAndLocker(@Param("statusExecuting") int statusExecuting, @Param("locker") String locker);
}

