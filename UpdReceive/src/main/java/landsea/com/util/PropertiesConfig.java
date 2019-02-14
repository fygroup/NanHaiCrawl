package landsea.com.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @ClassName: PropertiesConfig
 * @Description: 读取配置文件
 * @author gaow
 * @date 2018年11月15日 上午9:07:25
 * 
 */
public enum PropertiesConfig {

	INSTANCE;

	PropertiesConfig() {
		init();
	};

	private Properties pro = new Properties();

	private void init() {
		InputStream is = PropertiesConfig.class.getResourceAsStream("/application.properties");
		if (is != null) {
			try {
				pro.load(is);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getProPerties(String key) {
		return pro.getProperty(key);
	}
}
