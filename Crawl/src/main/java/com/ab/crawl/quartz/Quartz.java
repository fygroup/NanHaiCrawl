package com.ab.crawl.quartz;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/02/13 9:59
 * @see com.ab.crawl.quartz
 */
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class Quartz {

    public void quartzMain() {
        try {
            //创建scheduler
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            //定义Trigger（一个Trigger只能对应一个job，job可对应多个）
            //定义name/group
            Trigger triggerTyphoon = newTrigger().withIdentity("trigger5", "group5")
                    //一旦加入scheduler，立即生效
                    .startNow()
                    //使用SimpleTrigger
                    .withSchedule(simpleSchedule()
                            //每隔一秒执行一次
                            .withIntervalInSeconds(300)

                            //一直执行，奔腾到老不停歇
                            .repeatForever())
                    .build();
            //定义其他Trigger
            Trigger triggerNotice = newTrigger().withIdentity("trigger2", "group2")
                    .startNow().withSchedule(simpleSchedule().withIntervalInSeconds(300).repeatForever()).build();
            Trigger triggerWave = newTrigger().withIdentity("trigger3", "group3")
                    .startNow().withSchedule(simpleSchedule().withIntervalInSeconds(300).repeatForever()).build();
            Trigger triggerReef = newTrigger().withIdentity("trigger4", "group4")
                    .startNow().withSchedule(simpleSchedule().withIntervalInSeconds(300).repeatForever()).build();
            Trigger triggerState = newTrigger().withIdentity("trigger1", "group1")
                    .startNow().withSchedule(simpleSchedule().withIntervalInSeconds(300).repeatForever()).build();
            //定义一个JobDetail
            //定义Job类为HelloQuartz类，这是真正的执行逻辑所在
            JobDetail jobTyphoon = newJob(TyphoonQuartz.class)
                    //定义name/group
                    .withIdentity("job5", "group5")
                    //定义属性
                    .usingJobData("name", "quartz")
                    .build();

            //定义其他JobDetail
            JobDetail jobNotice = newJob(NoticeQuartz.class).withIdentity("job2", "group2")
                    .usingJobData("name", "quartz").build();
            JobDetail jobReef = newJob(ReefQuartz.class).withIdentity("job3", "group3")
                    .usingJobData("name", "quartz").build();
            JobDetail jobWave = newJob(WaveQuartz.class).withIdentity("job4", "group4")
                    .usingJobData("name", "quartz").build();
            JobDetail jobState = newJob(StateQuartz.class).withIdentity("job1", "group1")
                    .usingJobData("name", "quartz").build();

            //加入这些调度
            //scheduler.scheduleJob(jobState, triggerState);

            //启动之

            scheduler.scheduleJob(jobTyphoon, triggerTyphoon);
            scheduler.scheduleJob(jobNotice, triggerNotice);
            scheduler.scheduleJob(jobReef, triggerReef);
            scheduler.scheduleJob(jobWave, triggerWave);

            scheduler.start();



            //运行一段时间后关闭
            //Thread.sleep(10000);
            //scheduler.shutdown(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}