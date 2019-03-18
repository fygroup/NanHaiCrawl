package com.ab.crawl.processor;

import com.ab.crawl.udp.Send;
import com.ab.crawl.util.FtpUtil;
import com.ab.crawl.util.UserAgentUtil;
import com.alibaba.fastjson.JSONObject;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/01/18 9:39
 * @see com.ab.crawl.processor
 */
public class WenZhouTyphoonProcessor implements PageProcessor {

    private Site site = Site.me()
            .setDisableCookieManagement(true).setCharset("UTF-8").setTimeOut(10000).setRetryTimes(2)
            .setSleepTime(new Random().nextInt(3) * 15).setUserAgent(UserAgentUtil.getRandomUserAgent());

    @Override
    public void process(Page page) {

        Json json=page.getJson();
        //却分是否为台风列表
        if (page.getUrl().toString().contains("path")){

        }else {

        }
    }
    @Override
    public Site getSite() {
        return site;
    }
    public static void main(String[] args) {

        Spider.create(new WenZhouTyphoonProcessor())
                .addUrl("http://www.wztf121.com/data/complex/path.json")
                .thread(5)
                //启动爬虫
                .run();
    }

}