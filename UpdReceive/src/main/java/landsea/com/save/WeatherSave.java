package landsea.com.save;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import landsea.com.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/01/11 17:19
 * @see landsea.com.util
 */
public class WeatherSave {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static Connection conn = null;
    private static PreparedStatement ps = null;
    private static PreparedStatement ps1 = null;
    public String save (JSONObject base){

        conn = DBUtil.getConn();
        //向地区表中插入数据
        String sql = "insert into WEATHERAREA (AREA_ID,NAME_CHS,NameEN,FORECAST_TYPE,BEGIN_TIME,END_TIME,type,TIME,DISCRIBE) values(?,?,?,?,?,?,?,?,?)";
        try {
            ps = conn.prepareStatement(sql);
            JSONArray area;
            if(base.getIntValue("Type")==3){
                area=base.getJSONArray("LineForecast");
            } else {
                area=base.getJSONArray("SeaArea");
            }
            for (int i=0;i<area.size();i++){
                // 遍历 jsonArray 数组，把每一个对象转成 json 对象
                JSONObject area_item = area.getJSONObject(i);

                //取值时用到的时间
                String beginTime =getYear()+area_item.getString("EffectiveTime_Begin");
                String endTime = getYear()+area_item.getString("EffectiveTime_End");
                String curTime=base.getString("PublishTime");

                //将地区id值中‘，’去掉
                String areaID = base.getString("ForecastorID").replace(",","");
                //id值由气象唯一标识符+区域类型+英文名组成，作为当日该地区预报的唯一标识
                String ID = OnlyNum(curTime)+base.getIntValue("Type")+area_item.getString("NameEN");
                System.out.println(ID);

                //ID值为地区id+类型+地区英文名
                ps.setString(1,ID);
                ps.setString(2,area_item.getString("NameCHS"));
                ps.setString(3,area_item.getString("NameEN"));
                ps.setString(4,area_item.getString("ForecastType"));
                ps.setTimestamp(5,timeChange(beginTime));
                ps.setTimestamp(6,timeChange(endTime));
                ps.setInt(7,base.getIntValue("Type"));
                ps.setTimestamp(8,timeChange(curTime));
                ps.setString(9,base.getJSONObject("Discribe").getString("DiscribeCHS"));
                System.out.println("第"+i+"组数据-----------");



                JSONArray forecastContent=area_item.getJSONArray("ForecastContent");
                String forecastSql = "insert into WEATHERFORECAST (for_ID,TIME,WIND_DIRECTION,WIND_SPEED,WAVE_HEIGHT,WAVE_DIRECTION,MAX_WAVE_HEIGHT,VISIBILITY,WATER_TEMPERATURE,AREA_ID) values(?,?,?,?,?,?,?,?,?,?)";
                ps1= conn.prepareStatement(forecastSql);
                for (int temp= 0;temp<forecastContent.size();temp++){
                    JSONObject forcastItem =  forecastContent.getJSONObject(temp);
                    String forecastTime = getYear()+getMonth()+forcastItem.getString("time");
                    System.out.println(forecastTime);
                    ps1.setString(1,UUID.randomUUID().toString());
                    ps1.setTimestamp(2,toForecastTime(forecastTime));
                    ps1.setString(3,forcastItem.getString("WindDirection"));
                    ps1.setString(4,forcastItem.getString("WindSpeedStr"));
                    ps1.setString(5,forcastItem.getString("WaveHeightStr"));
                    ps1.setString(6,forcastItem.getString("WaveDirectionStr"));
                    ps1.setString(7,forcastItem.getString("MaxWaveHeightStr"));
                    ps1.setString(8,forcastItem.getString("Visibility"));
                    ps1.setString(9,forcastItem.getString("WaterTemperature"));
                    ps1.setString(10,ID);
                    //执行ps1
                    try {
                        boolean result = ps1.execute();
                    }catch (SQLServerException sqlServerException){
                        System.out.println("键值重复"+sqlServerException);
                    }

                }
                //执行ps
                try {
                    boolean flag = ps.execute();
                }catch (SQLServerException sqlServerException){
                    System.out.println("键值重复"+sqlServerException);
                }
        }

        conn.commit();
        ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }  finally {
            DBUtil.close(conn);
        }

        return "sucess";
    }

    /**
     * 获取当前年份
     * @return
     */
    public String getYear(){
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR))+"年";
        return year;
    }
    public String getMonth(){
        Calendar calendar = Calendar.getInstance();
        String month = String.valueOf(calendar.get(Calendar.MONTH)+1)+"月";
        return month;
    }

    /**
     * java的util时间转成SqlServer的datetime
     * @param date
     * @return
     * @throws ParseException
     */
    public Timestamp timeChange(String date) throws ParseException {
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy年MM月dd日HH时");
        Date flag_date =  sdf.parse(date);
        java.sql.Timestamp resultTime=new java.sql.Timestamp(flag_date.getTime());
        return resultTime;
    }

    public Timestamp toForecastTime(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date flag_date =  sdf.parse(date);
        java.sql.Timestamp resultTime=new java.sql.Timestamp(flag_date.getTime());
        return resultTime;
    }

    public String OnlyNum (String pm){
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(pm);
        return m.replaceAll("").trim();
    }

    public static void main(String[] args) {
        //test
        System.out.println(new WeatherSave().getMonth());
        System.out.println(new WeatherSave().getYear());
    }
}
