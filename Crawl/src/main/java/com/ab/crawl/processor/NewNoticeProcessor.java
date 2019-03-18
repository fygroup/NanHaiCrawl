package com.ab.crawl.processor;


import com.ab.crawl.pipeline.NewNoticePipeline;
import com.ab.crawl.udp.Send;
import com.ab.crawl.udp.State;
import com.ab.crawl.util.RegexUtil;
import com.ab.crawl.util.SetSystemProperty;
import com.ab.crawl.util.UserAgentUtil;
import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.processor.PageProcessor;
import java.util.ArrayList;
import java.util.Date;
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
    private static org.apache.log4j.Logger logger = Logger.getLogger(NewNoticeProcessor.class);
    /**
     * 敏感字 初版：“防汛防旱防风总指挥部”、“海洋”、“赤潮”、“防台”、“防风”、“低温防冻”、“热带低压”、“台风”;
     */
    private static final String contentRegex=SetSystemProperty.getKeyValue("contentRegex");
    List<String> urlSum = new ArrayList<>();
    List<String> titleSum = new ArrayList<>();
    @Override
    public void process(Page page) {
        State state =new State();
        state.setCRAWLTIME(new Date());
        state.setKIND("announcement");
        Send send = new Send();
        String noticeRegex=".*"+contentRegex.replace(",",".*|.*")+".*";
        //首先根据60判断是否是详情页
        int usuallyUrlLength=60;
        if (page.getUrl().toString().length()<usuallyUrlLength){
            String configDate=SetSystemProperty.getKeyValue("noticeDate");
           List<String> urls=page.getHtml().xpath("ul[@class='clearfix']/li/a[@target='_blank']/@href").all();
           List<String> date=page.getHtml().xpath("span[@class='time']/text()").all();
           List<String> titles=page.getHtml().xpath("div[@class='news_list']//li/a/@title").all();
           int item = 0 ;
           for (item=date.size()-1; item >0;item--){
               if (date.get(item).compareTo(configDate)>0){
                   break;
               }
           }
           if (item==0){
               state.setDATATIME(configDate);
               state.setREMARK("通告信息已经是最新数据");
               String s = JSON.toJSONString(state);
               boolean udpState= send.UdpSend(s,8815);
               if (udpState==false){
                   System.out.println();
                   logger.error("udp传输失败");
                   return;
               }
           }else {
               urls=urls.subList(0,item);
               page.addTargetRequests(urls);
               SetSystemProperty.updateProperties("noticeDate",date.get(0));
           }
           urlSum=urls;
           titleSum=titles;
           page.setSkip(true);
        }
        else {
            //标题
            String title="";
            String urlBefore = page.getUrl().toString();
            String url = urlBefore.substring(urlBefore.length()-25);
            for (int temp=0 ;temp<urlSum.size();temp++){
                String urlsTerr = urlSum.get(temp);
                if (url.equals(urlsTerr.substring(urlsTerr.length()-25))){
                    title= titleSum.get(temp);
                }
            }

            if (!RegexUtil.isContain(title,noticeRegex)){
                page.setSkip(true);
               return;
            }

            page.putField("title",title);

            page.putField("html",page.getHtml().xpath("//div[@class='content_article']"));
            page.putField("html_title",page.getHtml().xpath("//h1[@class='content_title']"));
            //通过Url解析出当前页的html名称，方便后用
            String[] htmlName = page.getUrl().toString().split("/");
            page.putField("htmlname",htmlName[htmlName.length-1]);
            //图片地址

            List<String> fileUrl = page.getHtml().xpath("//div[@class='content_article']").xpath("img/@src").all();

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
                //广东应急营业厅
                .addUrl("http://www.gdsafety.gov.cn/gdyjglt/gkai/list_2.shtml")
                .thread(1)
                .run();

    }

    }
