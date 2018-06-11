package cn.yuyizyk.tools.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.yuyizyk.tools.clz.ClassLoaderUtils;

/**
 * 
 * @author yuyi
 *
 */
public class PropertiesUtils {
	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);
	private static final String[] DEFAULT_FILE_SEARCH_LOCATIONS = new String[] { "./config/", "./" };

	public static Properties readConfigFile(String configPath, Properties defaults) {
		Properties props = new Properties();
		if (defaults != null) {
			props.putAll(defaults);
		}

		InputStream in = loadConfigFileFromDefaultSearchLocations(configPath);

		try {
			if (in != null) {
				props.load(in);
			}
		} catch (IOException ex) {
			logger.warn("Reading config failed: {}", ex.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					logger.warn("Close config failed: {}", ex.getMessage());
				}
			}
		}

		if (logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			for (String propertyName : props.stringPropertyNames()) {
				sb.append(propertyName).append('=').append(props.getProperty(propertyName)).append('\n');

			}
			if (sb.length() > 0) {
				logger.debug("Reading properties: \n" + sb.toString());
			} else {
				logger.warn("No available properties");
			}
		}
		return props;
	}

	private static InputStream loadConfigFileFromDefaultSearchLocations(String configPath) {
		try {
			// load from default search locations
			for (String searchLocation : DEFAULT_FILE_SEARCH_LOCATIONS) {
				File candidate = Paths.get(searchLocation, configPath).toFile();
				if (candidate.exists() && candidate.isFile() && candidate.canRead()) {
					logger.debug("Reading config from resource {}", candidate.getAbsolutePath());
					return new FileInputStream(candidate);
				}
			}

			// load from classpath
			URL url = ClassLoaderUtils.getClassLoader().getResource(configPath);

			if (url != null) {
				InputStream in = getResourceAsStream(url);

				if (in != null) {
					logger.debug("Reading config from resource {}", url.getPath());
					return in;
				}
			}

			// load outside resource under current user path
			File candidate = new File(System.getProperty("user.dir"), configPath);
			if (candidate.exists() && candidate.isFile() && candidate.canRead()) {
				logger.debug("Reading config from resource {}", candidate.getAbsolutePath());
				return new FileInputStream(candidate);
			}
		} catch (FileNotFoundException e) {
			// ignore
		}
		return null;
	}

	private static InputStream getResourceAsStream(URL url) {
		try {
			return url != null ? url.openStream() : null;
		} catch (IOException e) {
			return null;
		}
	}

	public static Properties read(String path) {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		Properties p = new Properties();
		try {
			p.load(new BufferedReader(new InputStreamReader(inputStream)));
		} catch (IOException e) {
			logger.error("异常:", e);
		}
		return p;
	}

	/**
	 * Transform the properties to string format
	 * 
	 * @param properties
	 *            the properties object
	 * @return the string containing the properties
	 * @throws IOException
	 */
	public static String toString(Properties properties) throws IOException {
		StringWriter writer = new StringWriter();
		properties.store(writer, null);
		StringBuffer stringBuffer = writer.getBuffer();
		filterPropertiesComment(stringBuffer);
		return stringBuffer.toString();
	}

	/**
	 * filter out the first comment line
	 * 
	 * @param stringBuffer
	 *            the string buffer
	 * @return true if filtered successfully, false otherwise
	 */
	static boolean filterPropertiesComment(StringBuffer stringBuffer) {
		// check whether has comment in the first line
		if (stringBuffer.charAt(0) != '#') {
			return false;
		}
		int commentLineIndex = stringBuffer.indexOf("\n");
		if (commentLineIndex == -1) {
			return false;
		}
		stringBuffer.delete(0, commentLineIndex + 1);
		return true;
	}
}
