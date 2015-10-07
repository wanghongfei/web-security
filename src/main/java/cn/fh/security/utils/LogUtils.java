package cn.fh.security.utils;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;


/**
 * Created by whf on 8/16/15.
 */
public class LogUtils {
    private LogUtils() {}

    /**
     * 简化logger代码，去掉if..log.isDebugEnabled()这样的判断
     */
    public static void printLog(Logger logger, Level level, String baseStr, Object... parms) {
        if (Level.DEBUG == level) {
            if (logger.isDebugEnabled()) {
                logger.debug(baseStr, parms);
            }
        }

        if (Level.INFO == level) {
            logger.info(baseStr, parms);
        }
    }
}
