package com.jinghui.callback.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jinghui.callback.dao.ScheduleTaskMapper;
import com.jinghui.callback.entity.ScheduleTaskDO;
import com.jinghui.callback.utils.HostUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TODO
 * @Author: JingHui Lin
 * @Date: 2020/5/6 10:23
 * @Version V1.0
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    @Value("${scheduler.thread.num:20}")
    private int threadNum = 20;

    @Autowired(required = false)
    private Map<String, Task> taskMap = new HashMap<>();

    private ExecutorService executorService;

    private boolean destroyed = false;

    @Autowired
    private ScheduleTaskMapper scheduleTaskMapper;

    @Override
    public long addSchedule(String scheduleName, String beanName, String taskParameter, String schedulePlan) {
        ScheduleTaskDO scheduleTaskDO = new ScheduleTaskDO();
        scheduleTaskDO.setScheduleName(scheduleName);
        scheduleTaskDO.setBeanName(beanName);
        scheduleTaskDO.setTaskParameter(taskParameter);
        scheduleTaskDO.setStatus(ScheduleTaskDO.STATUS_WAITING);
        scheduleTaskDO.setExecPlan(schedulePlan);
        scheduleTaskDO.setNextExecTime(getNextExecTime(schedulePlan));
        if (null == scheduleTaskDO.getNextExecTime()) {
            throw new RuntimeException("无法添加schedule:" + schedulePlan);
        }
        scheduleTaskMapper.insert(scheduleTaskDO);
        return scheduleTaskDO.getId();
    }

    @Override
    public long addSchedule(String scheduleName, String beanName, String taskParameter, Date nextExecTime) {
        ScheduleTaskDO scheduleTaskDO = new ScheduleTaskDO();
        scheduleTaskDO.setScheduleName(scheduleName);
        scheduleTaskDO.setBeanName(beanName);
        scheduleTaskDO.setTaskParameter(taskParameter);
        scheduleTaskDO.setStatus(ScheduleTaskDO.STATUS_WAITING);
        scheduleTaskDO.setNextExecTime(LocalDateTime.ofInstant(nextExecTime.toInstant(), ZoneId.systemDefault()));
        if (null == scheduleTaskDO.getNextExecTime()) {
            throw new RuntimeException("无法添加schedule:" + nextExecTime);
        }
        scheduleTaskMapper.insert(scheduleTaskDO);
        return scheduleTaskDO.getId();
    }

    @Override
    public ScheduleTaskDO findByScheduleName(String scheduleName) {
        return scheduleTaskMapper.findByScheduleName(scheduleName);
    }

    @Override
    public void pauseSchedule(int scheduleId) {
        ScheduleTaskDO scheduleTaskDO = scheduleTaskMapper.selectById(scheduleId);
        if (null == scheduleTaskDO) {
            return;
        }
        scheduleTaskDO.setStatus(ScheduleTaskDO.STATUS_PAUSED);
        scheduleTaskMapper.insert(scheduleTaskDO);
    }

    @Override
    public void deleteSchedule(int scheduleId) {
        scheduleTaskMapper.deleteById(scheduleId);
    }

    @Override
    public void deleteByScheduleNameLike(String scheduleName) {
        scheduleTaskMapper.deleteByScheduleNameLike(scheduleName);
    }

    @Override
    public void startSchedule(int scheduleId) {
        ScheduleTaskDO scheduleTaskDO = scheduleTaskMapper.selectById(scheduleId);
        scheduleTaskDO.setStatus(ScheduleTaskDO.STATUS_WAITING);
        scheduleTaskMapper.insert(scheduleTaskDO);
    }

    @PostConstruct
    public void init() {
        startUnlockTaskThread();
        executorService = Executors.newFixedThreadPool(threadNum,
                new ThreadFactoryBuilder().setNameFormat("pool-schedule-%d").build());
        new Thread(new ScheduleThreadTask(), "schedule-scanner").start();
    }

    /**
     * 任务加锁之后，有可能因为某些原因无法及时释放，该线程负责定时扫描未及时释放的任务，进行释放操作
     */
    private void startUnlockTaskThread() {
        Thread unlockTaskThread = new Thread(() -> {
            while (true) {
                int affectedRows = scheduleTaskMapper.unlockTasks(LocalDateTime.now().plus(5, ChronoUnit.SECONDS));
                if (affectedRows > 0) {
                    logger.warn("scheduler unlocked {} tasks", affectedRows);
                }
                try {
                    TimeUnit.MINUTES.sleep(5);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        });
        // 设置为守护线程，若是用户线程则可能导致应用不能正常关闭
        unlockTaskThread.setDaemon(true);
        unlockTaskThread.start();
    }


    @PreDestroy
    public void destroy() {
        destroyed = true;
    }

    private LocalDateTime getNextExecTime(String plan) {
        if (StringUtils.isBlank(plan)) {
            return null;
        }
        try {
            CronExpression cronExpression = new CronExpression(plan);
            Date dataBaseTime = new Date();
            Date nextValidTimeAfter = cronExpression.getNextValidTimeAfter(dataBaseTime);
            if (null == nextValidTimeAfter) {
                return null;
            }
            return LocalDateTime.ofInstant(nextValidTimeAfter.toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
    }

    private void executeTask(ScheduleTaskDO scheduleTaskDO) {
        String beanName = scheduleTaskDO.getBeanName();
        Task task = taskMap.get(beanName);
        if (null == task) {
            logger.error(scheduleTaskDO.getLastExecTime() + ":can not find bean by name:" + beanName);
            return;
        }
        try {
            logger.info("开始执行任务:{}", scheduleTaskDO.getTaskParameter());
            task.execute(scheduleTaskDO.getTaskParameter());
        } catch (Exception e) {
            logger.error(scheduleTaskDO.getLastExecTime() + "||" + scheduleTaskDO.getBeanName() + ":" + e.getMessage(), e);
        }
    }

    private class ScheduleThreadTask implements Runnable {
        @Override
        public void run() {
            String locker = HostUtils.getServerIp() + "-" + HostUtils.getPid() + "_" + Thread.currentThread().getId();
            while (!destroyed) {
                try {
                    int affectedRows = scheduleTaskMapper.lockTask(locker);
                    if (affectedRows <= 0) {
                        TimeUnit.SECONDS.sleep(10);
                        continue;
                    }

                    List<ScheduleTaskDO> scheduleTaskDOs = scheduleTaskMapper
                            .findByStatusAndLocker(ScheduleTaskDO.STATUS_EXECUTING, locker);
                    for (ScheduleTaskDO scheduleTaskDO : scheduleTaskDOs) {

                        LocalDateTime nextExecTime = getNextExecTime(scheduleTaskDO.getExecPlan());
                        scheduleTaskDO.setNextExecTime(nextExecTime);
                        if (null == nextExecTime) {
                            scheduleTaskDO.setStatus(ScheduleTaskDO.STATUS_FINISHED);
                        } else {
                            scheduleTaskDO.setStatus(ScheduleTaskDO.STATUS_WAITING);
                        }
                        scheduleTaskDO.setLastExecTime(LocalDateTime.now());
                        scheduleTaskMapper.updateById(scheduleTaskDO);
                        logger.info("提交执行任务到线程池,scheduleId: {}", scheduleTaskDO.getTaskParameter());
                        executorService.submit(() -> executeTask(scheduleTaskDO));
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
            logger.warn("stopped schedule thread");
        }
    }
}

