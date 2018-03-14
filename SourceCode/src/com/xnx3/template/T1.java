package com.xnx3.template;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xnx3.StringUtil;
import com.xnx3.UrlUtil;


public class T1 {
	public static void main(String[] args) throws IOException {
		
		Document doc = Jsoup.connect("http://www.mycodes.net/49/").get();
//			Document doc = Jsoup.connect("http://www.oschina.net/").get();
		
		System.out.println(getCharset(doc));
		
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