package cn.yuyizyk.tools.exception;

/**
 * 
 * 
 * 
 *
 * @author yuyi
 */
public class BoxException extends RuntimeException {
	private transient static final long serialVersionUID = 1L;

	public BoxException(Throwable e) {
		super(e);
	}

}
