package com.xnx3.spider.cache;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections.functors.ForClosure;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.xnx3.BaseVO;
import com.xnx3.Lang;
import com.xnx3.UrlUtil;
import com.xnx3.file.FileUtil;
import com.xnx3.spider.Global;
import com.xnx3.spider.bean.RequestParamBean;
import com.xnx3.spider.vo.ResourceVO;
import com.xnx3.util.StringUtil;

/**
 * 爬取抓取某个指定的页面
 * @author 管雷鸣
 */
public class PageSpider {
	private String url;	//要抓取的页面url
	private Document doc;
	private static final String CACHE_STRING = "_XNX3CACHE_";	//缓存特殊字符。只要缓存过的文件，替换时会加上这个。以免进行多次缓存
	
	/**
	 * 界面中设定的一些参数
	 */
	public static RequestParamBean requestParamBean;
	/**
	 * 将界面上设置的request的参数加入内存
	 */
	public static void setRequestParamBean(){
		requestParamBean = new RequestParamBean();
		requestParamBean.setUserAgent(Global.mainUI.getUserAgent().getText());
		requestParamBean.setCookies(Global.mainUI.getTextArea_cooikes().getText());
		
		Global.delayTime = Lang.stringToInt(Global.mainUI.getDelayTimeTextField().getText(), 100);
		Global.log("下载延迟间隔: "+Global.delayTime+"ms");
	}
	
	public static void main(String[] args) {
//		PageSpider.setRequestHeadersCookies("PHPSESSID=d4sdrubbnu2902n8ep14552pi4; Hm_lvt_8432b140808b28edfb25dc2e4d795a1b=1541922897; nb-referrer-hostname=www.xingyucjb.com; nb-start-page-url=http%3A%2F%2Fwww.xingyucjb.com%2F; Hm_lpvt_8432b140808b28edfb25dc2e4d795a1b=1541923230");
//		PageSpider ps = new PageSpider("http://www.xingyucjb.com/");
		
	}
	
	//请求页面带过去的 cookies
//	private static Map<String, String> cookiesMap = null;
	/**
	 * 设置抓取时附带的cookies，同时也是清除上一次运行时所全局缓存的cookies
	 * @param cookies textarea中设置的文本内容
	 */
//	public static void setRequestHeadersCookies(String cookies){
//		if(cookiesMap == null){
//			cookiesMap = new HashMap<String, String>();
//		}
//		cookiesMap.clear();//将上一次运行的cookies清空，如果本次运行设置了cookie，那么就设置上。如果没有设置，那么就让里面没有值好了
//		
//		if(cookies != null && cookies.length() > 1){
//			String cs[] = cookies.split(";");
//			for (int i = 0; i < cs.length; i++) {
//				String c = cs[i].trim();
//				if(c.length() > 0 || c.indexOf("=") > 0){
//					//成功，是cookie
//					String[] kvs = c.split("=");
//					if(kvs.length == 2){
//						String key = kvs[0].trim();
//						String value = kvs[1].trim();
//						if(key.length() > 0){
//							cookiesMap.put(key, value);
//						}
//					}
//				}
//			}
//			
//			Global.log("Cookies : ");
//			for(Map.Entry<String,String > entry:cookiesMap.entrySet()){
//				Global.log(entry.getKey() + "=" + entry.getValue());
//			}
//		}
//	}
	
	//请求页面带过去的 user-agent
//	private static String userAgent = "";
	/**
	 * 请求页面带过去的 user-agent
	 * @param requestUserAgent 这里便是请求时要带的  user-agent
	 */
//	public static void setRequestHeadersUserAgent(String requestUserAgent){
//		userAgent = requestUserAgent;
//	}
	
	//全局缓存的扒取网站的页面编码，由界面设定，点击开始提取时，缓存到此处.默认是utf-8
	public static String encode = "UTF-8";
	
	
	
	public PageSpider(String url) {
		if(url == null){
			return;
		}
		this.url = url;
		com.xnx3.spider.Global.log("开始抓取："+url);
		try {
//			doc = Jsoup.connect(this.url).cookies(cookiesMap).userAgent(userAgent).get();
			
			Map<String, String> headersMap = new HashMap<String, String>();
			if(requestParamBean.getCookies() != null && requestParamBean.getCookies().length() > 2){
				headersMap.put("Cookie", requestParamBean.getCookies());
			}
			if(requestParamBean.getUserAgent() != null && requestParamBean.getUserAgent().length() > 2){
				headersMap.put("User-Agent", requestParamBean.getUserAgent());
			}
			
			doc = Jsoup.connect(this.url).headers(headersMap).get();
			String en = getCharset(doc);
			if(en != null && en.equalsIgnoreCase("UTF-8")){
				encode = "UTF-8";
			}else{
				encode = en; 
			}
			com.xnx3.spider.Global.log("自动提取页面编码："+encode);
			
			String html = replaceResourceQuoteForHtml();
			//当前html文件名字
			String htmlName = StringUtil.getFileNameByUrl(url);
			if(htmlName.length() == 0){
				htmlName = "index_"+StringUtil.extractAlphabetAndNumber(url);
			}
			
			//判断当前是否有后缀，同一将其变为html后缀
			String beforeName = UrlUtil.getFileBeforeName(htmlName);
			
			//判断其中是否有 ？的动态传递参数。若有参数，还要将参数也提取出来
			String dynamicParam = "";	//动态参数。如果url中没有get传递的参数，则此处为空字符串即可
			if(url.indexOf("?") > 0){
				//有动态参数，那么，动态参数要加UUID
				dynamicParam = Lang.uuid();
			}
			
			//将此变为html后缀的页面进行保存
			htmlName = beforeName+ (dynamicParam.length() > 0 ? "__"+dynamicParam:"") +".html";
			htmlName = htmlName.replaceAll("/", "");
			
			FileUtil.write(com.xnx3.spider.Global.getLocalTemplatePath()+htmlName, html, encode);
		} catch (IOException e) {
			com.xnx3.spider.Global.log(e.getMessage()+" -- "+url);
			e.printStackTrace();
		}
		
		
	}

	/**
	 * 针对html文件，替换其源代码，将资源引用的相对路径改为绝对路径
	 * @throws IOException 
	 */
	public String replaceResourceQuoteForHtml(){
		//取CSS
		Elements cssEles = doc.getElementsByTag("link");
		for (int i = 0; i < cssEles.size(); i++) {
			Element ele = cssEles.get(i);
			String url = ele.attr("abs:href");
			if(url.length() > 3){
				//找到地址了，将其下载
				Resource res = new Resource(url, this.url, "");
				if(res.getNetUrl() == null){
					continue;
				}
				//判断是否缓存过
				if(Cache.cacheMap.get(res.getNetUrl()) == null){
					//未缓存过，那就进行缓存
					String cssText = com.xnx3.util.HttpUtil.getContent(res.getNetUrl());
					if(cssText != null){
						Cache.cacheMap.put(res.getNetUrl(), res);
						cssText = replaceCss(cssText, StringUtil.getPathByUrl(res.getNetUrl()), "../images/");
						try {
							FileUtil.write(res.getLocalUrl(), cssText, PageSpider.encode);
						} catch (IOException e) {
							com.xnx3.spider.Global.log("cache -- "+res.getLocalUrl()+"--error");
							e.printStackTrace();
						}
					}else{
						try {
							FileUtil.write(res.getLocalUrl(), "/* not find url */", PageSpider.encode);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}else{
					res = Cache.cacheMap.get(res.getNetUrl());
				}
				
				//替换css文件路径
				ele.attr("href", "css/"+res.getLocalFile());
			}
		}
		
		
		//取JS
		Elements jsEles = doc.getElementsByTag("script");
		for (int i = 0; i < jsEles.size(); i++) {
			Element ele = jsEles.get(i);
			String url = ele.attr("abs:src");
			if(url.length() > 3){
				//找到地址了，将其下载
				Resource res = new Resource(url, this.url, "");
				ResourceVO vo = Cache.addCache(res);
				if(vo.getResult() - BaseVO.SUCCESS == 0){
					//已经缓存过了，那么替换标签
					ele.attr("src", vo.getResource().getLocalRelativePath()+vo.getResource().getLocalFile());
				}
			}
		}
		
		
		//取img
		Elements imgEles = doc.getElementsByTag("img");
		for (int i = 0; i < imgEles.size(); i++) {
			Element ele = imgEles.get(i);
			String url = ele.attr("abs:src");
			if(url.length() > 3){
				//找到地址了，将其下载
				Resource res = new Resource(url, this.url, "");
				if(res.getNetUrl() == null){
					continue;
				}
				ResourceVO vo = Cache.addCache(res);
				if(vo.getResult() - BaseVO.SUCCESS == 0){
					//已经缓存过了，那么替换标签
					ele.attr("src", vo.getResource().getLocalRelativePath()+vo.getResource().getLocalFile());
				}
			}
		}
		
		
		//去掉base标签
		Elements baseEles = doc.getElementsByTag("base");
		if(baseEles != null && baseEles.size() > 0){
			for (int i = 0; i < baseEles.size(); i++) {
				baseEles.get(i).remove();
			}
		}
		
		//替换网页本身内写的css相关引用
		String html = replaceCss(doc.toString(), doc.baseUri(), "images/");
		
//		String html = rq.htmlFilter(doc.toString(), "src=\"(.*?)\"");
//		html = rq.htmlFilter(html, "background-image: *url\\('*(.*?)'*\\)");
//		
		
		return html;
	}
	
	
	/**
	 * 将css文件进行过滤替换，下载其中的图片
	 * @param text css的内容
	 * @param thisUrl 当前文件在网上的绝对路径
	 * @param cacheFilePath 缓存下来的文件，在text中引用的路径（缓存路径）。如/css/...css文件的引用为 ../images/   /..html文件引用为 "images/"
	 * @return 
	 */
	public static String replaceCss(String cssText, String thisUrl, String cacheFilePath){
		String uriPath = UrlUtil.getPath(thisUrl);	//当前的url的路径
		
//		Pattern pattern = Pattern.compile("background[-image]*: *url\\('?\"?(.*?)'?\"?\\)");
		Pattern pattern = Pattern.compile("url\\('?\"?(.*?)'?\"?\\)");
		Matcher matcher = pattern.matcher(cssText);
		while (matcher.find()) {
			String src = matcher.group(1);	//src的地址
			if(src != null && src.length() > 2){
				if(src.trim().indexOf("data:image/") > -1){
					//是 data:image/svg+xml; 这种的，那么就不用下载了
					continue;
				}
				
				String srcUrl = StringUtil.hierarchyReplace(uriPath, src);
				if(srcUrl.indexOf(CACHE_STRING) == -1){
					//如果没有缓存过，那才进行缓存
					Resource res = new Resource(srcUrl, thisUrl, "");
					if(res.getNetUrl() == null){
						continue;
					}
					ResourceVO vo = Cache.addCache(res);
					if(vo.getResult() - BaseVO.SUCCESS == 0){
						//将其进行替换为相对路径
						cssText = StringUtil.replaceAll(cssText, src, CACHE_STRING+cacheFilePath+vo.getResource().getLocalFile());
					}
				}
			}
		}
		//完事后，将 _XNX3CACHE_ 去除
		cssText = StringUtil.replaceAll(cssText, CACHE_STRING, "");
		
		return cssText;
	}


	/**
	 * 若使用的utf-8编码，则统一返回 “UTF-8” ，若是其他编码，则会返回charset中设置的编码
	 * @param doc
	 * @return
	 */
	public static String getCharset(Document doc){
		String defaultEncode = "UTF-8";
		
		Elements es = doc.getElementsByTag("meta");
		for (int i = 0; i < es.size(); i++) {
			Element ele = es.get(i);
			String equiv = ele.attr("http-equiv");
			if(equiv != null && equiv.equalsIgnoreCase("content-type")){
				String content = ele.attr("content");
				if(content == null){
					return defaultEncode;
				}
				String cs[] = content.split(";");
				for (int j = 0; j < cs.length; j++) {
					String kvs[] = cs[j].split("=");
					if(kvs[0].trim().equalsIgnoreCase("charset")){
						return kvs[1].trim();
					}
				}
			}
		}
		return defaultEncode;
	}
}
