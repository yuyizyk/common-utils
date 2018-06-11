package cn.yuyizyk.tools.io;

import java.io.File;
import java.nio.charset.Charset;

public class FileUtils {

	public static String readString(File file, Charset srcCharset) {
		return null;
	}

	public static File writeString(String str, File file, Charset destCharset) {
		return null;
	}

	public static boolean isWindows() {
		return false;
	}

	/**
	 * 转换文件编码<br>
	 * 此方法用于转换文件编码，读取的文件实际编码必须与指定的srcCharset编码一致，否则导致乱码
	 * 
	 * @param file
	 *            文件
	 * @param srcCharset
	 *            原文件的编码，必须与文件内容的编码保持一致
	 * @param destCharset
	 *            转码后的编码
	 * @return 被转换编码的文件
	 * @since 3.1.0
	 */
	public static File convert(File file, Charset srcCharset, Charset destCharset) {
		final String str = readString(file, srcCharset);
		return writeString(str, file, destCharset);
	}
}
