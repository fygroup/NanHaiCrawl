package landsea.com.domain;

import lombok.Data;

import java.util.Date;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/01/08 13:44
 * @see landsea.com.domain
 */
@Data
public class ForeCast {
    private  String ID ;
    private  Date arrival_time ;
    private  double center_latitude ;
    private  double center_longtitude ;
    private  double max_speed ;
    private  double center_pressure ;
    private  String FORECASTID ;
    private  Date createtime ;
    private  int sort_id;
}
