package landsea.com.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/01/08 13:45
 * @see landsea.com.domain
 */
@Data
public class Typhoon {
    private String ID;
    private String TYPHOONINFOID;
    private String FORECASTSTATION;
    private Date FORECASTTIME;
    private String TYPHOONTYPE;
    private double LATITUDE;
    private double LONGITUDE;
    private String GRADE;
    private double WINDSPEED;
    private double MOVESPEED;
    private String DIRECT;
    private double PRESSURE;
    private Date CREATETIME;
    private String OPTMARK;
    private Long AUTONO;
    private String POWER_HIGH;
    private String POWER_HIGH_RANGE;
    private String POWER_MIDDLE;
    private String POWER_MIDDLE_RANGE;
    private String POWER_LOW;
    private String POWER_LOW_RANGE;
    private String RELEASE_ID;
    private List<ForeCast> foreCasts = new ArrayList();
}
