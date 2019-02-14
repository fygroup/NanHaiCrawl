package com.ab.crawl.pipeline;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.ab.crawl.util.DBUtil;
import com.ab.crawl.util.DateUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * @ClassName: TyphoonZjwaterListPipeline
 * @Description: 处理台风列表数据
 * @author gaow
 * @date 2018年11月21日 上午9:06:47
 * 
 */
public class TyphoonZjwaterListPipeline implements Pipeline {

	private static Connection conn = null;
	private static PreparedStatement ps = null;

	@Override
	public void process(ResultItems resultItems, Task task) {
		for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
			if (entry.getKey().equals("content")) {
				JSONArray arr = (JSONArray) entry.getValue();
				if (arr != null && arr.size()>0) {
					for (Object obj : arr) {
						JSONObject o = (JSONObject) obj;
						if(("TD").equals(o.getString("enname"))||!NumberUtils.isNumber(o.getString("tfid"))) {
							continue;
						}
						create(o.getString("tfid"), o.getString("enname"), o.getString("name"),
								o.getString("starttime"), o.getString("endtime"), o.getString("isactive"), o.getString("warnlevel"));
					}

					String year = ((JSONObject) arr.get(0)).getString("tfid").substring(0, 4);
					System.out.println(year + "年台风列表下载完成");
					if (year.equals("2018")) {
						Spider.create(null).stop();
						System.out.println("结束运行");
					}
				}
			}
		}
	}


	/** 
	* @Title: create 
	* @Description: 保存浙江水利局台风列表
	* @param @param tfid
	* @param @param enname
	* @param @param name
	* @param @param starttime
	* @param @param endtime
	* @param @param isactive
	* @param @param warnlevel
	* @param @return    设定文件 
	* @return Integer    返回类型 
	* @throws 
	*/
	public static Integer create(String tfid, String enname, String name, String starttime, String endtime,
			String isactive,String warnlevel) {
		conn = DBUtil.getConn();
		int i = 0;
		String sql = "insert into typhoonZjlist (tfid,enname,name,starttime,endtime,isactive,warnlevel) values(?,?,?,?,?,?,?)";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, tfid);
			ps.setString(2, enname);
			ps.setString(3, name);
			ps.setDate(4,
					new Date(DateUtils.str2Date(starttime, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).getTime()));
			ps.setDate(5,
					new Date(DateUtils.str2Date(endtime, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).getTime()));
			ps.setString(6, isactive);
			ps.setString(7, warnlevel);
			i = ps.executeUpdate();
			conn.commit();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn);
		}
		return i;
	}

	public static JSONArray sort(JSONArray arr, final String key) {
		JSONArray res = new JSONArray();
		List<JSONObject> jsonValue = new ArrayList<JSONObject>();
		for (int i = 0; i < arr.size(); i++) {
			jsonValue.add(arr.getJSONObject(i));
		}
		Collections.sort(jsonValue, new Comparator<JSONObject>() {
			public int compare(JSONObject a, JSONObject b) {
				Integer valA = a.getIntValue(key);// String valA=a.getString(key);
				Integer valB = b.getIntValue(key);// String valB=b.getString(key);
				return valA.compareTo(valB);
			}
		});
		for (int i = 0; i < arr.size(); i++) {
			res.add(jsonValue.get(i));
		}
		return res;
	}
}
