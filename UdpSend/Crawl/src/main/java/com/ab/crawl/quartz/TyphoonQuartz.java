package com.ab.crawl.quartz;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/02/13 9:59
 * @see com.ab.crawl.quartz
 */
import java.util.Date;

import com.ab.crawl.processor.TyphoonList;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import us.codecraft.webmagic.Spider;

public class TyphoonQuartz implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        Spider.create(new TyphoonList())
                .addUrl("http://typhoon.nmc.cn/weatherservice/typhoon/jsons/list_2019")
                .thread(1)
                //启动爬虫
                .run();
    }
}