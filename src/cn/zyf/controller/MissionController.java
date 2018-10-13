package cn.zyf.controller;

import cn.zyf.config.Config;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/mission")
public class MissionController {

    @RequestMapping("/start")
    @ResponseBody
    public boolean start(HttpServletRequest request) {
        if (Config.stop) {
            Config.stop = false;
            return true;
        }
        return false;
    }

    @RequestMapping("/stop")
    @ResponseBody
    public boolean stop(HttpServletRequest request) {
        if (!Config.stop) {
            Config.stop = true;
            return true;
        }
        return false;
    }

}
