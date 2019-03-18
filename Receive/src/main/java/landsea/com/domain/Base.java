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
 * @createDate 2019/01/08 13:37
 * @see landsea.com.domain
 */
@Data
public class Base {
    private String ID;
    private String NUM_CHN;
    private String NUM_EN;
    private String NAME_CHN;
    private String NAME_EN;
    private double STATUS;
    private double TYPH_YEAR;
    private Date CREATETIME;
    private String OPTMARK;
    private List<Typhoon> typhoons = new ArrayList();

}
