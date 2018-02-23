package com.xnx3.spider.cache;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xnx3.BaseVO;
import com.xnx3.UrlUtil;
import com.xnx3.file.FileUtil;
import com.xnx3.net.HttpResponse;
import com.xnx3.net.HttpUtil;
import com.xnx3.spider.vo.ResourceVO;
import com.xnx3.template.Global;
import com.xnx3.template.ResourceQuote;
import com.xnx3.template.bean.Template;
import com.xnx3.util.StringUtil;

/**
 * 爬取抓取某个指定的页面
 * @author 管雷鸣
 */
public class PageSpider {
	private String url;	//要抓取的页面url
	private Document doc;
	private static final String CACHE_STRING = "_XNX3CACHE_";	//缓存特殊字符。只要缓存过的文件，替换时会加上这个。以免进行多次缓存
	
	public PageSpider(String url) {
		if(url == null){
			return;
		}
		this.url = url;
		com.xnx3.spider.Global.log("开始抓取："+url);
		try {
			doc = Jsoup.connect(this.url).get();
			
			String html = replaceResourceQuoteForHtml();
			//当前html文件名字
			String htmlName = StringUtil.getFileNameByUrl(url);
			if(htmlName.length() == 0){
				htmlName = "index_"+StringUtil.extractAlphabetAndNumber(url);
			}
			
			//判断当前是否有后缀，同一将其变为html后缀
			String beforeName = UrlUtil.getFileBeforeName(htmlName);
			//将此变为html后缀保存
			htmlName = beforeName+".html";
			
			try {
				FileUtil.write(com.xnx3.spider.Global.getLocalTemplatePath()+htmlName, html, "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			Global.log(e.getMessage()+" -- "+url);
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
				Resource res = new Resource(url);
				
				//判断是否缓存过
				if(Cache.cacheMap.get(res.getNetUrl()) == null){
					//未缓存过，那就进行缓存
					HttpUtil http = new HttpUtil("UTF-8");
					HttpResponse hr = http.get(res.getNetUrl());
					if(hr.getContent() != null){
						String cssText = http.get(res.getNetUrl()).getContent();
						Cache.cacheMap.put(res.getNetUrl(), res);
						cssText = replaceCss(cssText, StringUtil.getPathByUrl(res.getNetUrl()), "../images/");
						try {
							FileUtil.write(res.getLocalUrl(), cssText, "UTF-8");
						} catch (IOException e) {
							System.out.println(res.getLocalUrl()+"--error");
							e.printStackTrace();
						}
					}else{
						try {
							FileUtil.write(res.getLocalUrl(), "/* not find url */", "UTF-8");
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
				Resource res = new Resource(url);
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
				Resource res = new Resource(url);
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
		String uriPath = StringUtil.getPathByUrl(thisUrl);	//当前的url的路径
		
//		Pattern pattern = Pattern.compile("background[-image]*: *url\\('?\"?(.*?)'?\"?\\)");
		Pattern pattern = Pattern.compile("url\\('?\"?(.*?)'?\"?\\)");
		Matcher matcher = pattern.matcher(cssText);
		while (matcher.find()) {
			String src = matcher.group(1);	//src的地址
			if(src != null && src.length() > 2){
				String srcUrl = StringUtil.hierarchyReplace(uriPath, src);
				if(srcUrl.indexOf(CACHE_STRING) == -1){
					//如果没有缓存过，那才进行缓存
					Resource res = new Resource(srcUrl);
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

	
}
