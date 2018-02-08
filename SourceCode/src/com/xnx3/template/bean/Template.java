package com.xnx3.template.bean;

import java.io.File;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * 某个模板
 * @author 管雷鸣
 *
 */
public class Template {
	private File file;
	private Document doc;			//整个html模板的doc
	private Element bodyElement;	//模板页面的body
	
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public Document getDoc() {
		return doc;
	}
	public void setDoc(Document doc) {
		this.doc = doc;
	}
	public Element getBodyElement() {
		if(bodyElement == null){
			this.bodyElement = doc.getElementsByTag("body").first();
		}
		return bodyElement;
	}
	public void setBodyElement(Element bodyElement) {
		this.bodyElement = bodyElement;
	}
	
}
