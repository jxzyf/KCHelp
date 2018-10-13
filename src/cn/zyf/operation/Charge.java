package cn.zyf.operation;

import cn.zyf.resource.Resource;
import cn.zyf.util.GsonUtils;
import cn.zyf.util.HttpPostUtils;
import cn.zyf.util.HttpUtils;
import cn.zyf.util.ResponseDecoder;
import org.apache.http.HttpResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Charge {

        // 来源于main.js
        // e.PORT_API_SEED = [7399, 9308, 7908, 2340, 4609, 7707, 7181, 6875, 2859, 6114]
        private static final int PORT_API_SEED[] = new int[]{7399, 9308, 7908, 2340, 4609, 7707, 7181, 6875, 2859, 6114};
        private static final String URL = HttpUtils.server + "/kcsapi/api_req_hokyu/charge";

        public static void charge(int deckId) {
            HttpUtils http = new HttpPostUtils(URL);
            http.addPostParameter(generateBody(deckId));
            HttpResponse response = http.execute();

            if (200 != response.getStatusLine().getStatusCode()) {
                System.err.println("[Charge]unexpected http code: " + response.getStatusLine().getStatusCode());
                throw new RuntimeException();
            }

            Map<String, Object> result = ResponseDecoder.getHeaderAndBody(response);
            String responseBody = String.valueOf(result.get("body"));
            Map svdata = GsonUtils.json2map(responseBody);

            int api_result = ((Double) svdata.get("api_result")).intValue();
            String api_result_msg = (String) svdata.get("api_result_msg");
            if (1 != api_result && (!("成功".equals(api_result_msg) || "".equals(api_result_msg)))) {
                System.err.println(String.format("[Charge]unexpected response body, api_result: %s, api_result_msg: %s", api_result, api_result_msg));
                throw new RuntimeException();
            }

            saveResource(svdata);
        }

        private static void saveResource(Map svdata) {
            Map api_data = (Map) svdata.get("api_data");
        }

        private static Map<String, String> generateBody(int deckId) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("api_token", String.valueOf(Resource.getProperty("api_token")));
            map.put("api_verno", "1");
            // 补给所有舰船的类型是3
            map.put("api_kind", "3");
            // 要补给的舰船id，以逗号分隔
            Map deckMap = (Map) Resource.getProperty("decks");
            Map deckInfo = (Map) deckMap.get("deck_" + deckId);
            List shipIdList = (List) deckInfo.get("api_ship");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < shipIdList.size(); ++i) {
                sb.append(",").append((int) shipIdList.get(i));
            }
            sb.deleteCharAt(0);
            map.put("api_id_items", new String(sb));
            // 不知含义，先暂时固定为1，用于航母补给飞机？
            map.put("api_onslot", "1");
            return map;
        }

}
