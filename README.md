# Introduction
callback-component is a simple, time-lapse callback component that can be used in one minute by simply introducing a jar and configuring a data table.

callback-component 是一个简便的延时回调组件，一分钟入手，只需引入jar、配置数据表，即可进行使用。

# Quick Start
Use for, to achieve the [Task](https://github.com/jinyse/callback/blob/master/src/main/java/com/jinghui/callback/service/Task.java) interface, rewrite the logic of consumption, and where you need to add the Task calls scheduleService.addSchedule(param...) add tasks, add the delayed task table in the database [schedule_task](https://github.com/jinyse/callback/blob/master/src/main/resource/schedule_task.sql), that is, complete the configuration.

使用方式为，实现 [Task](https://github.com/jinyse/callback/blob/master/src/main/java/com/jinghui/callback/service/Task.java) 接口，重写消费逻辑，并在需要添加任务的地方调用scheduleService.addSchedule(param...)添加任务，在数据库中添加延时任务表 [schedule_task](https://github.com/jinyse/callback/blob/master/src/main/resource/schedule_task.sql)，即完成配置。

# More Example
[Test-Example](https://github.com/jinyse/callback/blob/master/src/test/java/com/jinghui/callback/service/ScheduleTaskTest.java)

[Main-Implementation](https://github.com/jinyse/callback/blob/master/src/main/java/com/jinghui/callback/service/ScheduleServiceImpl.java)