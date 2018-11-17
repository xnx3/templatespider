package com.xnx3.spider.bean;

/**
 * 界面中自定义设置的 request 参数
 * @author 管雷鸣
 *
 */
public class RequestParamBean {
	
	private String cookies;
	private String userAgent;
	
	
	public String getCookies() {
		return cookies;
	}
	public void setCookies(String cookies) {
		this.cookies = cookies;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	
	
}
