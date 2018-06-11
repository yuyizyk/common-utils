package cn.yuyizyk.tools.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.yuyizyk.tools.exception.Exceptions;
import cn.yuyizyk.tools.io.FileUtils;

/**
 * 常用字符串处理
 * <p>
 * 参考：
 * <ul>
 * <li>https://github.com/looly/hutool/tree/v4-master/hutool-core/src/main/java/cn/hutool/core/util</li>
 * <li>https://github.com/looly/hutool/tree/v4-master/hutool-core/src/main/java/cn/hutool/core/util</li>
 * </ul>
 * </p>
 * 
 * @author yuyi
 *
 */
public class Strs extends StringUtils {
	private final static Logger log = LoggerFactory.getLogger(Strs.class);

	/** ISO-8859-1 */
	public static final String ISO_8859_1 = "ISO-8859-1";
	/** UTF-8 */
	public static final String UTF_8 = "UTF-8";
	/** GBK */
	public static final String GBK = "GBK";
	/** ISO-8859-1 */
	public static final Charset CHARSET_ISO_8859_1 = StandardCharsets.ISO_8859_1;
	/** UTF-8 */
	public static final Charset CHARSET_UTF_8 = StandardCharsets.UTF_8;
	/** GBK */
	public static final Charset CHARSET_GBK = Charset.forName(GBK);
	public static final char C_SPACE = ' ';
	public static final char C_TAB = '	';
	public static final char C_DOT = '.';
	public static final char C_SLASH = '/';
	public static final char C_BACKSLASH = '\\';
	public static final char C_CR = '\r';
	public static final char C_LF = '\n';
	public static final char C_UNDERLINE = '_';
	public static final char C_COMMA = ',';
	public static final char C_DELIM_START = '{';
	public static final char C_DELIM_END = '}';
	public static final char C_BRACKET_START = '[';
	public static final char C_BRACKET_END = ']';
	public static final char C_COLON = ':';

	public static final String SPACE = " ";
	public static final String TAB = "	";
	public static final String DOUBLE_DOT = "..";
	public static final String SLASH = "/";
	public static final String BACKSLASH = "\\";
	public static final String EMPTY = "";
	public static final String CR = "\r";
	public static final String LF = "\n";
	public static final String CRLF = "\r\n";
	public static final String UNDERLINE = "_";
	public static final String COMMA = ",";
	public static final String DELIM_START = "{";
	public static final String DELIM_END = "}";
	public static final String BRACKET_START = "[";
	public static final String BRACKET_END = "]";
	public static final String COLON = ":";

	public static final String HTML_NBSP = "&nbsp;";
	public static final String HTML_AMP = "&amp;";
	public static final String HTML_QUOTE = "&quot;";
	public static final String HTML_APOS = "&apos;";
	public static final String HTML_LT = "&lt;";
	public static final String HTML_GT = "&gt;";

	public static final String EMPTY_JSON = "{}";
	/** 包名分界符: '.' */
	public static final char DOT = '.';

	private static Pattern linePattern = Pattern.compile("_(\\w)");
	private static Pattern humpPattern = Pattern.compile("[A-Z]");

	/**
	 * 格式化文本, {} 表示占位符<br>
	 * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
	 * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
	 * 例：<br>
	 * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
	 * 转义{}： format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
	 * 转义\： format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
	 * 
	 * @param template
	 *            文本模板，被替换的部分用 {} 表示
	 * @param params
	 *            参数值
	 * @return 格式化后的文本
	 */
	public static String format(CharSequence template, Object... params) {
		if (isEmpty(template)) {
			return null;
		}
		if (Objs.isEmpty(params) || isBlank(template)) {
			return template.toString();
		}
		return format(template.toString(), params);
	}

	/**
	 * 格式化字符串<br>
	 * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
	 * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
	 * 例：<br>
	 * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
	 * 转义{}： format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
	 * 转义\： format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
	 * 
	 * @param strPattern
	 *            字符串模板
	 * @param argArray
	 *            参数列表
	 * @return 结果
	 */
	public static String format(final String strPattern, final Object... argArray) {
		if (isBlank(strPattern) || Objs.isEmpty(argArray)) {
			return strPattern;
		}
		final int strPatternLength = strPattern.length();

		// 初始化定义好的长度以获得更好的性能
		StringBuilder sbuf = new StringBuilder(strPatternLength + 50);

		int handledPosition = 0;// 记录已经处理到的位置
		int delimIndex;// 占位符所在位置
		for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
			delimIndex = strPattern.indexOf(EMPTY_JSON, handledPosition);
			if (delimIndex == -1) {// 剩余部分无占位符
				if (handledPosition == 0) { // 不带占位符的模板直接返回
					return strPattern;
				} else { // 字符串模板剩余部分不再包含占位符，加入剩余部分后返回结果
					sbuf.append(strPattern, handledPosition, strPatternLength);
					return sbuf.toString();
				}
			} else {
				if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == C_BACKSLASH) {// 转义符
					if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == C_BACKSLASH) {// 双转义符
						// 转义符之前还有一个转义符，占位符依旧有效
						sbuf.append(strPattern, handledPosition, delimIndex - 1);
						sbuf.append(toString(argArray[argIndex]));
						handledPosition = delimIndex + 2;
					} else {
						// 占位符被转义
						argIndex--;
						sbuf.append(strPattern, handledPosition, delimIndex - 1);
						sbuf.append(C_DELIM_START);
						handledPosition = delimIndex + 1;
					}
				} else {// 正常占位符
					sbuf.append(strPattern, handledPosition, delimIndex);
					sbuf.append(toString(argArray[argIndex]));
					handledPosition = delimIndex + 2;
				}
			}
		}
		// append the characters following the last {} pair.
		// 加入最后一个占位符后所有的字符
		sbuf.append(strPattern, handledPosition, strPattern.length());

		return sbuf.toString();
	}

	/**
	 * 有序的格式化文本，使用{number}做为占位符<br>
	 * 例：<br>
	 * 通常使用：format("this is {0} for {1}", "a", "b") =》 this is a for b<br>
	 * 
	 * @param pattern
	 *            文本格式
	 * @param arguments
	 *            参数
	 * @return 格式化后的文本
	 */
	public static String indexedFormat(CharSequence pattern, Object... arguments) {
		return MessageFormat.format(pattern.toString(), arguments);
	}

	/**
	 * 格式化文本，使用 {varName} 占位<br>
	 * map = {a: "aValue", b: "bValue"} format("{a} and {b}", map) ---=》 aValue and
	 * bValue
	 * 
	 * @param template
	 *            文本模板，被替换的部分用 {key} 表示
	 * @param map
	 *            参数值对
	 * @return 格式化后的文本
	 */
	public static String format(CharSequence template, Map<?, ?> map) {
		if (isEmpty(template)) {
			return null;
		}
		if (Objs.isEmpty(map)) {
			return template.toString();
		}

		String template2 = template.toString();
		for (Entry<?, ?> entry : map.entrySet()) {
			template2 = template2.replace("{" + entry.getKey() + "}", toString(entry.getValue()));
		}
		return template2;
	}

	/**
	 * 将对象转为字符串<br>
	 * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
	 * 
	 * @param obj
	 *            对象
	 * @return 字符串
	 */
	public static String toString(Object obj) {
		return toString(obj, CHARSET_UTF_8);
	}

	/**
	 * 将对象转为字符串<br>
	 * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
	 * 
	 * @param obj
	 *            对象
	 * @param charsetName
	 *            字符集
	 * @return 字符串
	 */
	public static String toString(Object obj, String charsetName) {
		return toString(obj, Charset.forName(charsetName));
	}

	/**
	 * 将对象转为字符串<br>
	 * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
	 * 
	 * @param obj
	 *            对象
	 * @param charset
	 *            字符集
	 * @return 字符串
	 */
	public static String toString(Object obj, Charset charset) {
		if (Objs.isEmpty(obj)) {
			return null;
		}

		if (obj instanceof String) {
			return (String) obj;
		} else if (obj instanceof byte[]) {
			return toString((byte[]) obj, charset);
		} else if (obj instanceof Byte[]) {
			return toString((Byte[]) obj, charset);
		} else if (obj instanceof ByteBuffer) {
			return toString((ByteBuffer) obj, charset);
		} else if (Arrs.isArray(obj)) {
			return Arrs.toString(obj);
		}

		return obj.toString();
	}

	/**
	 * 将byte数组转为字符串
	 * 
	 * @param bytes
	 *            byte数组
	 * @param charset
	 *            字符集
	 * @return 字符串
	 */
	public static String toString(byte[] bytes, String charset) {
		return toString(bytes, isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
	}

	/**
	 * 解码字节码
	 * 
	 * @param data
	 *            字符串
	 * @param charset
	 *            字符集，如果此字段为空，则解码的结果取决于平台
	 * @return 解码后的字符串
	 */
	public static String toString(byte[] data, Charset charset) {
		if (Objs.isEmpty(data)) {
			return null;
		}

		if (Objs.isEmpty(charset)) {
			return new String(data);
		}
		return new String(data, charset);
	}

	/**
	 * 将Byte数组转为字符串
	 * 
	 * @param bytes
	 *            byte数组
	 * @param charset
	 *            字符集
	 * @return 字符串
	 */
	public static String toString(Byte[] bytes, String charset) {
		return toString(bytes, isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
	}

	/**
	 * 解码字节码
	 * 
	 * @param data
	 *            字符串
	 * @param charset
	 *            字符集，如果此字段为空，则解码的结果取决于平台
	 * @return 解码后的字符串
	 */
	public static String toString(Byte[] data, Charset charset) {
		if (Objs.isEmpty(data)) {
			return null;
		}

		byte[] bytes = new byte[data.length];
		Byte dataByte;
		for (int i = 0; i < data.length; i++) {
			dataByte = data[i];
			bytes[i] = (Objs.isEmpty(dataByte)) ? -1 : dataByte.byteValue();
		}

		return toString(bytes, charset);
	}

	/**
	 * 将编码的byteBuffer数据转换为字符串
	 * 
	 * @param data
	 *            数据
	 * @param charset
	 *            字符集，如果为空使用当前系统字符集
	 * @return 字符串
	 */
	public static String toString(ByteBuffer data, String charset) {
		if (Objs.isEmpty(data)) {
			return null;
		}

		return toString(data, Charset.forName(charset));
	}

	/**
	 * 将编码的byteBuffer数据转换为字符串
	 * 
	 * @param data
	 *            数据
	 * @param charset
	 *            字符集，如果为空使用当前系统字符集
	 * @return 字符串
	 */
	public static String toString(ByteBuffer data, Charset charset) {
		if (Objs.isEmpty(charset)) {
			charset = Charset.defaultCharset();
		}
		return charset.decode(data).toString();
	}

	/**
	 * {@link CharSequence} 转为字符串，null安全
	 * 
	 * @param cs
	 *            {@link CharSequence}
	 * @return 字符串
	 */
	public static String toString(CharSequence cs) {
		return isEmpty(cs) ? null : cs.toString();
	}

	/**
	 * 字符串转换为byteBuffer
	 * 
	 * @param str
	 *            字符串
	 * @param charset
	 *            编码
	 * @return byteBuffer
	 */
	public static ByteBuffer byteBuffer(CharSequence str, String charset) {
		return ByteBuffer.wrap(bytes(str, charset));
	}

	/**
	 * 编码字符串<br>
	 * 使用系统默认编码
	 * 
	 * @param str
	 *            字符串
	 * @return 编码后的字节码
	 */
	public static byte[] bytes(CharSequence str) {
		return bytes(str, Charset.defaultCharset());
	}

	/**
	 * 编码字符串
	 * 
	 * @param str
	 *            字符串
	 * @param charset
	 *            字符集，如果此字段为空，则解码的结果取决于平台
	 * @return 编码后的字节码
	 */
	public static byte[] bytes(CharSequence str, String charset) {
		return bytes(str, isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
	}

	/**
	 * 编码字符串
	 * 
	 * @param str
	 *            字符串
	 * @param charset
	 *            字符集，如果此字段为空，则解码的结果取决于平台
	 * @return 编码后的字节码
	 */
	public static byte[] bytes(CharSequence str, Charset charset) {
		if (isEmpty(str)) {
			return null;
		}

		if (Objs.isEmpty(charset)) {
			return str.toString().getBytes();
		}
		return str.toString().getBytes(charset);
	}

	/**
	 * 下划线转驼峰
	 * 
	 * @param str
	 * @return
	 */
	public static String lineToHump(String str) {
		if (isBlank(str)) {
			return str;
		}
		str = str.toLowerCase();
		Matcher matcher = linePattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);

		str = sb.toString();
		str = str.substring(0, 1).toUpperCase() + str.substring(1);

		return str;
	}

	/**
	 * 驼峰转下划线,效率比上面高
	 * 
	 * @param str
	 * @return
	 */
	public static String humpToLine(String str) {
		Matcher matcher = humpPattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 转换为Charset对象
	 * 
	 * @param charsetName
	 *            字符集，为空则返回默认字符集
	 * @return Charset
	 * @throws UnsupportedCharsetException
	 *             编码不支持
	 */
	public static Charset charset(String charsetName) throws UnsupportedCharsetException {
		return isBlank(charsetName) ? Charset.defaultCharset() : Charset.forName(charsetName);
	}

	/**
	 * 获得set或get方法对应的标准属性名<br>
	 * 例如：setName 返回 name
	 * 
	 * @param getOrSetMethodName
	 *            Get或Set方法名
	 * @return 如果是set或get方法名，返回field， 否则null
	 */
	public static String getGeneralField(CharSequence getOrSetMethodName) {
		final String getOrSetMethodNameStr = getOrSetMethodName.toString();
		if (getOrSetMethodNameStr.startsWith("get") || getOrSetMethodNameStr.startsWith("set")) {
			return substring(getOrSetMethodName.toString(), 3);
		}
		return null;
	}

	/**
	 * 转换字符串的字符集编码
	 * 
	 * @param source
	 *            字符串
	 * @param srcCharset
	 *            源字符集，默认ISO-8859-1
	 * @param destCharset
	 *            目标字符集，默认UTF-8
	 * @return 转换后的字符集
	 */
	public static String convert(String source, String srcCharset, String destCharset) {
		return convert(source, Charset.forName(srcCharset), Charset.forName(destCharset));
	}

	/**
	 * 转换字符串的字符集编码<br>
	 * 当以错误的编码读取为字符串时，打印字符串将出现乱码。<br>
	 * 此方法用于纠正因读取使用编码错误导致的乱码问题。<br>
	 * 例如，在Servlet请求中客户端用GBK编码了请求参数，我们使用UTF-8读取到的是乱码，此时，使用此方法即可还原原编码的内容
	 * 
	 * <pre>
	 * 客户端 -》 GBK编码 -》 Servlet容器 -》 UTF-8解码 -》 乱码
	 * 乱码 -》 UTF-8编码 -》 GBK解码 -》 正确内容
	 * </pre>
	 * 
	 * @param source
	 *            字符串
	 * @param srcCharset
	 *            源字符集，默认ISO-8859-1
	 * @param destCharset
	 *            目标字符集，默认UTF-8
	 * @return 转换后的字符集
	 */
	public static String convert(String source, Charset srcCharset, Charset destCharset) {
		if (Objs.isEmpty(srcCharset)) {
			srcCharset = StandardCharsets.ISO_8859_1;
		}

		if (Objs.isEmpty(destCharset)) {
			destCharset = StandardCharsets.UTF_8;
		}

		if (isBlank(source) || srcCharset.equals(destCharset)) {
			return source;
		}
		return new String(source.getBytes(srcCharset), destCharset);
	}

	/**
	 * 系统字符集编码，如果是Windows，则默认为GBK编码，否则取 {@link CharsetUtil#defaultCharsetName()}
	 * 
	 * @see CharsetUtil#defaultCharsetName()
	 * @return 系统字符集编码
	 * @since 3.1.2
	 */
	public static String systemCharsetName() {
		return systemCharset().name();
	}

	/**
	 * 系统字符集编码，如果是Windows，则默认为GBK编码，否则取 {@link CharsetUtil#defaultCharsetName()}
	 * 
	 * @see CharsetUtil#defaultCharsetName()
	 * @return 系统字符集编码
	 * @since 3.1.2
	 */
	public static Charset systemCharset() {
		return FileUtils.isWindows() ? CHARSET_GBK : defaultCharset();
	}

	/**
	 * 系统默认字符集编码
	 * 
	 * @return 系统字符集编码
	 */
	public static String defaultCharsetName() {
		return defaultCharset().name();
	}

	/**
	 * 系统默认字符集编码
	 * 
	 * @return 系统字符集编码
	 */
	public static Charset defaultCharset() {
		return Charset.defaultCharset();
	}

	/**
	 * 随机字符串生成
	 * 
	 * @return
	 */
	public static String getRandmStr(int length) {
		char[] tempCs = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o',
				'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm', 'Q', 'W', 'E', 'R',
				'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N',
				'M' };
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int temp = random.nextInt();
			if (0 != temp) {
				sb.append(tempCs[Math.abs(temp) % tempCs.length]);
			} else {
				sb.append(tempCs[Integer.MIN_VALUE % tempCs.length]);
			}
		}
		return sb.toString();
	}

	/**
	 * 字符串转为16进制
	 * 
	 * @param s
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static String convertStringTo16(String s) {
		byte[] b = s.getBytes();
		String str = "";
		try {
			for (int i = 0; i < b.length; i++) {
				Integer I = new Integer(b[i]);
				String strTmp = I.toHexString(b[i]);
				if (strTmp.length() > 2)
					strTmp = strTmp.substring(strTmp.length() - 2);
				str = str + strTmp;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str.toUpperCase();
	}

	/**
	 * 16进制转为字符串
	 * 
	 * @param s
	 * @return
	 */
	public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "utf-8");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	/**
	 * 转化为字符串
	 * 
	 * @param num
	 * @param n
	 *            整数位数
	 * @return
	 */
	static public String by(int num, int n) {
		return String.format("%0" + n + "d", num);
	}

	/**
	 * Clob To String
	 * 
	 * @param clob
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	static public String by(java.sql.Clob clob) {
		Reader isClob;
		String contentstr = "";
		StringBuffer sbResult = new StringBuffer();
		try {
			isClob = clob.getCharacterStream();
			BufferedReader bfClob = new BufferedReader(isClob);
			String strClob = bfClob.readLine();
			while (isNotEmpty(strClob)) {
				sbResult.append(strClob);
				strClob = bfClob.readLine();
			}
			contentstr = sbResult.toString();
		} catch (Exception e) {
			log.error("", e);
			throw Exceptions.box(e);
		}
		return contentstr;
	}

	/**
	 * 根据字符的Ascii来获得具体的长度
	 * 
	 * @param str
	 * @return
	 */
	public static int getAsciiLength(String str) {
		int length = 0;
		for (int i = 0; i < str.length(); i++) {
			int ascii = Character.codePointAt(str, i);
			if (ascii >= 0 && ascii <= 255)
				length++;
			else
				length += 2;
		}
		return length;
	}

	/**
	 * 获得指定Ascii长度的省略字符串 <br/>
	 */
	public static String subOmitStr(String text, int length) {
		return subOmitStr(text, length, "...");

	}

	/**
	 * 过滤Pattern匹配的字符串，并保留group数据。
	 * 
	 * @param input
	 * @param p
	 * @return
	 */
	public static String filter(String input, Pattern p) {
		Matcher m = p.matcher(input);
		int start = 0, end;
		StringBuilder sb = new StringBuilder();
		while (m.find()) {
			end = m.start();
			sb.append(input.subSequence(start, end));
			for (int i = 1, len = m.groupCount(); i <= len; i++) {
				sb.append(input.subSequence(m.start(i), m.end(i)));
			}
			start = m.end();
		}
		end = input.length();
		sb.append(input.subSequence(start, end));
		return sb.toString();
	}

	/**
	 * 将字符串按行风格，支持windows(\r\n)、linux(\n)和(\r)格式换行。
	 * 
	 * @param s
	 * @return
	 */
	public static String[] splitLines(String s) {
		return split(s, "\r\n");
	}

	/**
	 * 将换行符替换成\n
	 * 
	 * @return
	 */
	public static String replaceNewline(String s) {
		s = replace(s, "\r\n", "\n");
		s = replaceChars(s, '\r', '\n');
		return s;
	}

	public static String urlEncode(String s) {
		if (isBlank(s)) {
			return s;
		}
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// never
			throw new RuntimeException(e);
		}

	}

	public static String urlDecode(String s) {
		if (isBlank(s)) {
			return s;
		}
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// never
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获得指定Ascii长度的省略字符串 <br/>
	 * 字符串截断。编码大于127的字符作为占两个位置，否则占一个位置。
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static String subOmitStr(String text, int length, String append) {
		if (isBlank(text) || text.length() < length) {
			return text;
		}
		int num = 0, i = 0, len = text.length();
		StringBuilder sb = new StringBuilder();
		for (; i < len; i++) {
			char c = text.charAt(i);
			if (c > 127) {
				num += 2;
			} else {
				num++;
			}
			if (num <= length * 2) {
				sb.append(c);
			}
			if (num >= length * 2) {
				break;
			}
		}
		if (i + 1 < len && isNotBlank(append)) {
			if (text.charAt(i) > 127) {
				sb.setLength(sb.length() - 1);
			} else {
				sb.setLength(sb.length() - 2);
			}
			sb.append(append);
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param cs1
	 * @param cs2
	 * @param ignoreCase
	 * @return
	 */
	public static boolean equals(String cs1, String cs2, boolean ignoreCase) {
		if (null == cs1) {
			// 只有两个都为null才判断相等
			return cs2 == null;
		}
		if (null == cs2) {
			// 字符串2空，字符串1非空，直接false
			return false;
		}

		if (ignoreCase) {
			return cs1.toString().equalsIgnoreCase(cs2.toString());
		} else {
			return cs1.equals(cs2);
		}
	}

}
