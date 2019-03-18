package com.ab.crawl.pipeline;


import com.ab.crawl.processor.ReefProcessor;
import com.ab.crawl.udp.Send;
import com.ab.crawl.udp.State;
import com.ab.crawl.util.*;
import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
public class WavePipeline implements Pipeline {
    WaveDownload waveDownload=new WaveDownload();
    WindDownload windDownload=new WindDownload();
    private final FileUtil fileUtil = new FileUtil();
    private static org.apache.log4j.Logger logger = Logger.getLogger(ReefProcessor.class);
    private final static String HOUR_FORMAT = "yyyyMMddHH";
    private static String currDate;

    @Override
    public void process(ResultItems resultItems, Task task) {

        //1海浪2海风
        String state= resultItems.get("state").toString();
        int dateLength;
        try {
            dateLength= Integer.parseInt(resultItems.get("length").toString());
        }catch (Exception e){
            dateLength = 60;
            System.out.println(e);
        }
        currDate = resultItems.get("currdate").toString();
        String arrStr="";
        String dateStr="";
        if (!state.equals("1")&&!state.equals("2")){
            System.out.println("没有更新值");
            return;
        }
        //创建状态对象，结尾udp发送状态
        State stateWave = new State();
        stateWave.setREMARK("图片下载并上传成功");
        stateWave.setCRAWLTIME(new Date());
        stateWave.setDATATIME(currDate);
        stateWave.setDATASTATE(1);


        System.out.println("共有"+dateLength+"个数据");
        List<String> urlTime = new ArrayList<>();
            for (int temp=0;temp<dateLength;temp++) {
                arrStr += makeUrl(Integer.parseInt(state),temp);
                urlTime.add(addCurrentHour(temp) + ".png");
                dateStr +="<option value=\""+temp+"\">"+addCurrentHour(temp).substring(6,8)+"日"+addCurrentHour(temp).substring(8,10)+"时</option>";
            }

        for (String temp:urlTime){
            try {
                if (state.equals("1")){
                    waveDownload.downloadFile("http://szyb.hyyb.org/product/WW3/SCS//"+temp,temp);
                }else if (state.equals("2")){
                    windDownload.downloadFile("http://szyb.hyyb.org/product/WRF/SCS//"+temp,temp);
                }else {
                    System.out.println("没有判断出海风海浪类型，无法下载");
                    System.out.println(state);
                }

            } catch (Exception e) {
                logger.error("下载图片"+temp+"失败");
                stateWave.setDATASTATE(0);
                stateWave.setREMARK("下载图片失败");
            }
        }

        FtpUtil ftpUtil = new FtpUtil();

        //循环上传
        for (String item :urlTime){
            try {
                if (state.equals("1")){
                    ftpUtil.wavePutFile(item);
                }else if (state.equals("2")){
                    ftpUtil.windPutFile(item);
                }

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("ftp上传"+item+"失败");
                stateWave.setDATASTATE(0);
                stateWave.setREMARK("上传图片失败");
            }
        }

        if (state.equals("1")){
            String thirdStr="/attach/product/WW3/SCS//"+ addCurrentHour(0)+".png";
            fileUtil.NewFile( MakeHtmlUtil.makeHtml(arrStr,dateStr,thirdStr),"wave.html");
            ftpUtil.putFile("wave.html");
            stateWave.setKIND("wave");
            SetSystemProperty.updateProperties("wave",currDate);
        }else if (state.equals("2")){
            String thirdStr="/attach/product/WRF/SCS//"+ addCurrentHour(0)+".png";
            fileUtil.NewFile( MakeHtmlUtil.makeHtml(arrStr,dateStr,thirdStr),"wind.html");
            ftpUtil.putFile("wind.html");
            stateWave.setKIND("wind");
            SetSystemProperty.updateProperties("wind",currDate);
        }
        Send send = new Send();
        String sendStr = JSON.toJSONString(stateWave);
        System.out.println(sendStr);
        boolean b = send.UdpSend(sendStr, 8815);
        if (b==false){
            logger.error("udp发送失败");
            System.out.println("udp发送失败");
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
        cal.set(Calendar.YEAR,Integer.parseInt(currDate.substring(0,4)));

        cal.set(Calendar.MONTH,Integer.parseInt(currDate.substring(4,6))-1);
        cal.set(Calendar.DAY_OF_MONTH,Integer.parseInt(currDate.substring(6,8)));
        cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(currDate.substring(8,10)));
        cal.add(Calendar.HOUR_OF_DAY,count);
        return sdf.format(cal.getTime());
    }



    public static String makeUrl(int state,int count){
        if (state==1){
            return   "arr[" + count + "]='/attach/product/WW3/SCS//" + addCurrentHour(count) + ".png';";
        }else if (state==2){
            return  "arr[" + count + "]='/attach/product/WRF/SCS//" + addCurrentHour(count) + ".png';";
        }
        return "";

    }

}
