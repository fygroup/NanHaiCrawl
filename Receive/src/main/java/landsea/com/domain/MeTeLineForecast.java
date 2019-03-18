/**
  * Copyright 2019 bejson.com 
  */
package landsea.com.domain;
import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2019-01-14 16:53:2
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class MeTeLineForecast {

    private String NameCHS;
    private String NameEN;
    private String ForecastType;
    private String EffectiveTime_Begin;
    private String EffectiveTime_End;
    private String TimeSpan;
    private List<MeteForecastContent> ForecastContent;


}