package com.xnx3.template;

import java.util.HashMap;
import java.util.Map;

import com.xnx3.template.bean.ElementDiffRecord;
import com.xnx3.template.bean.Template;
import com.xnx3.template.ui.Main;
import com.xnx3.template.ui.TemplateVarGainJframe;

public class Global {
	public final static String VERSION = "3.5";	//对应wang.market云建站的版本
	public static double sim = 0.99;	//搜索9成以上相似度的进行提取模版变量
	//存储取得的模版变量的结果
	public static Map<String, ElementDiffRecord> templateVarMap;
	//当前存储的本地的模版页面，将这些模版页面进行计算，提取出模版变量
	public static Map<String, Template> templateMap;
	
	//主界面
	public static Main mainUI;
	//模版变量的高级设置
	public static TemplateVarGainJframe templateVarGainJframe;
	
	static{
		mainUI = new Main();
		templateVarGainJframe = new TemplateVarGainJframe();
		
		templateVarMap = new HashMap<String, ElementDiffRecord>();
		templateMap = new HashMap<String, Template>();
	}
	
	public static void log(String text){
		System.out.println(text);
		mainUI.getLblNewLabel_log().setText(text);
	}
	
	//使用 getAppPath() 来获取
	public static String path = null; 
	/**
	 * 取得当前应用的绝对路径
	 */
	public static String getAppPath(){
		if(path == null){
			String p = TemplateCompute.class.getResource("/").getPath();
			if(p.indexOf("/bin/") > 0){
				p = p.replace("/bin/", "/");
			}
			path = p;
		}
		
		return path;
	}
	
	
}
