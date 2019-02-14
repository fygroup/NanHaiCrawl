package com.ab.crawl.pipeline;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import com.ab.crawl.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;


/** 
* @ClassName: TyphoonZjwaterDataPipeline 
* @Description: 处理台风数据(浙江水利局)
* @author gaow
* @date 2018年11月15日 上午9:06:47 
*  
*/
public class TyphoonZjwaterDataPipeline implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    private static Connection conn = null;
	private static PreparedStatement ps = null;
	
    @Override
    public  void process(ResultItems resultItems, Task task) {
    	for(Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            if(entry.getKey().equals("content")) {
            	JSONObject obj = (JSONObject)entry.getValue();
                if(obj != null) {
                	String tfid = obj.getString("tfid");
                    create(tfid,obj.toJSONString());
                }
            }
        }
    }
    /** 
    * @Title: create 
    * @Description: 保存台风
    * @param @param id
    * @param @param typhoon
    * @param @return    设定文件 
    * @return Integer    返回类型 
    * @throws 
    */
    public static Integer create(String id,String typhoon) {
		conn = DBUtil.getConn();
	    int i = 0;
	    String sql = "insert into typhoonzj (id,data) values(?,?)";
	    try {
	        ps = conn.prepareStatement(sql);
	        ps.setString(1, id);
	        ps.setString(2, typhoon.toString());
	        i = ps.executeUpdate();
	        conn.commit();
	        ps.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }finally {
	    	DBUtil.close(conn);
		}
	    return i;
	}
}
