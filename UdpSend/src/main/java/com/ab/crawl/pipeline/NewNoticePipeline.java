package com.ab.crawl.pipeline;

import com.ab.crawl.udp.Send;
import com.ab.crawl.util.FileUtil;
import com.ab.crawl.util.FtpUtil;
import com.ab.crawl.util.UrlFile;
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
    FileUtil fileUtil = new FileUtil();
    UrlFile urlFile = new UrlFile();
    FtpUtil ftpUtil = new FtpUtil();
    @Override
    public void process(ResultItems resultItems, Task task) {
        // 获取爬取下来的内容
        String div = resultItems.getAll().get("html").toString();
        String htmlName = resultItems.getAll().get("htmlname").toString();
        String time = resultItems.getAll().get("time").toString();
        String title =  resultItems.getAll().get("title").toString();
        String fileUrl  = resultItems.getAll().get("fileurl").toString();

        System.out.println("附件地址"+fileUrl);
        //将div写入html文本
        fileUtil.NewFile(div,htmlName);
        ftpUtil.putFile(htmlName);


        if (fileUrl.length()>5){
            System.out.println(fileUrl);
            //将附件url根据“，”拆开方便使用
            String[] fileurls = fileUrl.substring(1,fileUrl.length()-1).split(",");
            System.out.println("建立list1");
            List<String> realUrl = new ArrayList<>();
            System.out.println("建立list2");
            String tempUrl=null;
            for (int temp=0;temp<fileurls.length;temp++){
                System.out.println("下载附件"+temp);
                try {
                    //下载
                    tempUrl=urlFile.downloadFile(fileurls[temp]);
                    if (!tempUrl.equals("")){
                        realUrl.add(tempUrl);
                        System.out.println("realUrl[temp]"+realUrl.get(temp));
                    }

                } catch (Exception e) {
                    System.out.println("未下载"+e);
                    e.printStackTrace();
                }
            }
            //循环上传
            for (String item :realUrl){
                System.out.println(item);

                try {
                    ftpUtil.putAnnex(item);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("ftp未能成功上传");
                    e.printStackTrace();
                }
            }
        }

        System.out.println(time);
        //解析date为时间格式，方便传值
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        try {
             date1 =sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(htmlName);
        List<String> sendList = new ArrayList<>();
        sendList.add(htmlName);
        sendList.add(time);
        sendList.add(title);
        System.out.println(sendList);
        try {
            new Send().UdpSend(htmlName+","+time+","+title);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Pipeline"+"结束");
    }
}
