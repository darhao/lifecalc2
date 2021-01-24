package cc.darhao.lifecalc.util;


import org.slf4j.Logger;

public class ExceptionUtil {
	
	private final static String PACKAGE_NAME_PREFIX = "cc.darhao";
	
	
	public static void logError(Logger logger, Throwable e) {
		for (StackTraceElement element : e.getStackTrace()) {
			if(element.toString().startsWith(PACKAGE_NAME_PREFIX)) {
				logger.error(getFullInfo(e));
				return;
			}
		}
		logWarn(logger, e);
	}
	
	
	public static void logWarn(Logger logger, Throwable e) {
		logger.warn(getFullInfo(e));
	}
	

	public static String getErrorStackString(Throwable e) {
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] stackTraceElements = e.getStackTrace();
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			sb.append("\n");
			sb.append(stackTraceElement.toString());
		}
		return sb.toString();
	}


	public static String getFullInfo(Throwable e){
		return e.getClass().getSimpleName() + " : " + e.getMessage() + getErrorStackString(e);
	}

}
