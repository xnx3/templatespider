package com.xnx3.util;

import com.xnx3.UrlUtil;
import com.xnx3.net.HttpResponse;
import com.xnx3.net.HttpsUtil;
import com.xnx3.spider.cache.PageSpider;

/**
 * http、https请求
 * @author 管雷鸣
 */
public class HttpUtil {
	
	/**
	 * 获取网页源代码，自动根据url的协议获取
	 * @param url 绝对路径
	 * @return 若失败返回null，成功返回源代码
	 */
	public static String getContent(String url){
		if(url == null){
			return null;
		}
		
		//获取协议，是http，还是https
		String protocol = UrlUtil.getProtocols(url);
		if(protocol == null){
			protocol = "http";
		}
		HttpResponse hr = null;
		if(protocol.equalsIgnoreCase("https")){
			HttpsUtil https = new HttpsUtil(PageSpider.encode);
			hr = https.get(url);
		}else{
			//只要不是https的，这里暂时都认为是http
			com.xnx3.net.HttpUtil http = new com.xnx3.net.HttpUtil(PageSpider.encode);
			hr = http.get(url);
		}
		
		if(hr == null || hr.getContent() == null){
			return null;
		}else{
			return hr.getContent();
		}
	}
	
}
