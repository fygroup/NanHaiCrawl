package com.ab.crawl.quartz;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/02/13 9:59
 * @see com.ab.crawl.quartz
 */

import com.ab.crawl.pipeline.NewNoticePipeline;
import com.ab.crawl.processor.NewNoticeProcessor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import us.codecraft.webmagic.Spider;

public class NoticeQuartz implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        Spider.create(new NewNoticeProcessor())
                .addPipeline(new NewNoticePipeline())
                //广东三防
                .addUrl("http://www.gdsafety.gov.cn/gdyjglt/gkai/list.shtml")
                .thread(1)
                .run();
    }
}