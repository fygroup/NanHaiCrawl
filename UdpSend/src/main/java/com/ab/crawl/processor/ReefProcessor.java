package com.ab.crawl.processor;

import com.ab.crawl.udp.Send;
import com.ab.crawl.util.RegexUtil;
import com.ab.crawl.util.SetSystemProperty;
import com.ab.crawl.util.UserAgentUtil;
import com.alibaba.fastjson.JSONObject;
import net.sf.json.xml.XMLSerializer;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

import java.util.Random;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/01/07 14:00
 * @see com.ab.crawl.processor
 */
public class ReefProcessor implements PageProcessor {
    private static String SEAAREA = "http://g.hyyb.org/archive/ForecastXML/NanHaiHaiQu.xml";
    private static String ISLANDS = "http://g.hyyb.org/archive/ForecastXML/FenJuDaoJiao.xml";
    private static String ROUTE = "http://g.hyyb.org/archive/ForecastXML/SanYaToRenAiJiao.xml";
    private static String rgex = "<PublishTime>(.*?)</PublishTime>";
    private Site site = Site.me()
            .setDisableCookieManagement(true).setCharset("UTF-8").setTimeOut(10000).setRetryTimes(2)
            .setSleepTime(new Random().nextInt(3) * 15).setUserAgent(UserAgentUtil.getRandomUserAgent());

    @Override
    public void process(Page page) {
        RegexUtil regexUtil = new RegexUtil();
        try {
            Json json = page.getJson();
            String json_str = json.toString();
            //把xml头去掉（固定长度）
            String xml_str = json_str.substring(41, json_str.length());
            //xml转json
            XMLSerializer xmlSerializer = new XMLSerializer();
            String sendstr = xmlSerializer.read(xml_str).toString().replace("@", "");
            JSONObject jsonObject = JSONObject.parseObject(sendstr);
            if (page.getUrl().toString().equals(ISLANDS)) {
                String configSeaAreaTime = SetSystemProperty.getKeyValue("seaArea");
                boolean flag = configSeaAreaTime.equals(regexUtil.getSubUtilSimple(json_str,rgex));
                System.out.println("比对结果1"+flag);
                if (flag==true){ return; }
                SetSystemProperty.writeProperties("seaArea", regexUtil.getSubUtilSimple(json_str,rgex));
                jsonObject.put("Type", 1);
            } else if (page.getUrl().toString().equals(SEAAREA)) {
                String configisLandsTime = SetSystemProperty.getKeyValue("isLands");
                boolean flag = configisLandsTime.equals(regexUtil.getSubUtilSimple(json_str,rgex));
                System.out.println("比对结果2"+flag);
                if (flag==true){ return; }
                SetSystemProperty.writeProperties("isLands", regexUtil.getSubUtilSimple(json_str,rgex));
                jsonObject.put("Type", 2);
            } else if (page.getUrl().toString().equals(ROUTE)) {
                String configrouteTime = SetSystemProperty.getKeyValue("route");
                boolean flag = configrouteTime.equals(regexUtil.getSubUtilSimple(json_str,rgex));
                System.out.println("比对结果3"+flag);
                if (flag==true){ return; }
                SetSystemProperty.writeProperties("route", regexUtil.getSubUtilSimple(json_str,rgex));
                jsonObject.put("Type", 3);
            }


            Send send = new Send();
            send.UdpSend(jsonObject);
            System.out.println("success");

        } catch (Exception e) {
            System.out.println(e);
        }

    }
    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new ReefProcessor())
                //从"https://github.com/code4craft"开始抓
                .addUrl(ISLANDS,SEAAREA,ROUTE)
                //开启5个线程抓取
                .thread(5)
                //启动爬虫
                .run();
    }
}
