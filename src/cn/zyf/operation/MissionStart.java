package cn.zyf.operation;

import cn.zyf.resource.Resource;
import cn.zyf.util.GsonUtils;
import cn.zyf.util.HttpPostUtils;
import cn.zyf.util.HttpUtils;
import cn.zyf.util.ResponseDecoder;
import org.apache.http.HttpResponse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MissionStart {

    // 来源于main.js
    // e.PORT_API_SEED = [4427, 6755, 3264, 7474, 2823, 6304, 6225, 8447, 3219, 4527]
    private static final int PORT_API_SEED[] = new int[]{4427, 6755, 3264, 7474, 2823, 6304, 6225, 8447, 3219, 4527};
    private static final String URL = HttpUtils.server + "/kcsapi/api_req_mission/start";

    private static final double MISSION_CLICK_CENTER_X = 470; // 200~800, 150
    private static final double MISSION_CLICK_CENTER_Y[] = new double[] {260, 306, 351, 397, 443, 489, 534, 580}; // 15

    public static void start(int deckId, int missionId) {
        HttpUtils http = new HttpPostUtils(URL);
        http.addPostParameter(generateBody(deckId, missionId));
        HttpResponse response = http.execute();

        if (200 != response.getStatusLine().getStatusCode()) {
            System.err.println("[MissionStart]unexpected http code: " + response.getStatusLine().getStatusCode());
            throw new RuntimeException();
        }

        Map<String, Object> result = ResponseDecoder.getHeaderAndBody(response);
        String responseBody = String.valueOf(result.get("body"));
        Map svdata = GsonUtils.json2map(responseBody);

        int api_result = ((Double) svdata.get("api_result")).intValue();
        String api_result_msg = (String) svdata.get("api_result_msg");
        if (1 != api_result && (!("成功".equals(api_result_msg) || "".equals(api_result_msg)))) {
            System.err.println(String.format("[MissionStart]unexpected response body, api_result: %s, api_result_msg: %s", api_result, api_result_msg));
            throw new RuntimeException();
        }

        saveResource(deckId, svdata);
    }

    private static void saveResource(int deckId, Map svdata) {
        Map api_data = (Map) svdata.get("api_data");
        long api_complatetime = ((Double) api_data.get("api_complatetime")).longValue();
        String api_complatetime_str = (String) api_data.get("api_complatetime_str");

        Map deckMap = (Map) Resource.getProperty("decks");
        Map deckInfo = (Map) deckMap.get("deck_" + deckId);
        deckInfo.put("api_complatetime", api_complatetime);
        deckInfo.put("api_complatetime_str", api_complatetime_str);
    }

    private static Map<String, String> generateBody(int deckId, int missionId) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("api_token", String.valueOf(Resource.getProperty("api_token")));
        map.put("api_verno", "1");
        map.put("api_mission_id", String.valueOf(missionId));
        map.put("api_deck_id", String.valueOf(deckId));
        map.put("api_mission", String.valueOf((int) Math.floor(Math.random() * 100)));
        map.put("api_serial_cid", __create_serial_id__(missionId));
        return map;
    }

    private static String __create_serial_id__(int missionId) {
        int t = (int) Resource.getProperty("api_member_id");
        // 点击远征列表时鼠标的x,y值，在一定范围内随机生成
        double x = 0.0;
        double y = 0.0;
        int index = 7;
        if (missionId < 100) {
            // 认为小于100的远征ID是常规远征，即不属于（A1、A2、B1等），否则都在最下面，即第8个位置
            index = (missionId - 1) % 8;
        }
        x = MISSION_CLICK_CENTER_X + (Math.random() - 0.5) * 150;
        y = MISSION_CLICK_CENTER_Y[index] + (Math.random() - 0.5) * 15;
        long r = (long) Math.floor(new Date().getTime() / 1e3);
        long e = (long) (Math.round(x) % 1e3 + 1e3);
        long i = (long) (Math.round(y) % 1e3 + 1e3);
        long s = (long) (1e4 * e + i);
        s *= MissionStart.PORT_API_SEED[t % 10];
        return String.valueOf(r) + String.valueOf(s);
    }
}
