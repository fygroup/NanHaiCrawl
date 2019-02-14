package com.ab.crawl.pipeline;

import com.ab.crawl.domain.Nvm;
import com.ab.crawl.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** 
* @ClassName: NmcPipeline
* @Description: 台风预报
* @author tongzq
* @date 2018年12月27日 上午9:06:47
*  
*/

public class NmcPipeline implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    private static Connection conn = null;
	private static PreparedStatement ps = null;
	
	String lasttfbh ;
	private int areatype;

	/**
	 * 将获取到的海风数据存入Nvm对象链表
	 * @param resultItems
	 * @param task
	 */
    @Override
    public  void process(ResultItems resultItems, Task task) {
    	//获得发布时间
		String time = resultItems.getAll().get("time").toString();
		//将数据存入链表
		List<String> details = new ArrayList<>();
		details=(ArrayList<String>)resultItems.get("item");
		System.out.println(details);
		int araetype=0;

		//存放某区域的海风预报
		List<Nvm> nvm = new ArrayList<>();
		for (int i = 0; i<details.size(); i=i+31){
			String area = details.get(i++);
			areatype++;
			for (int a = 0 ;a < 6; a++){
				Nvm wind = new Nvm();
				int periodtype=a;
				String id=clearChinese(time);
				// id的类型：11位  前8位（年月日时） 中间两位（地区类型）最为一位时段（0代表0-12）
				wind.setId(id+String.format("%02d", areatype)+periodtype);
				wind.setArea(area);
				wind.setPeriod(details.get(i));
				wind.setPhenomenon(details.get(i+1));
				wind.setDirection(details.get(i+2));
				wind.setLevel(details.get(i+3));
				wind.setVisibility(details.get(i+4));
				wind.setRemarks(time);
				System.out.println(wind);
				create(wind);
				i=i+5;
			}

		}

	}
	/**
	 *
	 * @param wind
	 * @return
	 */

	public static Integer create(Nvm wind) {
		conn = DBUtil.getConn();
	    int i = 0;
	    String sql = "insert into nvm (id,area,period,phenomenon,direction,level,visibility,remarks) values(?,?,?,?,?,?,?,?)";
		try {
	        ps = conn.prepareStatement(sql);
			ps.setString(1,wind.getId());
			ps.setString(2,wind.getArea());
			ps.setString(3,wind.getPeriod());
			ps.setString(4,wind.getPhenomenon());
			ps.setString(5,wind.getDirection());
			ps.setString(6,wind.getLevel());
			ps.setString(7,wind.getVisibility());
			ps.setString(8,wind.getRemarks());
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

	/**
	 * 去掉日期中的汉字
	 * @param buff
	 * @return
	 */
	private String clearChinese(String buff){

		String regEx="[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(buff);
		String result=m.replaceAll("").trim();
		return result.substring(2);

	}
}
