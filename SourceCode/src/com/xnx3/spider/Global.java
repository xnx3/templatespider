package com.xnx3.spider;

import com.xnx3.spider.ui.MainUI;
import com.xnx3.ui.Log;

/**
 * 数据缓存
 * @author 管雷鸣
 */
public class Global {
	public static MainUI mainUI;
	static{
		mainUI = new MainUI();
	}
	
	/**
	 * 软件打开后，自动将此文字赋予URL文本框内
	 */
	public static final String TEXTAREA_REMIND = "请将要抓取的目标url地址(网址)复制到此处。"
			+ "\n注意事项："
			+ "\n1.如果有多个url，每个url一行"
			+ "\n2.url必须是绝对路径";
	
	
	private static String localTemplatePath;
	public static String templateDomain;	//当前提取的网站模版的域名
	/**
	 * 获取当前应用的目录所在。返回如 /Users/apple/Desktop/MyEclipseWork/templaete/bin/wang.market/
	 */
	public static String getLocalTemplatePath(){
		if(localTemplatePath == null){
			localTemplatePath = Global.class.getResource("/").getPath();
		}
		return localTemplatePath+templateDomain+"/";
	}
	
	public static void main(String[] args) {
		System.out.println(getLocalTemplatePath());
	}
	
	
	public static Log logUI;
	static{
		if(logUI == null){
			logUI = new Log();
			logUI.setTitle("运行日志");
		}
	}
	/**
	 * 写出日志
	 * @param text
	 */
	public static void log(String text){
		System.out.println("log--"+text);
		logUI.appendLineForLastAndPositionLast(text);
	}
}
