/**
 * 文件名称：LogUtil.java
 * 版权所有：Copyright njty
 * 创建时间：2017年6月8日
 * 创 建 人：WCL (ln_admin@yeah.net)
 * 功能描述：
 **/
package com.gesoft.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志记录类
 */
public class LogUtil {

    private static Logger log;

    /**
     * Constructor for the LogUtil object
     */
    private LogUtil() {
    	log = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    }

    /**
     * Gets the Logger attribute of the LogUtil class
     * 
     * @return The Logger value
     */
    public static Logger getLogger() {
        if (log == null) {
        	log = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        }
        return log;
    }

    /**
     * Gets the debug attribute of the LogUtil class
     * 
     * @return The debug value
     */
    public static boolean isDebug() {
        return log.isDebugEnabled();
    }

}
