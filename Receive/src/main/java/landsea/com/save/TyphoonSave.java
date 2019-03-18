package landsea.com.save;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import landsea.com.domain.Base;
import landsea.com.domain.ForeCast;
import landsea.com.domain.Typhoon;
import landsea.com.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import java.util.Date;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/01/11 17:19
 * @see landsea.com.util
 */
public class TyphoonSave {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static Connection conn = null;
    private static PreparedStatement ps = null;
    private static PreparedStatement ps1 = null;
    private static PreparedStatement ps2 = null;
    private static String sql = "insert into TYPHOONINFO (ID,NUM_CHN,NUM_EN,NAME_CHN,NAME_EN,STATUS,TYPH_YEAR,CREATETIME,OPTMARK) values(?,?,?,?,?,?,?,?,?)";
    private static String typhoon_sql = "insert into TYPHOONFORECAST (ID,TYPHOONINFOID,FORECASTSTATION,TYPHOONTYPE,LATITUDE,LONGITUDE,GRADE," +
            "WINDSPEED,MOVESPEED,DIRECT,PRESSURE,POWER_HIGH,POWER_HIGH_RANGE,POWER_MIDDLE,POWER_MIDDLE_RANGE,POWER_LOW,POWER_LOW_RANGE," +
            "FORECASTTIME,CREATETIME) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static String forecast_sql = "insert into TYPHOONFORECASTINFO (id,center_latitude,center_longtitude,max_speed,center_pressure,FORECASTID," +
            "sort_id,arrival_time,createtime) values(?,?,?,?,?,?,?,?,?)";


    public String Save (Base base){

        conn = DBUtil.getConn();
         try { ps = conn.prepareStatement(sql);
            List<Typhoon> typhoons =base.getTyphoons();
            ps.setString(1,base.getID());
            ps.setString(2,base.getNUM_CHN());
            ps.setString(3,base.getNUM_EN());
            ps.setString(4,base.getNAME_CHN());
            ps.setString(5,base.getNAME_EN());
            ps.setDouble(6,base.getSTATUS());
            ps.setDouble(7,base.getTYPH_YEAR());
            java.util.Date cur_time=new Date();
            ps.setTimestamp(8,timeChange(cur_time));
             ps.setString(9,"");
            ps1=conn.prepareStatement(typhoon_sql);
            for (Typhoon typhoon :typhoons){
                ps1.setString(1,typhoon.getID());
                ps1.setString(2,typhoon.getTYPHOONINFOID());
                ps1.setString(3,typhoon.getFORECASTSTATION());
                ps1.setString(4,typhoon.getTYPHOONTYPE());
                ps1.setDouble(5,typhoon.getLATITUDE());
                ps1.setDouble(6,typhoon.getLONGITUDE());
                ps1.setString(7,typhoon.getGRADE());
                ps1.setDouble(8,typhoon.getWINDSPEED()*1.944);
                ps1.setDouble(9,typhoon.getMOVESPEED()/1.852);
                ps1.setString(10,typhoon.getDIRECT());
                ps1.setDouble(11,typhoon.getPRESSURE());
                String lowLevel = null;
                String highLevel = null;
                String middleLevel = null;
                if (typhoon.getPOWER_LOW()!=null){
                    lowLevel = toLevel(typhoon.getPOWER_LOW());
                }
                if (typhoon.getPOWER_MIDDLE()!=null){
                    middleLevel = toLevel(typhoon.getPOWER_MIDDLE());
                }
                if (typhoon.getPOWER_HIGH()!=null){
                    highLevel = toLevel(typhoon.getPOWER_HIGH());
                }
                ps1.setString(12,highLevel);
                ps1.setString(13,typhoon.getPOWER_HIGH_RANGE());
                ps1.setString(14,middleLevel);
                ps1.setString(15,typhoon.getPOWER_MIDDLE_RANGE());
                ps1.setString(16,lowLevel);
                ps1.setString(17,typhoon.getPOWER_LOW_RANGE());

                java.util.Date forecasttime=typhoon.getFORECASTTIME();
                java.util.Date curr_time=new Date();
                ps1.setTimestamp(18, timeChange(forecasttime));
                ps1.setTimestamp(19,timeChange(curr_time));
                ps2 = conn.prepareStatement(forecast_sql);
                try {
                    ps1.execute();
                }catch (SQLServerException sqlServerException){
                    System.out.println("键值重复"+sqlServerException);
                    continue;
                }
                List<ForeCast> foreCasts = typhoon.getForeCasts();

                for (ForeCast foreCast: foreCasts ){
                    ps2.setString(1, UUID.randomUUID().toString());
                    ps2.setDouble(2,foreCast.getCenter_latitude());
                    ps2.setDouble(3,foreCast.getCenter_longtitude());
                    ps2.setDouble(4,foreCast.getMax_speed()*1.944);
                    ps2.setDouble(5,foreCast.getCenter_pressure());
                    ps2.setString(6,foreCast.getFORECASTID());
                    ps2.setInt(7,foreCast.getSort_id());
                    Date arrival_time=foreCast.getArrival_time();
                    Date create_time=new Date();
                    ps2.setTimestamp(8, timeChange(arrival_time));
                    ps2.setTimestamp(9,timeChange(create_time));

                    ps2.execute();
                }

            }


            try {
                ps.execute();
            }catch (SQLServerException sqlServerException){
                System.out.println("键值重复"+sqlServerException);
            }
            conn.commit();
            ps.close();
            ps1.close();
            ps2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(conn);
        }

        return "sucess";
    }

    public String toLevel (String typhoonspeed){
        double level = 0;
        try {
            level = Double.parseDouble(typhoonspeed)*0.5144; 
        }catch (Exception e){
            System.out.println(e);
        }

        String levelResult = typhoonspeed+"KT";
        if(level>8.0&&level<=10.7){
            levelResult="5";
        } else if(level>10.7&&level<=13.8){
            levelResult="6";
        }
        else if(level>13.8&&level<=17.1){
            levelResult="7";
        }
        else if(level>17.1&&level<=20.7){
            levelResult="8";
        }
        else if(level>20.7&&level<=24.4){
            levelResult="9";
        }
        else if(level>24.4&&level<=28.4){
            levelResult="10";
        }
        else if(level>28.4&&level<=32.6){
            levelResult="11";
        }
        else if(level>32.6){
            levelResult="12";
        }
        return levelResult;
    }

    public Timestamp timeChange(Date date) throws ParseException {
        java.sql.Timestamp resultTime = null;
  
        try{
        resultTime=new java.sql.Timestamp(date.getTime());
            return resultTime;
        }catch (Exception e){
            System.out.println(e);
        }finally {
            return resultTime;
        }
    }    
}
