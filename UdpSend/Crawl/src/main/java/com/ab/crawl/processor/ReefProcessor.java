package com.ab.crawl.processor;

import com.ab.crawl.udp.Send;
import com.ab.crawl.udp.State;
import com.ab.crawl.util.RegexUtil;
import com.ab.crawl.util.SetSystemProperty;
import com.ab.crawl.util.UserAgentUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.log4j.Logger;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

import java.util.Date;
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
    private static org.apache.log4j.Logger logger = Logger.getLogger(ReefProcessor.class);
    private Site site = Site.me()
            .setDisableCookieManagement(true).setCharset("UTF-8").setTimeOut(10000).setRetryTimes(2)
            .setSleepTime(new Random().nextInt(3) * 15).setUserAgent(UserAgentUtil.getRandomUserAgent());

    @Override
    public void process(Page page) {
        RegexUtil regexUtil = new RegexUtil();
        State state =new State();
        state.setCRAWLTIME(new Date());

        Send send = new Send();
        try {
            Json json = page.getJson();
            String json_str = json.toString();
            //把xml头去掉（固定长度）
            state.setDATATIME(regexUtil.getSubUtilSimple(json_str,rgex));
            String xml_str = json_str.substring(41, json_str.length());
            //xml转json
            XMLSerializer xmlSerializer = new XMLSerializer();
            String sendstr = xmlSerializer.read(xml_str).toString().replace("@", "");
            JSONObject jsonObject = JSONObject.parseObject(sendstr);
            String pageUrl = page.getUrl().toString();
            if (pageUrl.equals(ISLANDS)) {
                state.setKIND("isLands");
                String configSeaAreaTime = SetSystemProperty.getKeyValue("isLands");
                boolean flag = configSeaAreaTime.equals(regexUtil.getSubUtilSimple(json_str,rgex));
                if (flag==true){state.setREMARK("海区预报已经是最新数据");String s = JSON.toJSONString(state);
                    boolean udpState= send.UdpSend(s,8815);
                    Thread.sleep(1000);
                    if (udpState==false){
                        logger.error("udp传输失败");

                    }return;  }
                SetSystemProperty.updateProperties("isLands", regexUtil.getSubUtilSimple(json_str,rgex));
                jsonObject.put("Type", 1);
            } else if (pageUrl.equals(SEAAREA)) {
                state.setKIND("seaArea");
                String configisLandsTime = SetSystemProperty.getKeyValue("seaArea");
                boolean flag = configisLandsTime.equals(regexUtil.getSubUtilSimple(json_str,rgex));
                if (flag==true){ state.setREMARK("岛礁预报已经是最新数据");String s = JSON.toJSONString(state);
                    boolean udpState= send.UdpSend(s,8815);
                    Thread.sleep(1000);
                    if (udpState==false){
                        logger.error("udp传输失败");
                    }return;   }
                SetSystemProperty.updateProperties("seaArea", regexUtil.getSubUtilSimple(json_str,rgex));
                jsonObject.put("Type", 2);
            } else if (pageUrl.equals(ROUTE)) {
                state.setKIND("route");
                String configrouteTime = SetSystemProperty.getKeyValue("route");
                boolean flag = configrouteTime.equals(regexUtil.getSubUtilSimple(json_str,rgex));

                if (flag==true){state.setREMARK("航线预报已经是最新数据");String s = JSON.toJSONString(state);
                    boolean udpState= send.UdpSend(s,8815);
                    Thread.sleep(1000);
                    if (udpState==false){
                        logger.error("udp传输失败");

                    }return;  }
                SetSystemProperty.updateProperties("route", regexUtil.getSubUtilSimple(json_str,rgex));
                jsonObject.put("Type", 3);
            }
            String s = JSONObject.toJSONString( jsonObject );
            boolean udpReef= send.UdpSend(s,8812);
            if (udpReef==false) {
                logger.error("气象预报udp传输失败");
            }
            logger.info(s);

            state.setDATASTATE(1);
            state.setREMARK("数据已更新");
            String sendStr = JSON.toJSONString(state);

            boolean udpState= send.UdpSend(sendStr,8815);
            if (udpState==false) {
                logger.error("状态信息udp传输失败");
            }
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

                .addUrl(ISLANDS,SEAAREA,ROUTE)

                //开启5个线程抓取
                .thread(1)
                //启动爬虫
                .run();
    }
}
