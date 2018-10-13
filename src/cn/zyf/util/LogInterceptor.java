package cn.zyf.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;

public class LogInterceptor {

    private Logger logger = LogManager.getLogger(LogInterceptor.class);

    public void before(JoinPoint joinPoint) {
        HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[0];
        logger.info(String.format("From %s:%s Receive New Http Request [%s]", request.getRemoteAddr(), request.getRemotePort(), request.getRequestURI()));
    }

    public static void info(String msg) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()).toString();
        System.out.println(String.format("[%s] %s", time, msg));
    }

    public static void error(String msg) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()).toString();
        System.err.println(String.format("[%s] %s", time, msg));
    }

}
