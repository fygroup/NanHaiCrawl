package landsea.com.save;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import landsea.com.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/01/25 11:08
 * @see landsea.com.save
 */
public class NoticeSave {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static Connection conn = null;
    private static PreparedStatement ps = null;



    public String save (String notice) {

        conn = DBUtil.getConn();
        //向地区表中插入数据
        String sql = "insert into WNOTICE (ID,time,title,createtime) values(?,?,?,?)";
        String[] items = notice.split(",");

        try {
            ps = conn.prepareStatement(sql);
            String html = items[0].substring(13);
            String time = items[1];
            String title= items[2];
            ps.setString(1,html);
            ps.setTimestamp(2,timeChange(time));
            ps.setString(3,title);
            ps.setTimestamp(4,timeChange(new Date()));
            ps.execute();
            conn.commit();
            ps.close();
        } catch (Exception e) {
            System.out.println(e);
        }finally {
            DBUtil.close(conn);
        }

        return "sucess";

    }
    public Timestamp timeChange(String date) throws ParseException {
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date flag_date =  sdf.parse(date);
        java.sql.Timestamp resultTime=new java.sql.Timestamp(flag_date.getTime());
        return resultTime;
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
