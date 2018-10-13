package cn.zyf.operation;

import cn.zyf.model.Ship;
import cn.zyf.resource.Resource;
import cn.zyf.util.GsonUtils;
import cn.zyf.util.HttpPostUtils;
import cn.zyf.util.HttpUtils;
import cn.zyf.util.ResponseDecoder;
import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

public class Port {

    private static final Logger logger = LogManager.getLogger(Port.class);

    // 来源于main.js
    // e.PORT_API_SEED = [4427, 6755, 3264, 7474, 2823, 6304, 6225, 8447, 3219, 4527]
    private static final int PORT_API_SEED[] = new int[]{4427, 6755, 3264, 7474, 2823, 6304, 6225, 8447, 3219, 4527};
    private static final String URL = HttpUtils.server + "/kcsapi/api_port/port";

    public static void port() {
        HttpUtils http = new HttpPostUtils(URL);
        http.addPostParameter(generateBody());
        HttpResponse response = http.execute();

        if (200 != response.getStatusLine().getStatusCode()) {
            logger.error("[Port]unexpected http code: " + response.getStatusLine().getStatusCode());
            throw new RuntimeException();
        }

        Map<String, Object> result = ResponseDecoder.getHeaderAndBody(response);
        String responseBody = String.valueOf(result.get("body"));
        Map svdata = GsonUtils.json2map(responseBody);

        int api_result = ((Double) svdata.get("api_result")).intValue();
        String api_result_msg = (String) svdata.get("api_result_msg");
        if (1 != api_result && (!("成功".equals(api_result_msg) || "".equals(api_result_msg)))) {
            logger.error(String.format("[Port]unexpected response body, api_result: %s, api_result_msg: %s", api_result, api_result_msg));
            throw new RuntimeException();
        }

        saveResource(svdata);
    }

    private static void saveResource(Map svdata) {
        Map api_data = (Map) svdata.get("api_data");
        List api_material = (List) api_data.get("api_material");
        for (int i = 0; i < api_material.size(); ++i) {
            Map m = (Map) api_material.get(i);
            int value = ((Double) m.get("api_value")).intValue();
            switch (((Double) m.get("api_id")).intValue()) {
                case 1:
                    Resource.setProperty("fuel", value);
                    if (null == Resource.getProperty("initial_fuel")) {
                        Resource.setProperty("initial_fuel", value);
                    }
                    break;
                case 2:
                    Resource.setProperty("bull", value);
                    if (null == Resource.getProperty("initial_bull")) {
                        Resource.setProperty("initial_bull", value);
                    }
                    break;
                case 3:
                    Resource.setProperty("steel", value);
                    if (null == Resource.getProperty("initial_steel")) {
                        Resource.setProperty("initial_steel", value);
                    }
                    break;
                case 4:
                    Resource.setProperty("al", value);
                    if (null == Resource.getProperty("initial_al")) {
                        Resource.setProperty("initial_al", value);
                    }
                    break;
                case 5:
                    Resource.setProperty("penghuo", value);
                    break;
                case 6:
                    Resource.setProperty("tong", value);
                    if (null == Resource.getProperty("initial_tong")) {
                        Resource.setProperty("initial_tong", value);
                    }
                    break;
                case 7:
                    Resource.setProperty("zicai", value);
                    break;
                case 8:
                    Resource.setProperty("luosi", value);
                    break;
            }
        }
        List api_deck_port = (List) api_data.get("api_deck_port");
        Map<String, Map> deckMap = new HashMap<String, Map>();
        for (int i = 0; i < api_deck_port.size(); ++i) {
            Map deck = (Map) api_deck_port.get(i);
            int api_id = ((Double) deck.get("api_id")).intValue();
            List shipIdList = (List) deck.get("api_ship");
            // 长度为4，第一个是远征页面，第二个是该页面第几个远征，第三个是远征完成时间，第四个未知
            List apiMission = (List) deck.get("api_mission");
            List shipIdListTransToInt = new ArrayList();
            for (int j = 0; j < shipIdList.size(); ++j) {
                int id = ((Double) shipIdList.get(j)).intValue();
                if (id != -1) {
                    shipIdListTransToInt.add(id);
                }
            }
            String key = "deck_" + api_id;
            Map value = new HashMap();
            value.put("api_ship", shipIdListTransToInt);
            long api_complatetime = ((Double) apiMission.get(2)).longValue();
            value.put("api_complatetime", api_complatetime);
            value.put("api_complatetime_str", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(api_complatetime).toString());
            deckMap.put(key, value);
        }
        Resource.setProperty("decks", deckMap);

        List api_ship = (List) api_data.get("api_ship");
        Map<Integer, Ship> shipMap = new HashMap<Integer, Ship>();
        for (int i = 0; i < api_ship.size(); ++i) {
            Map m = (Map) api_ship.get(i);
            Ship ship = new Ship();
            ship.setApi_id(((Double) m.get("api_id")).intValue());
            ship.setApi_ship_id(((Double) m.get("api_ship_id")).intValue());
            ship.setApi_fuel(((Double) m.get("api_fuel")).intValue());
            ship.setApi_bull(((Double) m.get("api_bull")).intValue());
            shipMap.put(ship.getApi_id(), ship);
        }
        Resource.setProperty("ships", shipMap);
    }

    private static Map<String, String> generateBody() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("api_token", String.valueOf(Resource.getProperty("api_token")));
        map.put("api_verno", "1");
        map.put("api_sort_key", "5");
        map.put("spi_sort_order", "2");
        map.put("api_port", createKey());
        return map;
    }

    private static String createKey() {
        int t = (int) Resource.getProperty("api_member_id");

        int e = getSeed(t);
        long i = (long) Math.floor(new Date().getTime() / 1e3);
        long n = (long) (1e3 * (Math.floor(9 * Math.random()) + 1) + t % 1e3);
        long o = (long) (Math.floor(8999 * Math.random()) + 1e3);
        long r = (long) (Math.floor(32767 * Math.random()) + 32768);
        long s = (long) (Math.floor(10 * Math.random()));
        long a = (long) (Math.floor(10 * Math.random()));
        long _ = (long) (Math.floor(10 * Math.random()));
        int u = Integer.parseInt(String.valueOf(t).substring(0, 4));
        double l = (4132653 + r) * (u + 1e3) - i + (1875979 + 9 * r);
        double c = l - t;
        double h = c * e;
        String p = String.valueOf(n) + String.valueOf((long) h) + String.valueOf(o);
        p = String.valueOf(s) + p;

        String d = p.substring(0, 8);
        String f = p.substring(8);

        p = d + String.valueOf(a) + f;
        d = p.substring(0, 18);
        f = p.substring(18);
        return d + _ + f + String.valueOf(r);
    }

    private static int getSeed(int t) {
        return PORT_API_SEED[t % 10];
    }

}
