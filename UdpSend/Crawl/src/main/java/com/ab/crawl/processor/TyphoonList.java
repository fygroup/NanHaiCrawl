package com.ab.crawl.processor;

import com.ab.crawl.udp.Send;
import com.ab.crawl.udp.State;
import com.ab.crawl.util.SetSystemProperty;
import com.ab.crawl.util.UserAgentUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

import java.util.*;

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

    private static final  String histroyStopMax = SetSystemProperty.getKeyValue("stopTyphoon");
    @Override
    public void process(Page page) {

        State state = new State();
        state.setKIND("typhoon");
        state.setCRAWLTIME(new Date());
        Json json=page.getJson();
        //却分是否为台风列表
        if (page.getUrl().toString().contains("list")){
            List<String> urlList = new ArrayList<>();
            List<String> idList =new ArrayList<>();
            String typhoonlistStr = json.toString();
            String json_list=typhoonlistStr.substring(40,typhoonlistStr.length()-4);
            String[] jsonList = json_list.split("],");
            for (int i = 0 ; i<jsonList.length;i++){
                String[] listItem = jsonList[i].split(",");
                String requestId = listItem[0].substring(1,listItem.length);
                if(listItem[listItem.length-1].contains("start")){
                    idList.add(requestId);
                    String requestUrl = "http://typhoon.nmc.cn/weatherservice/typhoon/jsons/view_"+requestId;
                    urlList.add(requestUrl);
                    return;
                }else {
                    String typhoonStop = jsonList[i].substring(1);
                    if (requestId.compareTo(histroyStopMax)<0){
                        Send send = new Send();
                        send.UdpSend(typhoonStop,8814);
                        state.setREMARK("udp传输成功");
                        state.setREMARK("台风停止");
                        String stopState = JSON.toJSONString(state);
                        send.UdpSend(stopState,8815);
                        SetSystemProperty.updateProperties("stopTyphoon",requestId);
                        return;
                    }
                }
            }
            state.setREMARK("没有更新数据");
            state.setCRAWLTIME(new Date());
            String StateTyphoon = JSON.toJSONString(state);
            new Send().UdpSend(StateTyphoon,8815);
            page.addTargetRequests(urlList);
        }else {
            try {
                String s = JSONObject.toJSONString( json );
                Send send = new Send();
                send.UdpSend(s,8811);
                state.setREMARK("udp传输成功");
                send.UdpSend(JSON.toJSONString(state),8815);
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
                .addUrl("http://typhoon.nmc.cn/weatherservice/typhoon/jsons/list_"+getYear())
                .thread(1)
                //启动爬虫
                .run();
    }

    public static String getYear (){
        Calendar calendar =Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.YEAR));
    }
}