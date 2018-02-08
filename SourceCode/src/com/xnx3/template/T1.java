package com.xnx3.template;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class T1 {
	public static void main(String[] args) throws IOException {
		String a = "haha <p>2</p>";
		Document doc = Jsoup.parse(a);
		Elements eles = doc.body().children();
		System.out.println(doc.body());
		System.out.println(doc.body().html());
		
		for (int i = 0; i < eles.size(); i++) {
			Element ele = eles.get(i);
			System.out.println(ele.toString());
		}
		
		
    }
}  