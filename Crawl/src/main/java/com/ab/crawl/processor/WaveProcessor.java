package com.ab.crawl.processor;


import com.ab.crawl.pipeline.WavePipeline;
import com.ab.crawl.udp.Send;
import com.ab.crawl.udp.State;
import com.ab.crawl.util.SetSystemProperty;
import com.ab.crawl.util.UserAgentUtil;
import com.alibaba.fastjson.JSON;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.*;

/**
 * <Description>
 * 爬取广东三防相关通知信息
 * @author tongziqi
 * @version 1.0
 * @createDate 2018/12/27 10:08
 * @see com.ab.crawl.processor
 */

public class WaveProcessor implements PageProcessor {
    /**
     * 配置信息
     */
    private final static String DATE_FORMAT = "yyyyMMdd";


    private Site site = Site.me()
            .setDisableCookieManagement(true).setCharset("UTF-8").setTimeOut(10000).setRetryTimes(2)
            .setSleepTime(new Random().nextInt(3) * 15).setUserAgent(UserAgentUtil.getRandomUserAgent());


    @Override
    public void process(Page page) {
        State state = new State();
        state.setCRAWLTIME(new Date());

        Html html = page.getHtml();
        //获取到页面上一次刷新时间
        String currDate = "";
        currDate = html.xpath("div[@id=tagContent1]/img/@src").toString();
        try{
            currDate= currDate.substring(18,28);
        }catch (NullPointerException n){
            state.setKIND("wave");
            state.setREMARK("海风海浪获取不到数据信息，请检查外网网络");
            state.setDATASTATE(0);
            state.setDATATIME(currDate);
            String stateWave = JSON.toJSONString(state);
            new Send().UdpSend(stateWave,8815);
            page.setSkip(true);
            return;
        }

        String configDay ="" ;
        if (page.getUrl().toString().contains("36")){
            state.setKIND("wave");
            configDay= SetSystemProperty.getKeyValue("wave");
        }else if (page.getUrl().toString().contains("16")){
            state.setKIND("wind");
            configDay= SetSystemProperty.getKeyValue("wind");
        }else {
            page.putField("state",3);
        }
        //判断是否刷新了数据
        boolean refresh = configDay.equals(currDate);
        if (refresh){
            state.setREMARK("没有更新数据");
            state.setDATASTATE(0);
            state.setDATATIME(currDate);
            String stateWave = JSON.toJSONString(state);
            new Send().UdpSend(stateWave,8815);
            page.setSkip(true);
            return;
        }
        if (page.getUrl().toString().contains("36")){
            page.putField("state",1);
        }else if (page.getUrl().toString().contains("16")){
            page.putField("state",2);
        }else {
            page.putField("state",3);
        }
        page.putField("currdate",currDate);
        List<String> dateLength = new ArrayList();
        dateLength = html.xpath("select[@onchange='show(this.value)']/option").all();
        //共有多少个图片
        //System.out.println("共有"+dateLength.size()+"个图片");

        page.putField("length",dateLength.size());

        //System.out.println(page.getResultItems().get("state").toString());
    }


    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        Spider.create(new WaveProcessor())
                .addPipeline(new WavePipeline())
                //海浪数据
                .addUrl("http://szyb.hyyb.org/bigimg_show.aspx?id=36")
                //海风数据
                .addUrl("http://szyb.hyyb.org/img_show.aspx?id=16")
                .thread(5)
                .run();
    }

    public static String getDay (){
        Calendar calendar =Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }
    }