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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.selector.Json;

/**
 * @ClassName: TyphoonListPipeline
 * @Description: 处理台风列表数据
 * @author gaow
 * @date 2018年11月15日 上午9:06:47
 * 
 */
public class TyphoonListPipeline implements Pipeline {

	private static Connection conn = null;
	private static PreparedStatement ps = null;

	@Override
	public void process(ResultItems resultItems, Task task) {
		for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
			if (entry.getKey().equals("content")) {
				Json json = (Json) entry.getValue();
				if (json != null && !json.toString().startsWith("<html>")) {
					JSONArray typhoons = sort(JSONArray.parseArray(json.toString()), "tfbh");
					for (Object obj : typhoons) {
						JSONObject o = (JSONObject) obj;
						if(o.getString("ename").equals("TD")) {
							continue;
						}
						create(o.getString("tfbh"), o.getString("ename"), o.getString("name"),
								o.getString("begin_time"), o.getString("end_time"), o.getString("is_current"));
					}

					String year = ((JSONObject) typhoons.get(0)).getString("tfbh").substring(0, 4);
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
	 * @Title: create @Description: 保存台风列表 @param @param tfbh @param @param
	 *         ename @param @param name @param @param begin_time @param @param
	 *         end_time @param @param is_current @param @return 设定文件 @return Integer
	 *         返回类型 @throws
	 */
	public static Integer create(String tfbh, String ename, String name, String begin_time, String end_time,
			String is_current) {
		conn = DBUtil.getConn();
		int i = 0;
		String sql = "insert into typhoonlist (tfbh,ename,name,begin_time,end_time,is_current) values(?,?,?,?,?,?)";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, tfbh);
			ps.setString(2, ename);
			ps.setString(3, name);
			ps.setDate(4,
					new Date(DateUtils.str2Date(begin_time, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).getTime()));
			ps.setDate(5,
					new Date(DateUtils.str2Date(end_time, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).getTime()));
			ps.setString(6, is_current);
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
