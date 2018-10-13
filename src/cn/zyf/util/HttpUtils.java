package cn.zyf.util;

import cn.zyf.resource.Resource;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.Map;

public interface HttpUtils {

    public static final CloseableHttpClient client = HttpClients.createDefault();

    public static final String server = (String) Resource.getProperty("server_ip");

    public static final String cookiePrefix = "UM_distinctid=16637fa36f943c-0c90039504ab82-7b113d-144000-16637fa36fa1cc; CNZZDATA5787084=cnzz_eid%3D1323979881-1538533183-null%26ntime%3D";

    public static final String refererPrefix = server + "/kcs2/index.php?api_root=/kcsapi&voice_root=/kcs/sound&osapi_root=osapi.dmm.com&version=" + Resource.getProperty("kancolle_version");

    public void setDefaultHeader();

    public void addHeader(Map<String, String> map);

    public void addPostParameter(Map<String, String> map);

    public HttpResponse execute();

}
