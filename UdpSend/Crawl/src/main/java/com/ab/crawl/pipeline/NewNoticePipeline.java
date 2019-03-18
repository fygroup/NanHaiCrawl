package com.ab.crawl.pipeline;

import com.ab.crawl.processor.ReefProcessor;
import com.ab.crawl.udp.Send;
import com.ab.crawl.util.FileUtil;
import com.ab.crawl.util.FtpUtil;
import com.ab.crawl.util.UrlFile;
import org.apache.log4j.Logger;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/01/23 14:41
 * @see com.ab.crawl.pipeline
 */
public class NewNoticePipeline implements Pipeline {
    private static org.apache.log4j.Logger logger = Logger.getLogger(ReefProcessor.class);
    private final FileUtil fileUtil = new FileUtil();
    private final UrlFile urlFile = new UrlFile();
    private final Send send = new Send();
    @Override
    public void process(ResultItems resultItems, Task task) {
        String div="";
        // 获取爬取下来的内容
        div= resultItems.getAll().get("html").toString();
        String htmlName = resultItems.getAll().get("htmlname").toString();
        String time = resultItems.getAll().get("time").toString();
        String title =  resultItems.getAll().get("title").toString();
        String fileUrl  = resultItems.getAll().get("fileurl").toString();
        String html_title = resultItems.getAll().get("html_title").toString();
        //将div写入html文本
        htmlName="announcement/"+htmlName;
        String html_all= "<html><head><meta charset=\"utf-8\"><title></title></head>";
        html_all+=html_title+div;
        html_all+="<body></body></html>";
        fileUtil.NewFile(html_all,htmlName);
        try {
            FtpUtil ftpUtil = new FtpUtil();
            ftpUtil.putFile(htmlName);
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (fileUrl.length()>5){
            //将附件url根据“，”拆开方便使用
            String[] fileurls = fileUrl.substring(1,fileUrl.length()-1).split(",");
            List<String> realUrl = new ArrayList<>();
            String tempUrl=null;
            for (int temp=0;temp<fileurls.length;temp++){
                try {
                    //下载
                    tempUrl=urlFile.downloadFile(fileurls[temp]);
                    if (!tempUrl.equals("")){
                        realUrl.add(tempUrl);
                    }
                } catch (Exception e) {
                    System.out.println(temp+"未下载"+e);
                    e.printStackTrace();
                }
            }
            //循环上传
            for (String item :realUrl){
                try {
                    FtpUtil.getInstance().putAnnex(item);
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println(item+"文件ftp未能成功上传");
                    e.printStackTrace();
                }
            }
        }

        //解析date为时间格式，方便传值
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        try {
             date1 =sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<String> sendList = new ArrayList<>();
        sendList.add(htmlName);
        sendList.add(time);
        sendList.add(title);
        try {
            boolean udpState = send.UdpSend(htmlName+","+time+","+title,8813);
            System.out.println(udpState);
        } catch (Exception e) {
            logger.error("udp传输错误");
            e.printStackTrace();
        }
    }
}
