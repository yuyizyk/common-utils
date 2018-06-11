package cn.yuyizyk.tools.exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.yuyizyk.tools.lang.Strs;

/**
 * 关于异常的工具类.
 * 
 */
public class Exceptions {
	private final static Logger log = LoggerFactory.getLogger(Exceptions.class);

	/**
	 * 将CheckedException转换为UncheckedException.
	 */
	public static RuntimeException unchecked(Exception e) {
		if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		} else {
			return new RuntimeException(e);
		}
	}

	/**
	 * log.error("",e); <br/>
	 * throw new BoxException(e);
	 * 
	 * @param e
	 */
	public static BoxException box(Throwable e, String msg) {
		log.error(msg, e);
		return box(e);
	}

	/**
	 * log.error("",e); <br/>
	 * throw new BoxException(e);
	 * 
	 * @param e
	 * @return
	 */
	public static BoxException box(Throwable e, String fromStr, Object... args) {
		return box(e, Strs.format(fromStr, args), e);
	}

	/**
	 * log.error("",e); <br/>
	 * throw new BoxException(e);
	 * 
	 * @param e
	 */
	public static BoxException box(Throwable e, boolean logFlag) {
		if (logFlag) {
			log.error("", e);
		}
		return box(e);
	}

	/**
	 * 包装
	 * 
	 * @param e
	 * @return
	 */
	public static BoxException box(Throwable e) {
		if (e instanceof BoxException) {
			return (BoxException) e;
		} else {
			return new BoxException(e);
		}
	}

	/**
	 * 拆箱
	 * 
	 * @param e
	 * @return
	 */
	public static Throwable unbox(Throwable e) {
		while (e instanceof BoxException) {
			e = e.getCause();
		}
		return e;
	}

	/**
	 * 将ErrorStack转化为String.
	 */
	public static String getStackTraceAsString(Exception e) {
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}

	/**
	 * 判断异常是否由某些底层的异常引起.
	 */
	public static boolean isCausedBy(Exception ex,
			@SuppressWarnings("unchecked") Class<? extends Exception>... causeExceptionClasses) {
		Throwable cause = ex.getCause();
		while (cause != null) {
			for (Class<? extends Exception> causeClass : causeExceptionClasses) {
				if (causeClass.isInstance(cause)) {
					return true;
				}
			}
			cause = cause.getCause();
		}
		return false;
	}

	/**
	 * 获取Exception的堆栈新息。用于显示出错来源时使用。
	 * 
	 * @param e
	 *            Exception对象
	 * @param length
	 *            需要的信息长度，如果 <=0,表示全部信息
	 * @return String 返回该Exception的堆栈新息
	 * @author AIXIANG
	 */
	public static String getErrorStack(Throwable e) {
		String error = null;
		if (e != null) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos);
				e.printStackTrace(ps);
				error = baos.toString();
				baos.close();
				ps.close();
			} catch (Exception e1) {
				error = e.toString();
			}
		}
		return error;
	}

	public static void printException(Logger logger, Throwable e) {
		String error = getErrorStack(e);
		logger.info(error);
		logger.error(error);
	}

}
