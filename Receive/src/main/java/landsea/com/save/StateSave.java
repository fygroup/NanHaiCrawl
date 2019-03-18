package landsea.com.save;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import landsea.com.domain.State;
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

import static org.jsoup.nodes.Document.OutputSettings.Syntax.html;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/01/25 11:08
 * @see landsea.com.save
 */
public class StateSave {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static Connection conn = null;
    private static PreparedStatement ps = null;



    public boolean save (State state) {

        conn = DBUtil.getConn();
        //向地区表中插入数据
        String sql = "update CRAWLSTATE set CRAWLTIME=?,DATASTATE=?,REMARK=?,DATATIME=? where ID =?";

        try {
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1,timeChange(state.getCRAWLTIME()));
            ps.setInt(2,state.getDATASTATE());
            ps.setString(3,state.getREMARK());
            ps.setString(4,state.getDATATIME());
            ps.setString(5,state.getKIND());

            ps.execute();
            conn.commit();
            ps.close();
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }finally {
            DBUtil.close(conn);
        }

        return true;

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
