package com.ab.crawl.processor;

import com.ab.crawl.udp.Send;
import com.ab.crawl.util.UserAgentUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

import java.util.ArrayList;
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
public class TyphoonList implements PageProcessor {

    private Site site = Site.me()
            .setDisableCookieManagement(true).setCharset("UTF-8").setTimeOut(10000).setRetryTimes(2)
            .setSleepTime(new Random().nextInt(3) * 15).setUserAgent(UserAgentUtil.getRandomUserAgent());

    @Override
    public void process(Page page) {

        Json json=page.getJson();
        if (page.getUrl().toString().contains("list")){
            List<String> urlList = new ArrayList<>();
            String typhoonlistStr = json.toString();
            String json_list=typhoonlistStr.substring(40,typhoonlistStr.length()-4);
            String[] jsonList = json_list.split("],");
            for (int i = 0 ; i<jsonList.length;i++){
                System.out.println(jsonList[i]);
                String[] listItem = jsonList[i].split(",");
                if(listItem[listItem.length-1].contains("start")){
                    String requestId = listItem[0].substring(1,listItem.length);
                    String requestUrl = "http://typhoon.nmc.cn/weatherservice/typhoon/jsons/view_"+requestId;
                    System.out.println(requestUrl);
                    urlList.add(requestUrl);
                }
            }
            page.addTargetRequests(urlList);
        }else {
            try {
                Send send = new Send();
                send.UdpSend(json);
                System.out.println(json);
                System.out.println("success");
            }catch (Exception e){
                System.out.println(e);
            }
        }
    }
    @Override
    public Site getSite() {
        return site;
    }
    public static void main(String[] args) {

        Spider.create(new TyphoonList())
                .addUrl("http://typhoon.nmc.cn/weatherservice/typhoon/jsons/list_2019")
                .thread(5)
                //启动爬虫
                .run();
    }

}