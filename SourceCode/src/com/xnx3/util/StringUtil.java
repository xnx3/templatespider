package com.xnx3.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;

public class StringUtil {
	public static int count = 0;	//对比次数

	/**
	 * 文章相似度对比。越相似，越接近1
	 * @param str1
	 * @param str2
	 * @return 0~1
	 */
	public static double similarity(Element ele1, Element ele2){
		//首先进行tag的对比。当前标签tag一致，才有可能整个代码块相同
		String tag1 = ele1.tagName();
		String tag2 = ele2.tagName();
		if(tag1 != null && tag2 != null && tag1.equals(tag2)){
			//tag相同，进而对比其class、id
			
			//对比id
			if(ele1.attr("id").equals(ele2.attr("id"))){
				
				//对比class
				if(ele1.attr("class").equals(ele2.attr("class"))){
					return similarity_(ele1.toString(), ele2.toString());
				}
			}
		}
		return 0.1;
		
//		return similarity_(ele1.toString(), ele2.toString());
	}
	
	/**
	 * 文章相似度对比。越相似，越接近1
	 * @param str1
	 * @param str2
	 * @return 0~1
	 */
	public static double similarity_(String str1, String str2){
		str1 = purification(str1);
		str2 = purification(str2);
		if(str1.equals(str2)){
			return 1;
		}
		
		//进行长度比对，避免很长的字符串占用cpu
		int i1 = str1.length();
		int i2 = str2.length();
		double d = 0;	//结果在0~1，越接近1，则长度越相等
		if(i1 > i2){
			d = i2 / i1;
		}else{
			d = i1 / i2;
		}
		if(i1 < 200 && d > 0.85){
			//有希望相等
		}else if(i1 < 1000 && d > 0.92){
			//有那么点希望相等
		}else if(i1 < 3000 && d > 0.94){
			//有一丁点可能相等
		}else if(i1 < 10000 && d > 0.95){
			//相等很小，但有可能
		}else{
			//这里了直接判定就是不相等了，别浪费时间了，直接给0.2打发掉
			return 0.2;
		}
		
		count++;
		
//		if(Math.abs( / ) ){
//			
//		}
		try {
			return Computeclass.SimilarDegree(str1, str2);  
		} catch (Exception e) {
			return Computeclass.SimilarDegree(str2, str1);  
		}
	}
	
	/**
	 * 精华，将空格等字符去除，用于字符比对
	 * @param text
	 * @return
	 */
	public static String purification(String text){
		return text.replaceAll("\\s*", "");
	}
	
	/**
	 * 判断传入的字符串是否全部都是汉字、英文、数字。如果有某个字符不是，那么都会返回false
	 * @param str 要判断的字符串
	 * @return true：字符串的字符全部都在汉字、英文、数字中
	 */
	public static boolean isChinese(String str){
		return str.matches("[\\u4e00-\\u9fa5A-Za-z0-9]+");
	}
	
	public static void main(String[] args) {
		String a = "url(/image/default/1.png?v=201711250607";
		System.out.println(getFileNameByUrl(a));
	}
	

	/**
	 * 正则替换
	 * @param text 操作的内容源，主体
	 * @param regex 替换掉的
	 * @param replacement 替换成新的，取而代之的
	 * @return 提花好的内容
	 */
	public static String replaceAll(String text, String regex, String replacement){
		String s[] = {"?","(",")"}; 
		for (int i = 0; i < s.length; i++) {
			regex = regex.replaceAll("\\"+s[i], "\\\\"+s[i]);
		}
		text = text.replaceAll(regex, replacement);
		
		return text;
	}
	
	
	/**
	 * 替换掉路径的层级，即替换掉前缀的../  、  ./  、 /
	 * @param path 绝对路径前缀，如 http://www.wang.market/a/
	 * @param originalUrl 具体文件，如 ../js/func.js
	 */
	public static String hierarchyReplace(String path, String originalUrl){
		if(isAbsoluteUrl(originalUrl)){
			return originalUrl;
		}
		
		//提取出path中的域名
		String domain = "";		//如  http://wang.market   
		String s = path.substring(9, path.length());
		int domianInt = s.indexOf("/");
		if(domianInt > 0){
			domain = path.substring(0, domianInt+9);
		}
		
		//判断前缀路径末尾是否有/，如没有，补上
		if(path.lastIndexOf("/") != path.length()-1){
			path = path + "/";
		}
		
		//如果是跟路径引用，直接返回组合网址
		if(originalUrl.indexOf("/") == 0){
			return domain+originalUrl;
		}
		
		while(originalUrl.indexOf("./") == 0 || originalUrl.indexOf("../") == 0){
			if(originalUrl.indexOf("./") == 0){
				//过滤前面的./
				originalUrl = originalUrl.substring(2, originalUrl.length());
			}else if (originalUrl.indexOf("../") == 0) {
				//过滤前面的../
				originalUrl = originalUrl.substring(3, originalUrl.length());
				
				//prefixUrl要向上一级  
				//去掉最后的/
				String p = path.substring(0, path.length()-1);
				path = path.substring(0, p.lastIndexOf("/")+1);
			}
		}
		
		return path+originalUrl;
	}
	
	/**
	 * 判断url是否是绝对路径网址
	 * @param url 要判断的url
	 * @return true:是绝对路径的
	 */
	public static boolean isAbsoluteUrl(String url){
		if(url.indexOf("http://") > -1 || url.indexOf("https://") > -1 || url.indexOf("//") > -1){
			return true;
		}
		return false;
	}
	
	/**
	 * 获取当前url地址所在的远程文件路径
	 * @param url 目标url地址，绝对路径，如 http://www.wscso.com/test/a.jsp
	 * @return url所在地址的路径，返回如 http://www.wscso.com/test/
	 */
	public static String getPathByUrl(String url){
		int last = url.lastIndexOf("/");
		if(last > 8){
			url = url.substring(0, last+1);
		}
		
		return url;
	}
	
	/**
	 * 根据url地址，获取其访问的文件名字
	 * @param url 目标url，如 http://wang.market/images/a.jpg?a=123
	 * @return 返回访问的文件名，如以上url返回： a.jpg
	 */
	public static String getFileNameByUrl(String url){
		int last = url.lastIndexOf("/");
		if(last > 8){
			url = url.substring(last+1, url.length());
			if(url.indexOf("?") > 0){
				url = url.substring(0, url.indexOf("?"));
			}
			if(url.indexOf("#") > 0){
				url = url.substring(0, url.indexOf("#"));
			}
			return url;
		}else{
			//不是正常的url地址
			return "";
		}
		
	}
	
	

	/**
	 * 根据url地址，获取其访问的域名。若么有发现，返回空字符
	 * @param url 目标url，如 http://wang.market/images/a.jpg
	 * @return 返回wang.market
	 */
	public static String getDomainByUrl(String url){
		int start = url.indexOf("//");
		if(start > 0){
			url = url.substring(start+2, url.length());
			
			int end = url.indexOf("/");
			if(end > 0){
				url = url.substring(0, end);
			}
			return url;
		}
		
		return "";
	}
	
	/**
	 * 从指定字符串中提取字母跟数字，将其组合成一个新字符串返回
	 * @param text 要提取的目的字符串
	 * @return 若没有，返回空字符串""
	 */
	public static String extractAlphabetAndNumber(String text){
		StringBuffer sb = new StringBuffer();
        String s = "\\d+.\\d+|\\w+";
        Pattern pattern=Pattern.compile(s);  
        Matcher ma=pattern.matcher(text);  
        while(ma.find()){  
        	sb.append(ma.group());
        }
		return sb.toString();
	}
}
