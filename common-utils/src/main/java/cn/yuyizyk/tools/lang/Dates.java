package cn.yuyizyk.tools.lang;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日期字符串
 * 
 * @author yuyi
 *
 */
public class Dates {
	private final static Logger log = LoggerFactory.getLogger(Dates.class);

	public static String getWeek(Date date) {
		final String dayNames[] = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayOfWeek < 0)
			dayOfWeek = 0;
		return dayNames[dayOfWeek];
	}

	/**
	 * 转换为时间
	 */
	public static Date toDate(Object value) {
		if (value == null) {
			return null;
		}
		try {
			Class<?> clazz = value.getClass();
			if (clazz.isPrimitive()) {
				if (value.toString().length() == 13) {
					return new Date(Long.valueOf(value.toString()));
				}
			}
			if (Regs.isMatcher(value.toString(), "\\d{13}")) {
				value = new Date(Long.valueOf(value.toString()));
				return (Date) value;
			}
			if (Regs.isMatcher(value.toString(), "\\d{8}")) {
				value = new SimpleDateFormat("yyyyMMdd").parse(value.toString());
				return (Date) value;
			}
			if (Regs.isMatcher(value.toString(), "\\d{14}")) {
				value = new SimpleDateFormat("yyyyMMddHHmmss").parse(value.toString());
				return (Date) value;
			}
			if (Regs.isMatcher(value.toString(), "\\d{17}")) {
				value = new SimpleDateFormat("yyyyMMddHHmmssSSS").parse(value.toString());
				return (Date) value;
			}
			if (Regs.isMatcher(value.toString(), "[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}")) {
				value = new SimpleDateFormat("yyyy-MM-dd").parse(value.toString());
				return (Date) value;
			}
			if (Regs.isMatcher(value.toString(),
					"^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*")) {
				value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value.toString());
				return (Date) value;
			}
			return (Date) value;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 判断是否为日期型数据
	 * 
	 * @Description: 判断是否为日期型数据(YYYY-MM-DD)
	 */
	public static boolean isYMD(String date) {
		return Regs.matches(date, Regs.V_DATE_YMD);
	}

	/**
	 * 判断是否为时间型数据
	 * 
	 * @Description: 判断是否为时间型数据(YYYY-MM-DD HH:MM:SS)
	 */
	public static boolean isDateTime(String dateString) {
		return Regs.matches(dateString, Regs.V_DATETIME);
	}

	private static final String ZID = "Asia/Shanghai"; // 上海

	// /**
	// *
	// * @Title: between @Description: TODO @param @param stime 开始时间 @param @param
	// * etime 结束时间 @param @return 时间间隔 @return Duration Duration.toDays();
	// * 取得时间间隔的天数 Duration.toHours(); 取得时间将的小时数 Duration.toMinutes();
	// * 取得时间间隔的分钟数 @throws
	// */
	// public static Duration between(LocalDateTime stime, LocalDateTime etime) {
	// return Duration.between(stime, etime);
	// }

	/**
	 * 
	 * @Title: parse @Description: 把字符串转为时间 @param @param str @param @return
	 *         设定文件 @return LocalDateTime 返回类型 @throws
	 */
	public static LocalDateTime parse(String str) {
		if (StringUtils.isEmpty(str))
			return null;
		String[] parsePatterns = { "yyyy-MM-dd", "yyyy/MM/dd", "dd/MM/yyyy", "HH:mm:ss", "yyyy-MM-dd HH:mm:ss",
				"yyyy-MM-dd HH:mm:ss.SSS", "yyyyMMdd HHmmss", "yyyyMMddHHmmss", "yyyy-MM-dd'T'HH:mm:ss",
				"yyyy-MM-dd'T'HH:mm:ss.SSS", "EEEE MMM dd HH:mm:ss Z yyyy", "MMM d, yyyy HH:mm:ss a",
				"yyyy年MM月dd日 HH时mm分ss秒" };
		Date date = new Date();
		try {
			date = org.apache.commons.lang3.time.DateUtils.parseDate(str, Locale.US, parsePatterns);

			return toLocalDateTime(date);
		} catch (ParseException e) {
			log.error("ParseException date :[{}]", str, e);
			return null;
		}

	}

	/**
	 * 
	 * @Title: format @Description: 时间转换为字符串 @param @param
	 *         localDateTime @param @param pattern @param @return 设定文件 @return
	 *         String 返回类型 @throws
	 */
	public static String format(LocalDateTime localDateTime, String pattern) {
		if (pattern == null)
			pattern = "yyyy-MM-dd HH:mm:ss";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.US);
		return localDateTime.format(formatter);

	}

	public static String format(LocalDate localDate, String pattern) {
		if (pattern == null)
			pattern = "yyyy-MM-dd";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.US);
		return localDate.format(formatter);
	}

	/**
	 * 
	 * @Title: toLocalDateTime @Description: TODO Date 转为
	 *         LocalDateTime @param @param date @param @return 设定文件 @return
	 *         LocalDateTime 返回类型 @throws
	 */
	public static LocalDateTime toLocalDateTime(Date date) {
		Instant instant = date.toInstant();
		return LocalDateTime.ofInstant(instant, ZoneId.of(ZID));
	}

	public static LocalDateTime toLocalDateTime(java.sql.Date date) {
		return date.toLocalDate().atTime(0, 0);
	}

	/**
	 * 
	 * @Title: toDate @Description: LocalDateTime 转为 Date @param @param
	 *         localDateTime @param @return 设定文件 @return Date 返回类型 @throws
	 */
	public static Date toDate(LocalDateTime localDateTime) {
		ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(ZID));
		Instant instant = zonedDateTime.toInstant();
		return Date.from(instant);
	}

	public static java.sql.Date toSqlDate(LocalDateTime localDateTime) {
		ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(ZID));
		Instant instant = zonedDateTime.toInstant();
		return new java.sql.Date(Date.from(instant).getTime());
	}

	public static Timestamp toTimestamp(LocalDateTime localDateTime) {
		ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(ZID));
		Instant instant = zonedDateTime.toInstant();
		return new Timestamp(Date.from(instant).getTime());
	}

	/**
	 * 根据生日字符串计算年龄(1991-11-13)
	 * 
	 * @param birthTimeString
	 * @return
	 */
	public static int getAgeFromBirthStr(String birthTimeString) {
		// 先截取到字符串中的年、月、日
		String strs[] = birthTimeString.trim().split("-");
		int selectYear = Integer.parseInt(strs[0]);
		int selectMonth = Integer.parseInt(strs[1]);
		int selectDay = Integer.parseInt(strs[2]);
		// 得到当前时间的年、月、日
		Calendar cal = Calendar.getInstance();
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH) + 1;
		int dayNow = cal.get(Calendar.DATE);

		// 用当前年月日减去生日年月日
		int yearMinus = yearNow - selectYear;
		int monthMinus = monthNow - selectMonth;
		int dayMinus = dayNow - selectDay;

		int age = yearMinus;// 先大致赋值
		if (yearMinus < 0) {// 选了未来的年份
			age = 0;
		} else if (yearMinus == 0) {// 同年的，要么为1，要么为0
			if (monthMinus < 0) {// 选了未来的月份
				age = 0;
			} else if (monthMinus == 0) {// 同月份的
				if (dayMinus < 0) {// 选了未来的日期
					age = 0;
				} else if (dayMinus >= 0) {
					age = 1;
				}
			} else if (monthMinus > 0) {
				age = 1;
			}
		} else if (yearMinus > 0) {
			if (monthMinus < 0) {// 当前月>生日月
			} else if (monthMinus == 0) {// 同月份的，再根据日期计算年龄
				if (dayMinus < 0) {
				} else if (dayMinus >= 0) {
					age = age + 1;
				}
			} else if (monthMinus > 0) {
				age = age + 1;
			}
		}
		return age;
	}

}
