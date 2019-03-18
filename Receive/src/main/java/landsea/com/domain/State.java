package landsea.com.domain;

import lombok.Data;

import java.util.Date;

/**
 * <Description>
 * 状态类，用于交互，使用UDP发送
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/02/28 15:40

 */
@Data
public class State {
    private String KIND;
    private Date CRAWLTIME;
    private String  DATATIME;
    private int DATASTATE;
    private String REMARK;
    private String PROPERTIES;



}
