package cn.zyf.operation;

import cn.zyf.model.ShipAttribute;
import cn.zyf.resource.Resource;
import cn.zyf.util.GsonUtils;
import cn.zyf.util.HttpPostUtils;
import cn.zyf.util.HttpUtils;
import cn.zyf.util.ResponseDecoder;
import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {

    private static final String URL = HttpUtils.server + "/kcsapi/api_start2/getData";

    public static void get() {
        HttpUtils http = new HttpPostUtils(URL);
        http.addPostParameter(generateBody());
        HttpResponse response = http.execute();

        if (200 != response.getStatusLine().getStatusCode()) {
            System.err.println("[Data]unexpected http code: " + response.getStatusLine().getStatusCode());
            throw new RuntimeException();
        }

        Map<String, Object> result = ResponseDecoder.getHeaderAndBody(response);
        String responseBody = String.valueOf(result.get("body"));
        Map svdata = GsonUtils.json2map(responseBody);

        int api_result = ((Double) svdata.get("api_result")).intValue();
        String api_result_msg = (String) svdata.get("api_result_msg");
        if (1 != api_result && (!("成功".equals(api_result_msg) || "".equals(api_result_msg)))) {
            System.err.println(String.format("[Data]unexpected response body, api_result: %s, api_result_msg: %s", api_result, api_result_msg));
            throw new RuntimeException();
        }

        saveResource(svdata);
    }

    private static void saveResource(Map svdata) {
        Map api_data = (Map) svdata.get("api_data");
        List api_mst_ship = (List) api_data.get("api_mst_ship");
        Map<Integer, ShipAttribute> map = new HashMap<Integer, ShipAttribute>();
        for (int i = 0; i < api_mst_ship.size(); ++i) {
            Map m = (Map) api_mst_ship.get(i);
            ShipAttribute ship = new ShipAttribute();
            ship.setApi_id(((Double) m.get("api_id")).intValue());
            ship.setApi_name((String) m.get("api_name"));
            // 敌方舰队无油料和弹药属性，随便设置成0
            ship.setApi_bull_max(((Double) m.getOrDefault("api_bull_max", 0.0)).intValue());
            ship.setApi_fuel_max(((Double) m.getOrDefault("api_fuel_max", 0.0)).intValue());
            map.put(ship.getApi_id(), ship);
        }
        Resource.setProperty("ship_attribute", map);
    }

    private static Map<String, String> generateBody() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("api_token", String.valueOf(Resource.getProperty("api_token")));
        map.put("api_verno", "1");
        return map;
    }

}
