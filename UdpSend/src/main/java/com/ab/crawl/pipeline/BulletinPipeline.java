package com.ab.crawl.pipeline;

import com.ab.crawl.domain.Nvm;
import com.ab.crawl.util.DBUtil;
import lombok.extern.slf4j.Slf4j;
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

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2018/12/28 13:50
 * @see com.ab.crawl.pipeline
 */
@Slf4j
public class BulletinPipeline implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static Connection conn = null;
    private static PreparedStatement ps = null;

    /**
     *
     * @param resultItems
     * @param task
     */
    @Override
    public void process(ResultItems resultItems, Task task) {
        String date = resultItems.getAll().get("date").toString();
        String title = resultItems.getAll().get("title").toString();
        List<String> content = new ArrayList<>();
        content=(ArrayList<String>)resultItems.get("content");
        List<String> paragraph =new ArrayList<>();
        String text="";
        for (String item :content){
            if (item.contains("二、")||item.contains("三、")||item.contains("四、")||item.contains("五、")){
                paragraph.add(text);
                text="";
            }
            if (item.contains("请参见中央气象台最新一期")){
                paragraph.add(text);
                break;
            }
            text+=item;

        }
        List<String> images = new ArrayList<>();
        images=(ArrayList<String>)resultItems.get("img");

        for (int pos=0;pos<paragraph.size();pos++){
            System.out.println(paragraph.get(pos));
        }
        for (int pos=0;pos<images.size();pos++){
            System.out.println(images.get(pos));
        }
        System.out.println(date+title+paragraph+images);

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

}
