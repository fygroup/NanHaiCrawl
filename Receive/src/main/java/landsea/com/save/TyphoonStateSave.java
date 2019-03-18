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
public class TyphoonStateSave {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static Connection conn = null;
    private static PreparedStatement ps = null;
    private static PreparedStatement ps_AUTO = null;

    private static String sql = "insert into TYPHOONINFO (ID,NUM_CHN,NUM_EN,NAME_CHN,NAME_EN,STATUS,TYPH_YEAR,CREATETIME,OPTMARK) values(?,?,?,?,?,?,?,?,?)";


    public boolean saveStopTyphoon(String base) {
        base= base.replace("\"","");
        boolean execute = false;
        conn = DBUtil.getConn();
        try {
            String[] baseInfo = base.split(",");
                String AUTOID_sql = "select AUTONO from TYPHOONINFO where ID = ?";
                ps_AUTO = conn.prepareStatement(AUTOID_sql);
                ps_AUTO.setString(1, baseInfo[0]);
                ResultSet rs = ps_AUTO.executeQuery();
                String autoID = "";
                if (rs.next()) {//rs.next();
                    autoID = rs.getString(1);
                    System.out.println(autoID);
                }
                ps = conn.prepareStatement(sql);
                ps.setString(1, "U_" + baseInfo[0]);
                ps.setString(2, baseInfo[3]);
                ps.setString(3, baseInfo[4]);
                ps.setString(4, baseInfo[2]);
                ps.setString(5, baseInfo[1]);
                ps.setDouble(6, 0);
                ps.setDouble(7, 2019);
                ps.setTimestamp(8, timeChange(new Date()));
                ps.setString(9, "U_" + String.valueOf(autoID));
            System.out.println("test");
            execute = ps.execute();
            conn.commit();
            ps.close();
            ps_AUTO.close();
            System.out.println(execute);
        } catch (Exception e) {
        }
        return execute;
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
