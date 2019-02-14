package com.ab.crawl.quartz;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/02/13 9:59
 * @see com.ab.crawl.quartz
 */

import com.ab.crawl.pipeline.WavePipeline;
import com.ab.crawl.processor.TyphoonList;
import com.ab.crawl.processor.WaveProcessor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import us.codecraft.webmagic.Spider;

public class WaveQuartz implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("正在运行海风海浪爬取任务");
        Spider.create(new WaveProcessor())
                .addPipeline(new WavePipeline())
                //海浪数据
                .addUrl("http://szyb.hyyb.org/img_show.aspx?id=36")
                //海风数据
                //.addUrl("http://szyb.hyyb.org/img_show.aspx?id=16")
                .thread(5)
                .run();
    }
}