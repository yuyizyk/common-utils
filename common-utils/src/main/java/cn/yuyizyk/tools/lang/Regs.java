package cn.yuyizyk.tools.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 常用正则表达式及方法
 * 
 * @author yuyi
 *
 */
public class Regs {
	public final static Logger log = LoggerFactory.getLogger(Regs.class);

	/**
	 * 验证
	 * 
	 * @param val
	 * @param matcher
	 * @return
	 */
	public static Boolean isMatcher(String val, String matcher) {
		Pattern p = Pattern.compile(matcher);
		Matcher m = p.matcher(val);
		return m.matches();
	}

	public static Boolean isParaMatch(String val, String mateher) {
		try {
			if (Strs.isBlank(val)) {
				return false;
			}
			List<String> paraNames = getParams(mateher);
			mateher = parseMatchContext(mateher, paraNames);
			return isMatcher(val, mateher);
		} catch (Exception e) {

			return false;
		}
	}

	public static String parseMatchContext(String matchContext, List<String> paraNames) {
		String exportPat = "(.*)";
		for (String para : paraNames) {
			matchContext = matchContext.replace("${" + para + "}", exportPat);
		}
		return matchContext;
	}

	public static List<String> matchResults(String context, String matchContext) {
		String exportPat = "(.*)";
		String[] pattenTrunk = matchContext.split("\\(\\.\\*\\)");
		List<String> results = new ArrayList<String>();
		for (int i = 0; i < pattenTrunk.length; i++) {
			String patten = pattenTrunk[i] + exportPat;
			if (i < pattenTrunk.length - 1) {
				patten = patten + pattenTrunk[i + 1];
			}
			String value = matchExportFirst(context, patten);
			results.add(value);
		}
		return results;
	}

	public static Map<String, String> matchParamMap(String context, String matchContext) {
		List<String> paraNames = getParams(matchContext);
		matchContext = parseMatchContext(matchContext, paraNames);
		List<String> results = matchResults(context, matchContext);
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < paraNames.size(); i++) {
			try {
				if (map.containsKey(paraNames.get(i))) {
					map.put(paraNames.get(i), map.get(paraNames.get(i)) + "," + results.get(i));
					continue;
				}
				map.put(paraNames.get(i), results.get(i));
			} catch (Exception e) {
			}
		}
		return map;
	}

	/**
	 * ${asd}
	 * 
	 * @param context
	 * @return
	 */
	public static List<String> getParams(String context) {
		String patten = "\\$\\{([A-Za-z0-9_]+)\\}";
		return matchExport(context, patten);
	}

	public static String matchExportFirst(String context, String patten) {
		List<String> results = matchExport(context, patten);
		if (Objects.isNull(results)) {
			return null;
		}
		return results.get(0);
	}

	public static List<String> matchExport(String context, String patten) {
		try {
			Integer index = 0;
			Pattern pattern = Pattern.compile(patten, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(context);
			List<String> results = new ArrayList<String>();
			while (matcher.find(index)) {
				String tmp = matcher.group(1);
				index = matcher.end();
				if (Strs.isBlank(tmp)) {
					continue;
				}
				results.add(tmp);
			}
			return results;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 判读
	 * 
	 * @param str
	 * @param regex
	 * @return
	 */
	public static final boolean matches(String str, String regex) {
		return !Strs.isBlank(str) && str.matches(regex);
	}

	/**
	 * 获取查询的字符串  <br/>
	 * 将匹配的字符串取出    
	 */
	public static List<String> findSubStr(String str, String regx) {
		Pattern pattern = Pattern.compile(regx);
		Matcher matcher = pattern.matcher(str);
		List<String> list = new ArrayList<String>();
		while (matcher.find()) {
			list.add(str.substring(matcher.regionStart(), matcher.end()));
		}
		return list;
	}

	/**
	 * 验证是不是整数
	 * 
	 * @param value
	 *            要验证的字符串 要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Integer(String value) {
		return match(V_INTEGER, value);
	}

	/**
	 * 验证是不是正整数
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Z_index(String value) {
		return match(V_Z_INDEX, value);
	}

	/**
	 * 验证是不是负整数
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Negative_integer(String value) {
		return match(V_NEGATIVE_INTEGER, value);
	}

	/**
	 * 验证是不是数字
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Number(String value) {
		return match(V_NUMBER, value);
	}

	/**
	 * 验证是不是正数
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean PositiveNumber(String value) {
		return match(V_POSITIVE_NUMBER, value);
	}

	/**
	 * 验证是不是负数
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean NegatineNumber(String value) {
		return match(V_NEGATINE_NUMBER, value);
	}

	/**
	 * 验证一个月的31天
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Is31Days(String value) {
		return match(V_31DAYS, value);
	}

	/**
	 * 验证是不是ASCII
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean ASCII(String value) {
		return match(V_ASCII, value);
	}

	/**
	 * 验证是不是中文
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Chinese(String value) {
		return match(V_CHINESE, value);
	}

	/**
	 * 验证是不是颜色
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Color(String value) {
		return match(V_COLOR, value);
	}

	// /**
	// * 验证是不是日期
	// *
	// * @param value
	// * 要验证的字符串
	// * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	// */
	// public static boolean Date(String value) {
	// return match(V_DATE, value);
	// }

	/**
	 * 验证是不是邮箱地址
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Email(String value) {
		if (Strs.isBlank(value))
			return false;
		return match(V_EMAIL, value);
	}

	/**
	 * 验证是不是浮点数
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Float(String value) {
		return match(V_FLOAT, value);
	}

	/**
	 * 验证是不是正确的身份证号码
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean IDcard(String value) {
		return match(V_IDCARD, value);
	}

	/**
	 * 验证是不是正确的IP地址
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean IP4(String value) {
		return match(V_IP4, value);
	}

	/**
	 * 验证是不是字母
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Letter(String value) {
		return match(V_LETTER, value);
	}

	/**
	 * 验证是不是小写字母
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Letter_i(String value) {
		return match(V_LETTER_I, value);
	}

	/**
	 * 验证是不是大写字母
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Letter_u(String value) {
		return match(V_LETTER_U, value);
	}

	/**
	 * 验证是不是手机号码
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Mobile(String value) {
		return match(V_MOBILE, value);
	}

	/**
	 * 验证是不是负浮点数
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Negative_float(String value) {
		return match(V_NEGATIVE_FLOAT, value);
	}

	/**
	 * 验证非空
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Notempty(String value) {
		return match(V_NOTEMPTY, value);
	}

	/**
	 * 验证密码的长度(6~18位)
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Number_length(String value) {
		return match(V_PASSWORD_LENGTH, value);
	}

	/**
	 * 验证密码(数字和英文同时存在)
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Password_reg(String value) {
		return match(V_PASSWORD_REG, value);
	}

	/**
	 * 验证图片
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Picture(String value) {
		return match(V_PICTURE, value);
	}

	/**
	 * 验证正浮点数
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Posttive_float(String value) {
		return match(V_POSTTIVE_FLOAT, value);
	}

	/**
	 * 验证QQ号码
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean QQnumber(String value) {
		return match(V_QQ_NUMBER, value);
	}

	/**
	 * 验证压缩文件
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Rar(String value) {
		return match(V_RAR, value);
	}

	/**
	 * 验证电话
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Tel(String value) {
		return match(V_TEL, value);
	}

	/**
	 * 验证两位小数
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Two_point(String value) {
		return match(V_TWO＿POINT, value);
	}

	/**
	 * 验证非正浮点数
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Un_negative_float(String value) {
		return match(V_UN_NEGATIVE_FLOAT, value);
	}

	/**
	 * 验证非负浮点数
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Unpositive_float(String value) {
		return match(V_UNPOSITIVE_FLOAT, value);
	}

	/**
	 * 验证URL
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Url(String value) {
		return match(V_URL, value);
	}

	/**
	 * 验证用户注册。匹配由数字、26个英文字母或者下划线组成的字符串
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean UserName(String value) {
		return match(V_USERNAME, value);
	}

	/**
	 * 验证用户注册。匹配由中文、数字、26个英文字母或者下划线组成的字符串
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Account(String value) {
		if (Strs.isBlank(value))
			return false;
		return match(V_ACCOUNT, value);
	}

	/**
	 * 验证邮编
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Zipcode(String value) {
		return match(V_ZIPCODE, value);
	}

	/**
	 * @param regex
	 *            正则表达式字符串
	 * @param str
	 *            要匹配的字符串
	 * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
	 */
	public static boolean match(String regex, String str) {
		if (Strs.isBlank(str))
			return false;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 验证CSS tag 与 class id 的定义
	 * 
	 * @param value
	 *            要验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean Css(String value) {
		return match(V_CSS, value);
	}

	/// ----------------constant--begin-------------------///
	/** 正则中需要被转义的关键字 */
	public final static Character[] RE_KEYS = new Character[] { '$', '(', ')', '*', '+', '.', '[', ']', '?', '\\', '^',
			'{', '}', '|' };
	/** 整数 */
	public static final String V_INTEGER = "^-?[1-9]\\d*$";
	/** 正整数 */
	public static final String V_Z_INDEX = "^[1-9]\\d*$";
	/** 负整数 */
	public static final String V_NEGATIVE_INTEGER = "^-[1-9]\\d*$";
	/** 数字 */
	public static final String V_NUMBER = "^([+-]?)\\d*\\.?\\d+$";
	/** 正数 */
	public static final String V_POSITIVE_NUMBER = "^[1-9]\\d*|0$";
	/** 负数 */
	public static final String V_NEGATINE_NUMBER = "^-[1-9]\\d*|0$";
	/** 浮点数 */
	public static final String V_FLOAT = "^([+-]?)\\d*\\.\\d+$";
	/** 正浮点数 */
	public static final String V_POSTTIVE_FLOAT = "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*$";
	/** 负浮点数 */
	public static final String V_NEGATIVE_FLOAT = "^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$";
	/** 非负浮点数（正浮点数 + 0） */
	public static final String V_UNPOSITIVE_FLOAT = "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0$";
	/** 非正浮点数（负浮点数 + 0） */
	public static final String V_UN_NEGATIVE_FLOAT = "^(-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*))|0?.0+|0$";
	/** 邮件 */
	public static final String V_EMAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
	/** 颜色 */
	public static final String V_COLOR = "^[a-fA-F0-9]{6}$";
	/** 仅中文 */
	public static final String V_CHINESE = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$";
	/** 仅ACSII字符 */
	public static final String V_ASCII = "^[\\x00-\\xFF]+$";
	/** 邮编 */
	public static final String V_ZIPCODE = "^\\d{6}$";
	/** 手机 */
	public static final String V_MOBILE = "^(13|14|15|17|18)[0-9]{9}$";
	/** ip地址 */
	public static final String V_IP4 = "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$";
	/** 非空 */
	public static final String V_NOTEMPTY = "^\\S+$";
	/** 图片 */
	public static final String V_PICTURE = "(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$";
	/** 压缩文件 */
	public static final String V_RAR = "(.*)\\.(rar|zip|7zip|tgz)$";
	/// ** 日期 */
	// public static final String V_DATE =
	// "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))
	// (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";
	/** QQ号码 */
	public static final String V_QQ_NUMBER = "^[1-9]*[1-9][0-9]*$";
	/** 电话号码的函数(包括验证国内区号,国际区号,分机号) */
	public static final String V_TEL = "^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)?(\\d{7,8})(-(\\d{3,}))?$";
	/** 用来用户注册。匹配由数字、26个英文字母或者下划线组成的字符串 */
	public static final String V_USERNAME = "^\\w+$";
	/** 用来用户注册。匹配由中文、数字、26个英文字母或者下划线组成的字符串 */
	public static final String V_ACCOUNT = "[\u4e00-\u9fa5\\w]+";
	/** 字母 */
	public static final String V_LETTER = "^[A-Za-z]+$";
	/** 大写字母 */
	public static final String V_LETTER_U = "^[A-Z]+$";
	/** 小写字母 */
	public static final String V_LETTER_I = "^[a-z]+$";
	/** 身份证 */
	public static final String V_IDCARD = "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$";
	/** 验证密码(数字和英文同时存在) */
	public static final String V_PASSWORD_REG = "[A-Za-z]+[0-9]";
	/** 验证密码长度(6-18位) */
	public static final String V_PASSWORD_LENGTH = "^\\d{6,18}$";
	/** 验证两位数 */
	public static final String V_TWO＿POINT = "^[0-9]+(.[0-9]{2})?$";
	/** 验证一个月的31天 */
	public static final String V_31DAYS = "^((0?[1-9])|((1|2)[0-9])|30|31)$";
	/** 验证CSS的class tag id */
	public static final String V_CSS = "^[class|id|tag]+\\[+([\\w-])+\\]+(\\[([0-9]+)\\])?$";
	/** 日期型正则文本格式（YYYY-MM-DD） */
	public static final String V_DATE_YMD = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
	/** 时间型正则文本格式（YYYY-MM-DD HH:MM:SS） */
	public static final String V_DATETIME = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8])))))) ((0[0-9])|(1[0-9])|(2[0-3])):[0-5][0-9]:[0-5][0-9]{1}";
	/** 中国电话 */
	public static final String V_CPhoneNnumber = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$|^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$";
	/** 中文字符 */
	public static final String V_ChineseChar = "[\\u4E00-\\u9FA5\\uF900-\\uFA2D]";
	/** 双字节字符 */
	public static final String V_DoubleChar = "[^\\x00-\\xff]";
	/** 字符串中含有中文 */
	public static final String V_hasAnyChinese = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]{1,}$";
	/** 通用url */
	public static final String V_URL = "^(\\w+)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]$";
	/** https or http url */
	public static final String V_HTTPSORHTTPURL = "^https?://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]$";
	/** http url */
	public static final String V_HTTPURL = "^http://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]$";
	/** https url */
	public static final String V_HTTPSURL = "^https://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]$";
	/** 域名 */
	public static final String V_DOMAIN = "((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+(:[0-9]+)?|(?:ww‌​w.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:\\/[\\+~%\\/.\\w-_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?‌​(?:[\\w]*))?)";
	/** 字母开头，4字节以上，允许字母数字下划线 */
	public static final String V_checkAccount = "^[a-zA-Z][a-zA-Z0-9_]{3,}$";
	/** UUID */
	public final static Pattern V_UUID = Pattern
			.compile("^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$");
	/** 不带横线的UUID */
	public final static Pattern V_UUID_SIMPLE = Pattern.compile("^[0-9a-z]{32}$");
	/** 中国车牌号码 */
	public final static Pattern V_PLATE_NUMBER = Pattern
			.compile("^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$");
	/** MAC地址正则 */
	public static final Pattern V_MAC_ADDRESS = Pattern
			.compile("((?:[A-F0-9]{1,2}[:-]){5}[A-F0-9]{1,2})|(?:0x)(\\d{12})(?:.+ETHER)", Pattern.CASE_INSENSITIVE);

	/// ----------------constant--end-------------------///
}
