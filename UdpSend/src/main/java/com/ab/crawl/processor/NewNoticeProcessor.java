package com.ab.crawl.processor;


import com.ab.crawl.pipeline.NewNoticePipeline;
import com.ab.crawl.util.SetSystemProperty;
import com.ab.crawl.util.UserAgentUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;
import java.util.Random;

/**
 * <Description>
 * 爬取广东三防相关通知信息
 * @author tongziqi
 * @version 1.0
 * @createDate 2018/12/27 10:08
 * @see com.ab.crawl.processor
 */
public class NewNoticeProcessor implements PageProcessor {
    /**
     * 配置信息
     */
    private Site site = Site.me()
            .setDisableCookieManagement(true).setCharset("UTF-8").setTimeOut(10000).setRetryTimes(2)
            .setSleepTime(new Random().nextInt(3) * 15).setUserAgent(UserAgentUtil.getRandomUserAgent());



    @Override
    public void process(Page page) {

        //首先判断是否是详情页
        if (page.getUrl().toString().length()<60){
            String configDate=SetSystemProperty.getKeyValue("noticeDate");
            System.out.println("配置文件时间"+configDate);
           List<String> urls=page.getHtml().xpath("ul[@class='clearfix']/li/a[@target='_blank']/@href").all();
           List<String> date=page.getHtml().xpath("span[@class='time']/text()").all();
           int item = 0 ;
           for (item =0; item <date.size();item++){
               if (date.get(item).equals(configDate)){
                   break;
               }
           }
           SetSystemProperty.writeProperties("noticeDate",date.get(0));
           urls=urls.subList(0,item);
           page.addTargetRequests(urls);
        }
        else {
            page.putField("html",page.getHtml().xpath("//div[@class='content_article']"));
            //通过Url解析出当前页的html名称，方便后用
            String[] htmlName = page.getUrl().toString().split("/");
            page.putField("htmlname",htmlName[htmlName.length-1]);
            //附件地址和图片地址
            List<String> fileUrl = page.getHtml().xpath("//div[@class='content_article']").xpath("a/@href|img/@src").all();
            //标题
            List<String> titles = page.getHtml().xpath("//span[@style='font-size: 22pt;']/text()").all();
            //因文章中标题不在同一标签所以遍历
            String title = "";
            for (String temp :titles){
                title+=temp;
            }
            page.putField("title",title);
            page.putField("time",page.getHtml().xpath("//publishtime/text()"));
            //根地址
            String[] pageUrl =page.getUrl().toString().split("/");
            String fileBaseUrl="";
            for (int item =0;item<6;item++){
                fileBaseUrl=fileBaseUrl+pageUrl[item]+"/";
            }
            for (int item = 0 ;item < fileUrl.size(); item ++){
                //如果没有带http则添加根地址方便下载
                if(!fileUrl.get(item).contains("http")){
                    String AfterUrl = fileBaseUrl+fileUrl.get(item);
                    fileUrl.set(item,AfterUrl);
                }
                //把邮箱去掉
                if (fileUrl.get(item).contains("@")){
                    fileUrl.remove(item);
                }
            }
            page.putField("fileurl",fileUrl);
        }
    }
    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        Spider.create(new NewNoticeProcessor())
                .addPipeline(new NewNoticePipeline())
                //广东三防
                .addUrl("http://www.gdsafety.gov.cn/gdyjglt/gkai/list.shtml")
                .thread(5)
                .run();
    }

    }
