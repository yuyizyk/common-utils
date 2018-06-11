package cn.yuyizyk.tools.net;

import java.net.MalformedURLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.yuyizyk.tools.lang.Regs;
import cn.yuyizyk.tools.lang.Strs;

/**
 * url字符串 处理
 * 
 * @author yuyi
 *
 */
public class Urls {
	private final static Logger log = LoggerFactory.getLogger(Strs.class);
	/** 针对ClassPath路径的伪协议前缀（兼容Spring）: "classpath:" */
	public static final String CLASSPATH_URL_PREFIX = "classpath:";
	/** URL 前缀表示文件: "file:" */
	public static final String FILE_URL_PREFIX = "file:";
	/** URL 前缀表示jar: "jar:" */
	public static final String JAR_URL_PREFIX = "jar:";
	/** URL 前缀表示war: "war:" */
	public static final String WAR_URL_PREFIX = "war:";
	/** URL 协议表示文件: "file" */
	public static final String URL_PROTOCOL_FILE = "file";
	/** URL 协议表示Jar文件: "jar" */
	public static final String URL_PROTOCOL_JAR = "jar";
	/** URL 协议表示zip文件: "zip" */
	public static final String URL_PROTOCOL_ZIP = "zip";
	/** URL 协议表示WebSphere文件: "wsjar" */
	public static final String URL_PROTOCOL_WSJAR = "wsjar";
	/** URL 协议表示JBoss zip文件: "vfszip" */
	public static final String URL_PROTOCOL_VFSZIP = "vfszip";
	/** URL 协议表示JBoss文件: "vfsfile" */
	public static final String URL_PROTOCOL_VFSFILE = "vfsfile";
	/** URL 协议表示JBoss VFS资源: "vfs" */
	public static final String URL_PROTOCOL_VFS = "vfs";
	/** Jar路径以及内部文件路径的分界符: "!/" */
	public static final String JAR_URL_SEPARATOR = "!/";
	/** WAR路径及内部文件路径分界符 */
	public static final String WAR_URL_SEPARATOR = "*/";
	
	
	
	
	public static boolean is(String url) {
		return Regs.matches(url, Regs.V_URL);
	}

	/**
	 * 获得url的domain  
	 */
	public static final String getDomain(String url) {
		List<String> li = Regs.findSubStr(url, Regs.V_DOMAIN);
		return li.isEmpty() ? null : li.get(0);
	}

	/**
	 * 获得url的getHost  
	 */
	public static final String getHost(String urlStr) {
		try {
			java.net.URL url = new java.net.URL(urlStr);
			return url.getHost();
		} catch (MalformedURLException e) {
			log.error("", e);
		}
		return null;
	}

	/**
	 * 获得url的Protocol  
	 */
	public static final String getProtocol(String urlStr) {
		try {
			java.net.URL url = new java.net.URL(urlStr);
			return url.getProtocol();
		} catch (MalformedURLException e) {
			log.error("", e);
		}
		return null;
	}

	/**
	 * 获得url的getUriPath  
	 */
	public static final String getUriPath(String urlStr) {
		try {
			java.net.URL url = new java.net.URL(urlStr);
			return url.getPath();
		} catch (MalformedURLException e) {
			log.error("", e);
		}
		return null;
	}

	/**
	 * 获得url的getUrlQueryStr  
	 */
	public static final String getUrlQueryStr(String urlStr) {
		try {
			java.net.URL url = new java.net.URL(urlStr);
			return url.getQuery();
		} catch (MalformedURLException e) {
			log.error("", e);
		}
		return null;
	}

	public static final int getPort(String urlStr) {
		try {
			java.net.URL url = new java.net.URL(urlStr);
			return url.getPort();
		} catch (MalformedURLException e) {
			log.error("", e);
		}
		return 0;
	}

}
