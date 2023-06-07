package cc.fxqq.hippo.test;

import java.io.FileWriter;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

public class HttpHostTest {

	@Test
	public void test() throws Exception {
		
		HttpClient httpClient = HttpClients.createDefault();
	    HttpGet httpGet = new HttpGet("http://www.dailyfxasia.com/weekly_strategy/20220929-768.html");
	    
		//使用代理服务器
	    HttpHost httpHost = new HttpHost("127.0.0.1",19180);
	    RequestConfig config = RequestConfig.custom().setProxy(httpHost).build();
	    httpGet.setConfig(config);
	    CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpGet);
	    HttpEntity entity = response.getEntity();
	    //输出网页内容
	    System.out.println("网页内容:");
	    String content = EntityUtils.toString(entity,"utf-8");
	    //System.out.println(content);
	    response.close();

	    FileWriter fw = new FileWriter("test.html");
	    fw.write(StringEscapeUtils.unescapeHtml4(content));
	    fw.flush();
	    fw.close();
//		HttpHost entry = new HttpHost("127.0.0.1", 19180);
//        String query = Executor.newInstance()
//            //.auth(entry, "crnr98", "ae44equ3")
//            .execute(Request.Get("https://www.fxstreet.cn/analysis/").viaProxy(entry))
//            .returnContent().asString();
//        System.out.println(query);
	    
	    
	}
}
