package cn.zyf.controller;

import cn.zyf.config.Config;
import cn.zyf.entry.MissionThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/thread")
public class ThreadController {

    private static final Logger logger = LogManager.getLogger(ThreadController.class);

    @RequestMapping("/get")
    @ResponseBody
    public String getThreadState(HttpServletRequest request) {
        String htmlResult = "";
        htmlResult += "Mission-2: " + Config.t2.getState() + "<br>";
        htmlResult += "Mission-3: " + Config.t3.getState() + "<br>";
        htmlResult += "Mission-4: " + Config.t4.getState() + "<br>";

        String t2StackTrace = "";
        String t3StackTrace = "";
        String t4StackTrace = "";

        Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
        for (Thread t : map.keySet()) {
            if (t == Config.t2) {
                for (StackTraceElement trace : map.get(t)) {
                    t2StackTrace += trace.toString() + "<br>";
                }
            }
            if (t == Config.t3) {
                for (StackTraceElement trace : map.get(t)) {
                    t3StackTrace += trace.toString() + "<br>";
                }
            }
            if (t == Config.t4) {
                for (StackTraceElement trace : map.get(t)) {
                    t4StackTrace += trace.toString() + "<br>";
                }
            }
        }

        return htmlResult + "<br>Mission-2 Trace:<br>" + t2StackTrace + "<br>Mission-3 Trace:<br>" + t3StackTrace + "<br>Mission-4 Trace:<br>" + t4StackTrace;
    }

    @RequestMapping("/kill")
    @ResponseBody
    public void killThread(HttpServletRequest request) {
        logger.info("receive kill thread request, id = " + request.getParameter("t"));
        int id = Integer.parseInt(request.getParameter("t"));
        if (id != 2 && id != 3 && id != 4) {
            return;
        }
        Thread threadArray[] = new Thread[]{null, null, Config.t2, Config.t3, Config.t4};
        Thread.getAllStackTraces().forEach((k, v) -> {
            if (k == threadArray[id] && k.isAlive()) {
                logger.info("send interrupt signal to " + k.getName());
                k.interrupt();
            }
        });
    }

    @RequestMapping("/recover")
    @ResponseBody
    public void recoverThread(HttpServletRequest request) {
        logger.info("receive recover thread request, id = " + request.getParameter("t"));
        int id = Integer.parseInt(request.getParameter("t"));
        if (id != 2 && id != 3 && id != 4) {
            return;
        }
        Thread threadArray[] = new Thread[]{null, null, Config.t2, Config.t3, Config.t4};
        if (threadArray[id].isAlive()) {
            logger.error("cannot recover a thread which is already alive: " + threadArray[id].getName());
            return;
        } else {
            switch (id) {
                case 2:
                    Config.t2 = new Thread(new MissionThread(2), "Mission-2");
                    Config.t2.start();
                    break;
                case 3:
                    Config.t3 = new Thread(new MissionThread(3), "Mission-3");
                    Config.t3.start();
                    break;
                case 4:
                    Config.t4 = new Thread(new MissionThread(4), "Mission-4");
                    Config.t4.start();
                    break;
            }
        }
        logger.info("recover thread Mission-" + id + " success!");
    }

}
