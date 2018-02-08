package com.xnx3.template;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.xnx3.util.StringUtil;

/**
 * 资源引用相关的处理，如，将资源引用的相对路径换为绝对路径
 * @author 管雷鸣
 */
public class ResourceQuote {
	private String baseUri = null;
	
	public ResourceQuote(String baseUri) {
		this.baseUri = baseUri;
	}
	
	public static void main(String[] args) {
//		Action.loadLocalTemplate();
		Document doc = Global.templateMap.get("about1.html").getDoc();
		
		ResourceQuote rq = new ResourceQuote("http://www.xnx3.com/res");
		doc = rq.tagReplace(doc, "img", "src");
		doc = rq.tagReplace(doc, "link", "href");
		doc = rq.tagReplace(doc, "script", "src");
		
		String html = rq.htmlFilter(doc.toString(), "src=\"(.*?)\"");
		System.out.println("之前："+html);
		html = rq.htmlFilter(html, "background-image: *url\\('*(.*?)'*\\)");
		System.out.println("之后："+html);
		System.out.println(html);
	}
	
	/**
	 * 替换 img 标签
	 * @param doc
	 * @return
	 */
	public Document imgTag(Document doc){
		Elements imgElements = doc.getElementsByTag("img");
		for (int i = 0; i < imgElements.size(); i++) {
			Element e = imgElements.get(i);
			String url = e.attr("src");
			String absUrl = hierarchyReplace(this.baseUri, url);
			if(!url.equals(absUrl)){
				e.attr("src", absUrl);
			}
		}
		return doc;
	}
	
	/**
	 * Tag标签的引用资源替换，替换为绝对路径
	 * @param doc Document，整个页面
	 * @param tagName tag的名字，如 img、   script
	 * @param tagProperty 上面的tag中资源引用的标签，如 src
	 * @return 替换好的Document
	 */
	public Document tagReplace(Document doc, String tagName, String tagProperty){
		Elements imgElements = doc.getElementsByTag(tagName);
		for (int i = 0; i < imgElements.size(); i++) {
			Element e = imgElements.get(i);
			String url = e.attr(tagProperty);
			if(url.length() > 3 && url.indexOf(baseUri) == -1){
				String absUrl = hierarchyReplace(this.baseUri, url);
				if((!url.equals(absUrl)) && url.indexOf("://") == -1){
					//如果url未替换过，且不是绝对路径，那么进行替换操作
					e.attr(tagProperty, absUrl);
				}
			}
		}
		return doc;
	}
	
	
	
	
	/**
	 * 替换 src="" 中间的网址
	 * @param html html页面源代码
	 * @param regex 如 src=\"(.*?)\"
	 * @return 
	 */
	public String htmlFilter(String html, String regex){
		//将找到的要替换的网址存入map。起排重的作用。可能一个网页中会有多个。此会在 htmlFilter 中进行提取。多个提取完毕后，才会统一进行替换
		Map<String, String> map = new HashMap<String, String>();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);
		while (matcher.find()) {
			String src = matcher.group(1);	//src的地址
			if(!isAbsoluteUrl(src) && src.length() > 3 && map.get(src) == null && src.indexOf(baseUri) == -1){
				//只要不是绝对路径的，都要替换为绝对路径
				map.put(src, "1");
			}
		}
		
		//将上面正则找到的网址，进行替换
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String src = entry.getKey();
			//replace 加上前后标示，防止同一个图片出现多次，会多次匹配赋值 的情况
			html = StringUtil.replaceAll(html, regex.replace("(.*?)", src), hierarchyReplace(this.baseUri, src));
		}
		return html;
	}

	/**
	 * 判断url是否是绝对路径网址
	 * @param url 要判断的url
	 * @return true:是绝对路径的
	 */
	public static boolean isAbsoluteUrl(String url){
		if(url.indexOf("http://") > -1 || url.indexOf("https://") > -1 || url.indexOf("//") > -1){
			return true;
		}
		return false;
	}
	
	/**
	 * 替换掉路径的层级，即替换掉前缀的../  、  ./  、 /
	 * @param prefixUrl 绝对路径前缀，如 http://www.wang.market/a/
	 */
	public static String hierarchyReplace(String prefixUrl, String originalUrl){
		if(isAbsoluteUrl(originalUrl)){
			return originalUrl;
		}
		
		//判断前缀路径末尾是否有/，如没有，补上
		if(prefixUrl.lastIndexOf("/") != prefixUrl.length()){
			prefixUrl = prefixUrl + "/";
		}
		
		//如果是跟路径引用，暂时直接返回组合网址
		if(originalUrl.indexOf("/") == 0){
			System.out.println("如果是跟路径引用，暂时直接返回组合网址--"+originalUrl);
			return prefixUrl+originalUrl.substring(1, originalUrl.length());
		}
		
		
		while(originalUrl.indexOf("./") == 0 || originalUrl.indexOf("../") == 0){
			if(originalUrl.indexOf("./") == 0){
				//过滤前面的./
				originalUrl = originalUrl.substring(2, originalUrl.length());
			}else if (originalUrl.indexOf("../") == 0) {
				//过滤前面的./
				originalUrl = originalUrl.substring(3, originalUrl.length());
				//prefixUrl要向上一级  
				prefixUrl = prefixUrl.substring(0, prefixUrl.substring(0, prefixUrl.length()-1).lastIndexOf("/")+1);
			}
		}
		
		return prefixUrl+originalUrl;
	}
	
}
