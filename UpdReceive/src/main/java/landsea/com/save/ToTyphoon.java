package landsea.com.save;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import landsea.com.domain.Base;
import landsea.com.domain.ForeCast;
import landsea.com.domain.Typhoon;
import org.apache.commons.lang.StringEscapeUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <Description>
 *解析台风的json格式，转换成java对象
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/01/11 16:55
 * @see landsea.com.save
 */
public class ToTyphoon {

    public Base toBase(String json_str){
        json_str = StringEscapeUtils.unescapeJavaScript(json_str);
        System.out.println(json_str);
        Base typhoonBase = new Base();

        try {
            List<Typhoon> typhoonList =new ArrayList<>();

            String json_list=json_str.substring(58,json_str.length()-2);
            String[] base=json_list.split(",",9);
            for ( int i =0 ;i<base.length-1;i++){
                base[i]= base[i].replace("\"", "");
                System.out.println(base[i]);
            }
            //String baseId= base[0].substring(base[0].length()-15);
            typhoonBase.setID(OnlyNum(base[0]));
            typhoonBase.setNUM_EN(base[3]);
            typhoonBase.setNUM_CHN(base[4]);
            typhoonBase.setNAME_CHN(base[2]);
            typhoonBase.setNAME_EN(base[1]);
            if(base[7].equals("start")){
                typhoonBase.setSTATUS(1);
            }else {
                typhoonBase.setSTATUS(0);
            }

            typhoonBase.setTYPH_YEAR(getYear());
            typhoonBase.setCREATETIME(new Date());

            String forecasts=base[8].substring(1,base[8].length()-1);
            System.out.println(forecasts);
            String regex="分\"]],";
            String[] forecast = forecasts.split(regex);
            for (int i = 0;i<forecast.length;i++){

                List<ForeCast> typhoonForeCasts = new ArrayList<>();
                String[] forecastInfo = forecast[i].split(",",11);
                String [] forecastItems ;
                for (int j=0 ;j<forecastInfo.length-1;j++){
                    forecastInfo[j]= forecastInfo[j].replace("\"", "");
                }

                Typhoon typhoon = new Typhoon();
                typhoon.setID(typhoonBase.getID()+forecastInfo[1]);
                typhoon.setTYPHOONINFOID(typhoonBase.getID());
                typhoon.setFORECASTSTATION("BABJ");
                Long Timestamp = Long.parseLong(forecastInfo[2]);
                typhoon.setFORECASTTIME(new Date(Timestamp));
                typhoon.setTYPHOONTYPE(forecastInfo[3]);
                typhoon.setLATITUDE(Double.parseDouble(forecastInfo[5]));
                typhoon.setLONGITUDE(Double.parseDouble(forecastInfo[4]));
                typhoon.setWINDSPEED(Double.parseDouble(forecastInfo[7]));
                typhoon.setMOVESPEED(Double.parseDouble(forecastInfo[9]));
                typhoon.setDIRECT(forecastInfo[8]);
                typhoon.setPRESSURE(Double.parseDouble(forecastInfo[6]));
                typhoon.setCREATETIME(new Date());


                forecastItems = forecastInfo[10].split("BABJ",2);
                String[] range = forecastItems[0].split("],");
                if(range.length>1&&forecastItems[0].length()>7){
                    String[] rangeItem =range[0].split(",");
                    String power =rangeItem[0].substring(3,5);
                    typhoon.setPOWER_LOW(power);
                    String rangeText= rangeItem[1]+"|"+rangeItem[2]+"|"+rangeItem[3]+"|"+rangeItem[4];
                    typhoon.setPOWER_LOW_RANGE(rangeText);
                }
                if(range.length>2){
                    String[] rangeItem =range[1].split(",");
                    String power =rangeItem[0].substring(2,4);
                    typhoon.setPOWER_MIDDLE(power);
                    String rangeText= rangeItem[1]+"|"+rangeItem[2]+"|"+rangeItem[3]+"|"+rangeItem[4];
                    typhoon.setPOWER_MIDDLE_RANGE(rangeText);
                }
                if(range.length>3){
                    String[] rangeItem =range[2].split(",");
                    String power =rangeItem[0].substring(2,4);
                    typhoon.setPOWER_HIGH(power);
                    String rangeText= rangeItem[1]+"|"+rangeItem[2]+"|"+rangeItem[3]+"|"+rangeItem[4];
                    typhoon.setPOWER_HIGH_RANGE(rangeText);
                }


                //预测详情
                String[] forecast_content = forecastItems[1].split("]}",2);
                String forecast_text = forecast_content[0];
                String forecast_time = forecast_content[1];
                String[] forecast_items = forecast_text.split("],");
                int sort_id= 0;
                int seq_temp=0;
                for (String temp: forecast_items){
                    ForeCast foreCast =new ForeCast();
                    temp= temp.replace("\"", "");
                    String[] forecastItem = temp.split(",");
                    int Separate = Integer.parseInt(OnlyNum(forecastItem[0]));
                    if (Separate>seq_temp){
                        seq_temp=Separate;
                        Date ArrivalTime = addDateMinut(forecastItem[1],Separate+8);
                        foreCast.setArrival_time(ArrivalTime);
                        foreCast.setCenter_latitude(Double.parseDouble(forecastItem[3]));
                        foreCast.setCenter_longtitude(Double.parseDouble(forecastItem[2]));
                        foreCast.setCenter_pressure(Double.parseDouble(forecastItem[4]));
                        foreCast.setCreatetime(new Date());
                        foreCast.setMax_speed(Double.parseDouble(forecastItem[5]));
                        foreCast.setSort_id(sort_id);
                        foreCast.setFORECASTID(typhoon.getID());
                        typhoonForeCasts.add(foreCast);
                        sort_id++;
                    }
                }
                String[] forecast_time_items = forecast_time.split(",");
                for (String temp: forecast_time_items){
                    temp= temp.replace("\"", "");
                }

                typhoon.setForeCasts(typhoonForeCasts);
                typhoonList.add(typhoon);
                //全部


            }

            typhoonBase.setTyphoons(typhoonList);
            JSON Json = (JSON) JSONObject.toJSON(typhoonBase);
        }catch (Exception e){
            System.out.println(e);
        }
        return  typhoonBase;
    }

    public String OnlyNum (String pm){
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(pm);
        return m.replaceAll("").trim();
    }

    public  Date addDateMinut(String day, int hour){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = null;
        try {
            date = format.parse(day);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (date == null){
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hour);// 24小时制
        date = cal.getTime();
        // cal = null;
        return date;

    }

    public double getYear(){
        Calendar calendar = Calendar.getInstance();
        double year = calendar.get(Calendar.YEAR);
        return year;
    }


}
