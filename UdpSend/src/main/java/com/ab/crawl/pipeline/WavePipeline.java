package com.ab.crawl.pipeline;


import com.ab.crawl.util.FtpUtil;
import com.ab.crawl.util.WaveDownload;
import com.ab.crawl.util.WindDownload;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/01/23 14:41
 * @see com.ab.crawl.pipeline
 */
public class WavePipeline implements Pipeline {
    WaveDownload waveDownload=new WaveDownload();
    WindDownload windDownload=new WindDownload();
    private final static String HOUR_FORMAT = "yyyyMMddHH";
    @Override
    public void process(ResultItems resultItems, Task task) {
        String state= resultItems.get("state").toString();
        System.out.println(state);
        if (!state.equals("1")&&!state.equals("2")){
            System.out.println("没有更新值");
            return;
        }
        int dateLength;
        try {
             dateLength= Integer.parseInt(resultItems.get("length").toString());
        }catch (Exception e){
            dateLength = 60;
            System.out.println(e);
        }
        System.out.println("共有"+dateLength+"个数据");
        List<String> urlTime = new ArrayList<>();
            for (int temp=0;temp<dateLength;temp++){
                System.out.println(addCurrentHour(temp));
                urlTime.add(addCurrentHour(temp)+".png");
            }
        for (String temp:urlTime){
            try {
                if (state.equals("1")){
                    waveDownload.downloadFile("http://szyb.hyyb.org/product/WW3/NWP//"+temp,temp);
                    System.out.println("下载海浪:"+temp);
                }else if (state.equals("2")){
                    windDownload.downloadFile("http://szyb.hyyb.org/product/WRF/SCS//"+temp,temp);
                    System.out.println("下载海风:"+temp);
                }else {
                    System.out.println("没有判断出海风海浪类型，无法下载");
                    System.out.println(state);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("TESET1");
        FtpUtil ftpUtil = new FtpUtil();
        System.out.println("TESET1");

        //循环上传
        for (String item :urlTime){
            System.out.println(item);
            try {
                System.out.println("TESET1");

                if (state.equals("1")){
                    ftpUtil.wavePutFile(item);
                    System.out.println("上传海浪");
                }else if (state.equals("2")){
                    ftpUtil.windPutFile(item);
                    System.out.println("上传海风");
                }

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("没有类型，无法上传");
                e.printStackTrace();
            }
        }
    }

    /**
     *用来遍历打印时间如：2019013108
     * @param count
     * @return
     */
    public static String addCurrentHour(int count) {
        SimpleDateFormat sdf = new SimpleDateFormat(HOUR_FORMAT);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,8);
        cal.add(Calendar.HOUR_OF_DAY,count);
        return sdf.format(cal.getTime());
    }

}
