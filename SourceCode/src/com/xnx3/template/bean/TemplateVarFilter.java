package com.xnx3.template.bean;

import org.jsoup.nodes.Element;

/**
 * 模板变量的筛选，将每个模板页面中都有的模板变量筛选出来
 */
public class TemplateVarFilter {
	
	private String key;		//查找的元素，即对element进行精简，去除空格等
	private Element element; //元素本身
	private double simSum;	//相似度的和
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public double getSimSum() {
		return simSum;
	}
	public void setSimSum(double simSum) {
		this.simSum = simSum;
	}
	public Element getElement() {
		return element;
	}
	public void setElement(Element element) {
		this.element = element;
	}
	
}
