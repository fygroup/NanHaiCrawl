package com.ab.crawl.quartz;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/02/13 9:59
 * @see com.ab.crawl.quartz
 */

import com.ab.crawl.processor.ReefProcessor;
import com.ab.crawl.processor.TyphoonList;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import us.codecraft.webmagic.Spider;



public class ReefQuartz implements Job {

    private static String SEAAREA = "http://g.hyyb.org/archive/ForecastXML/NanHaiHaiQu.xml";
    private static String ISLANDS = "http://g.hyyb.org/archive/ForecastXML/FenJuDaoJiao.xml";
    private static String ROUTE = "http://g.hyyb.org/archive/ForecastXML/SanYaToRenAiJiao.xml";
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        Spider.create(new ReefProcessor())
                //从"https://github.com/code4craft"开始抓
                .addUrl(ISLANDS,SEAAREA,ROUTE)
                //开启5个线程抓取
                .thread(1)
                //启动爬虫
                .run();
    }
}