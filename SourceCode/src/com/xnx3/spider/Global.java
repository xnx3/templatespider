package com.xnx3.spider;

import com.xnx3.StringUtil;
import com.xnx3.SystemUtil;
import com.xnx3.spider.ui.MainUI;
import com.xnx3.swing.LogFrame;

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
			+ "\n2.url必须是绝对路径"
			+ "\n3.url的格式如  http://qiye1.wscso.com/index.html";
	
	public static int delayTime = 100;	//下载的时间间隔，mainUI界面的延迟时间，在点击启动按钮后会将ui界面的输入框中的值赋予此
	
	private static String localTemplatePath;
	public static String templateDomain;	//当前提取的网站模版的域名
	/**
	 * 获取当前应用的目录所在。返回如 /Users/apple/Desktop/MyEclipseWork/templaete/bin/wang.market/
	 */
	public static String getLocalTemplatePath(){
		if(localTemplatePath == null){
			try {
				localTemplatePath = Global.class.getResource("/").getPath();
			} catch (Exception e) {
				//如果jar包变成exe，那么会走cache，所以下面要判断是否为null
				e.printStackTrace();
			}
			
			if(localTemplatePath == null){
				localTemplatePath = SystemUtil.getCurrentDir()+"/";
			}
			if(localTemplatePath.indexOf("%") > -1){
				//判断路径中是否有URL编码，若有，进行转码为正常汉字
				localTemplatePath = StringUtil.urlToString(localTemplatePath);
			}
		}
		return localTemplatePath+templateDomain+"/";
	}
	
	public static void main(String[] args) {
		System.out.println(getLocalTemplatePath());
	}
	
	
	public static LogFrame logUI;
	/**
	 * 写出日志
	 * @param text
	 */
	public static void log(String text){
		if(logUI == null){
			logUI = new LogFrame();
			logUI.setTitle("运行日志");
		}
		System.out.println("log--"+text);
		logUI.appendLineForLastAndPositionLast(text);
	}
}
