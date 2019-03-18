/**
  * Copyright 2019 bejson.com 
  */
package landsea.com.domain;
import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2019-01-14 16:53:2
 *

 */
@Data
public class MeteWeather {

    private String PublishTime;
    private String ForecastorID;
    private String Discribe;
    private List<String> SeaArea;
}