package cn.zyf.controller;

import cn.zyf.config.Config;
import cn.zyf.resource.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/check")
public class CheckController {

    private static final Logger logger = LogManager.getLogger(CheckController.class);

    @RequestMapping("/resource")
    @ResponseBody
    public String getResource(HttpServletRequest request) {

        String htmlResult = "";
        htmlResult += "初始油： " + Resource.getProperty("initial_fuel") + "<br>";
        htmlResult += "油： " + Resource.getProperty("fuel") + "<br>";
        htmlResult += "初始弹： " + Resource.getProperty("initial_bull") + "<br>";
        htmlResult += "弹： " + Resource.getProperty("bull") + "<br>";
        htmlResult += "初始钢： " + Resource.getProperty("initial_steel") + "<br>";
        htmlResult += "钢： " + Resource.getProperty("steel") + "<br>";
        htmlResult += "初始铝： " + Resource.getProperty("initial_al") + "<br>";
        htmlResult += "铝： " + Resource.getProperty("al") + "<br>";
        htmlResult += "初始桶： " + Resource.getProperty("initial_tong") + "<br>";
        htmlResult += "桶： " + Resource.getProperty("tong") + "<br><br>";

        htmlResult += "程序启动时间： " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Resource.getProperty("program_start_time")).toString() + "<br>";
        htmlResult += "stop: " + String.valueOf(Config.stop) + "<br><br>";

        Map deckMap = (Map) Resource.getProperty("decks");
        Map deck2Info = (Map) deckMap.get("deck_2");
        Map deck3Info = (Map) deckMap.get("deck_3");
        Map deck4Info = (Map) deckMap.get("deck_4");
        htmlResult += "第2舰队补给次数: " + Resource.getProperty("mission_2_count") + "<br>";
        htmlResult += "第3舰队补给次数: " + Resource.getProperty("mission_3_count") + "<br>";
        htmlResult += "第4舰队补给次数: " + Resource.getProperty("mission_4_count") + "<br>";
        htmlResult += "第2舰队归还时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(deck2Info.get("api_complatetime")).toString() + "<br>";
        htmlResult += "第3舰队归还时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(deck3Info.get("api_complatetime")).toString() + "<br>";
        htmlResult += "第4舰队归还时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(deck4Info.get("api_complatetime")).toString() + "<br>";

        return htmlResult;
    }

}
