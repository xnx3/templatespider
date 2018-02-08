package com.xnx3.template;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xnx3.file.FileUtil;

public class doc {
	
	public static void main(String[] args) {
		
		Document doc = Jsoup.parse(FileUtil.read("G:/MyEclipseWorkSpace/templaete/template/about.html", FileUtil.UTF8));
		Element body = doc.getElementsByTag("body").first();
//		System.out.println(body);
		
		Elements e = body.children();
		for (int i = 0; i < e.size(); i++) {
			System.out.println("---------------"+e.get(i).tagName());
			System.out.println(e.get(i));
		}
		
	}
	
}
