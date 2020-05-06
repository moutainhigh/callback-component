package com.jinghui.callback.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description: DO
 * @Author: JingHui Lin
 * @Date: 2020/5/6 10:21
 * @Version V1.0
 */
@Data
@TableName("schedule_task")
public class ScheduleTaskDO implements Serializable {

    private static final long serialVersionUID = 5652213124498939276L;

    public static final int STATUS_PAUSED = 0;
    public static final int STATUS_WAITING = 1;
    public static final int STATUS_EXECUTING = 2;
    public static final int STATUS_FINISHED = 3;
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "schedule_name")
    private String scheduleName;

    /**
     * beanName
     */
    @TableField(value = "bean_name")
    private String beanName;

    /**
     * 任务的参数
     */
    @TableField(value = "task_parameter")
    private String taskParameter;

    /**
     * 下次执行时间
     */
    @TableField(value = "next_exec_time")
    private LocalDateTime nextExecTime;

    /**
     * 执行计划
     */
    @TableField(value = "exec_plan")
    private String execPlan;
    /**
     * 上次执行时间
     */
    @TableField(value = "last_exec_time")
    private LocalDateTime lastExecTime;

    /**
     * 加锁者
     */
    @TableField(value = "locker")
    private String locker;

    /**
     * 状态 1:待执行 2:执行中 3:已完成
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 错误信息
     */
    @TableField(value = "error_msg")
    private String errorMsg;

    /**
     * 记录创建时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "gmt_create", fill = FieldFill.DEFAULT)
    private LocalDateTime gmtCreate;

    /**
     * 记录修改时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "gmt_modified", fill = FieldFill.DEFAULT)
    private LocalDateTime gmtModified;
}

