/**
  * Copyright 2019 bejson.com 
  */
package landsea.com.domain;
import lombok.Data;

import java.util.Date;

/**
 * Auto-generated: 2019-01-14 16:53:2
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class MeteForecastContent {

    private String time;
    private String WindDirection;
    private Date WindSpeedStr;
    private String WaveHeightStr;
    private String WaveDirectionStr;
    private String MaxWaveHeightStr;


}