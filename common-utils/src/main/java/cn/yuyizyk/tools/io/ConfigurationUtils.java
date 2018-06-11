package cn.yuyizyk.tools.io;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * 取得配置文件
 * 
 *
 */
public class ConfigurationUtils {

	public static Configuration read(String name) {
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(
				PropertiesConfiguration.class).configure(params.properties().setFileName(name).setEncoding("UTF-8"));
		Configuration config;
		try {
			config = builder.getConfiguration();
			return config;
		} catch (ConfigurationException e) {
			e.printStackTrace();
			return null;
		}

	}
}
