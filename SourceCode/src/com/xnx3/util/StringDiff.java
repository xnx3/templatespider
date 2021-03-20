package com.xnx3.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xnx3.DateUtil;
import com.xnx3.Lang;
import com.xnx3.StringUtil;
import com.xnx3.SystemUtil;
import com.xnx3.file.FileUtil;
import com.xnx3.template.Global;
import com.xnx3.template.TemplateCompute;
import com.xnx3.template.ui.Diff;
import com.xnx3.template.ui.diffJeditorPanel;
import com.xnx3.util.stringDiff.ShortStringTrait;


public class StringDiff {
	//默认的diff html
	public final static String DEFAULT_DIFF_HTML = "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"></head><body><div style=\"float:left; width:49%;\"><div style=\"font-size:30px; font-weight: bold;\">{DIFF1_TITLE}</div><hr/><div class=\"code\">{DIFF1_TEXT}</div></div><div style=\"float:left; width:0.5%; background-color: black; height:100%;\">&nbsp;</div><div style=\"float:left; width:49%;\"><div style=\"font-size:30px; font-weight: bold;\">{DIFF2_TITLE}</div><hr/><div class=\"code\">{DIFF2_TEXT}</div></div></body></html>";
	
	private int maxLength = 10;	//只要字符串长度小于这个数字，都会进行二分
	
	public String duan;		//存储较短的字符串
	public String chang;		//存储较长的字符串
	private Map<String, String> duanMap;		//较短的字符串集合
	
	private Map<Integer, String> equalMap;	//相等的字符串集合。key:数字编号，即generateIntKey()    value:替换掉的相同的字符串
	private Map<String, String> notFindDuanStrMap;	//未发现的短字符串map。当字符串的长度小于maxLength且匹配了一次未发现匹配项后，都会在此记录。避免一直匹配且找不到造成死循环
	
	private int i;		//自动编号，i++递增。必须通过 generateIntKey()获取
	
	private boolean strChange;	//是否进行了字符串前后位置更换. true：是，进行了更换
	
	/**
	 * 是否进行了字符串前后位置更换. 
	 * @return true：是，进行了更换
	 */
	public boolean isStrChange() {
		return strChange;
	}

	public static void main(String[] args) {
		String a = FileUtil.read(Global.getAppPath()+"template/about2.html");
		String b = FileUtil.read(Global.getAppPath()+"template/about1.html");
		StringDiff d = new StringDiff(a, b);
		
		try {
			FileUtil.write(StringDiff.class.getResource("/").getPath()+"preview.html", d.previewDiffHtml(), "UTF-8");
			SystemUtil.openUrl("file://"+StringDiff.class.getResource("/").getPath()+"preview.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public StringDiff(String str1, String str2) {
		//将字符串判断，找出长的、短的字符串
		String d = "";
		if(str1.length() > str2.length()){
			chang = str1;
			d = str2;
			this.strChange = false;
		}else{
			d = str1;
			chang = str2;
			this.strChange = true;
		}
		
		duan = d;
		duanMap = new HashMap<String, String>();
		duanMap.put(d, "1");
		
		equalMap = new HashMap<Integer, String>();
		notFindDuanStrMap = new HashMap<String, String>();
		
		
		/** 进行对比，diff核心 ***/
		boolean duanSplit = dichotomy();
		while(duanSplit){
			duanSplit = dichotomy();
			System.out.println(duanMap.size());
		}
		/*** 一轮比对完成，进行遗漏短词匹配，长度<maxLength的匹配 ***/
		
		/**** 二轮匹配，将一轮遗留的短字符，加入前后的{xnx3=123}，重新组合特征串进行匹配。匹配时，长、短字符串分别找出目前存在的不匹配的字符，组合好特征串，过滤空字符后进行对比 ****/
		Pattern p = Pattern.compile("([0-9]*)\\}([\\s\\S]*?)\\{xnx3");
		
		//获取短字符特征串
		List<ShortStringTrait> duanList = new ArrayList<ShortStringTrait>();
        Matcher m = p.matcher(this.duan);
        while (m.find()) {
        	String g = m.group(2);	//中间的小字符
        	if(g.length() > 0){
        		ShortStringTrait sst = new ShortStringTrait();
        		sst.setBeforeVarKey(m.group(1));
        		sst.setOriginalText(g);
        		sst.setPurificationText(com.xnx3.util.StringUtil.purification(g));
        		duanList.add(sst);
        	}
        }
        
        //获取长字符特征串
        List<ShortStringTrait> changList = new ArrayList<ShortStringTrait>();
        Matcher mchang = p.matcher(this.chang);
    	while (mchang.find()) {
    		String g = mchang.group(2);	//中间的小字符
    		if(g.length() > 0){
          		ShortStringTrait sst = new ShortStringTrait();
          		sst.setBeforeVarKey(mchang.group(1));
          		sst.setOriginalText(g);
          		sst.setPurificationText(com.xnx3.util.StringUtil.purification(g));
          		changList.add(sst);
    		}
    	}
		
		//进行长短字符特征串的比对
    	//先遍历短特征字符串
    	for (int i = 0; i < duanList.size(); i++) {
    		ShortStringTrait duanSST = duanList.get(i);
    		//在遍历长特征字符
    		for (int j = 0; j < changList.size(); j++) {
    			ShortStringTrait changSST = changList.get(j);
    			if(groupStringTrait(duanSST.getBeforeVarKey(), duanSST.getPurificationText()).equals(groupStringTrait(changSST.getBeforeVarKey(), changSST.getPurificationText()))){
    				
    				int key = generateIntKey();
        			equalMap.put(key, duanSST.getOriginalText());
        			chang = chang.replace(groupStringTrait(changSST.getBeforeVarKey(), changSST.getOriginalText()) , groupStringTrait(changSST.getBeforeVarKey(), "{xnx3="+key+"}"));
        			duan = duan.replace(groupStringTrait(duanSST.getBeforeVarKey(), duanSST.getOriginalText()) , groupStringTrait(duanSST.getBeforeVarKey(), "{xnx3="+key+"}"));
    				System.out.println("ok--"+key+"-->"+duanSST.getPurificationText());
    				//跳出此短特征字符串的遍历，继续下一个
    				break;
    			}
    			
			}
		}
		
	}
	
	/**
	 * 组合特征字符串，第二步骤比对时使用
	 * @param beforeKey 比对的字符串前的特征xnx3=?
	 * @param text 要比对的字符
	 * @return {xnx3=beforeKey}text{xnx3
	 */
	public static String groupStringTrait(String beforeKey, String text){
		return "{xnx3="+beforeKey+"}"+text+"{xnx3";
	}
	
	public String previewChangDiff(){
		Map<Integer, String> map = new HashMap<Integer, String>();
		for (Map.Entry<Integer, String> entry : this.equalMap.entrySet()) {
			String html = entry.getValue().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			map.put(entry.getKey(), html);
		}
		
		Pattern pattern = Pattern.compile("\\{xnx3=([0-9]*)\\}");
		Matcher matcher = pattern.matcher(chang);
		String changStr = this.chang+" ";
		changStr = changStr.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		System.out.println("------changStr");
		System.out.println(changStr);
		while (matcher.find()) {
        	String m = matcher.group(1);	//模版变量的id
        	int key = Lang.stringToInt(m, -1);
        	String k = "{xnx3="+key+"}";
        	changStr = changStr.replace(k, "<font color=\"#dddddd;\">"+map.get(key)+"</font>");
		}
		return changStr;
	}
	
	public String previewDiffHtml(){
		String html = DEFAULT_DIFF_HTML.replace("{DIFF1_TEXT}", previewDuanDiff());
		html = html.replace("{DIFF2_TEXT}", previewChangDiff());
		
		return html;
	}
	
	/**
	 * 获取第一个字符串对比结果。在New创建此对象时，传入的第一个字符串
	 * @return
	 */
	public String getFirstStrDiff(){
		if(isStrChange()){
			return previewDuanDiff();
		}else{
			return previewChangDiff();
		}
	}
	
	/**
	 * 获取原始差异的对比，只输出不同的字符串
	 * @return
	 */
	public String getFirstStrOriginalDiff(){
		if(isStrChange()){
			return this.duan.replaceAll("\\{xnx3=([0-9]*)\\}", " ");
		}else{
			return this.chang.replaceAll("\\{xnx3=([0-9]*)\\}", " ");
		}
	}
	
	/**
	 * 获取原始差异的对比，只输出不同的字符串
	 * @return
	 */
	public String getTwoStrOriginalDiff(){
		if(isStrChange()){
			return this.chang.replaceAll("\\{xnx3=([0-9]*)\\}", " ");
		}else{
			return this.duan.replaceAll("\\{xnx3=([0-9]*)\\}", " ");
		}
	}
	
	/**
	 * 获取第二个字符串对比结果。在New创建此对象时，传入的第二个字符串
	 * @return
	 */
	public String getTwoStrDiff(){
		if(isStrChange()){
			return previewChangDiff();
		}else{
			return previewDuanDiff();
		}
	}
	
	
	public String previewDuanDiff(){
		Map<Integer, String> map = new HashMap<Integer, String>();
		for (Map.Entry<Integer, String> entry : this.equalMap.entrySet()) {
			String html = entry.getValue().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			map.put(entry.getKey(), html);
		}
		
		Pattern pattern = Pattern.compile("\\{xnx3=([0-9]*)\\}");
		Matcher matcher = pattern.matcher(duan);
		String duanStr = this.duan+" ";
		duanStr = duanStr.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		while (matcher.find()) {
        	String m = matcher.group(1);	//模版变量的id
        	int key = Lang.stringToInt(m, -1);
        	String k = "{xnx3="+key+"}";
        	duanStr = duanStr.replace(k, "<font color=\"#dddddd;\">"+map.get(key)+"</font>");
		}
		return duanStr;
	}
	
	/**
	 * 二分法，替换掉长字符串中相同的字符串
	 * @param duan
	 * @return 是否可继续进行拆分
	 */
	public synchronized boolean dichotomy(){
		boolean duanSplit = false;	 //duanMap中是否有可对比拆分的字符串。只要有一个，那都是true
		
		Map<String, String> map = new HashMap<String, String>();
		map.putAll(getDuanMap());
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String duanStr = entry.getKey();

			//先将小的字符串进行二分法拆分
			//判断小的字符串是否具备拆分条件
			if(duanStr.length() < maxLength){
				//System.out.println("不可拆分。已到最小："+duanStr);
			}else{
				//可拆分
				
				String sp[] = duanStr.split("\\{xnx3=([0-9]*)\\}"); 
				for (int i = 0; i < sp.length; i++) {
					String s = sp[i];
					System.out.println(s);
					if(s.length() > maxLength){
						//如果比最大值大，那么进行拆分
						boolean split = splitDiff(s);
						if(split){
							duanSplit = true;
						}
					}else if (s.length()*2 > maxLength) {
						//如果短字符串的两倍在最大拆分长度之内，那么可进行对比
						boolean b = findStr(s);
						if(b){
							duanSplit = true;
						}
					}else{
						//小于最小值的，这里考虑＋前{xnx3=}进行匹配
						System.out.println("－－－短－－"+s);
						
					}
					
				}
				
//				123
			}
		}
		
		return duanSplit;
	}
	
	/**
	 * 拆分对比
	 * @param duanStr 要进行二分法拆分的短字符串
	 * @return duanMap中是否有可对比拆分的字符串。只要有一个，那都是true
	 */
	public synchronized boolean splitDiff(String duanStr){
		int centerIndex = Math.round(duanStr.length()/2);
		String duan1 = duanStr.substring(0, centerIndex);
		String duan2 = duanStr.substring(centerIndex, duanStr.length());
		
		duanMap.remove(duanStr);	//移除短字符串中，拆分的
		
		boolean b1 = findStr(duan1); 
		boolean b2 = findStr(duan2);
		
		return b1 || b2;
	}
	
	/**
	 * 用一个短字符串，去长字符串中搜寻，看是否能搜寻到，并替换
	 * @param duanStr 拿来去长字符串中进行搜寻的短字符串
	 * @return duanMap中是否有可对比拆分的字符串。只要有一个，那都是true
	 */
	public synchronized boolean findStr(String duanStr){
		boolean duanSplit = false;
		
		if(chang.indexOf(duanStr) > -1){
			int key = generateIntKey();
			equalMap.put(key, duanStr);
			System.out.println("---find--"+key+"--"+duanStr);
			chang = chang.replace(duanStr, "{xnx3="+key+"}");
			duan = duan.replace(duanStr, "{xnx3="+key+"}");
		}else{
//			if(getDuanMap().get(duanStr) != null){
				getDuanMap().put(duanStr, "1");	//如果长字符串没有发现，那么要将这个拆分的短字符串加入map－未发现的短集合中
				if(duanStr.length() >= maxLength && notFindDuanStrMap.get(duanStr) == null){
//					System.out.println("==> "+duanStr);
					duanSplit = true;
					notFindDuanStrMap.put(duanStr, "1");
				}
//			}else{
//				//已经对比过一次了。这应该是在小于最小比对长度的。避免死循环，单独判断出来
//			}
		}
		return duanSplit;
	}
	
	/**
	 * 获取一个随机数
	 */
	public synchronized int generateIntKey(){
		return i++;
	}
	
	public synchronized Map<String, String> getDuanMap(){
		return this.duanMap;
	}
}
