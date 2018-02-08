package com.xnx3.util.stringDiff;

/**
 * 短字符特征串，用于StringDiff中进行第二轮diff对比
 * @author 管雷鸣
 */
public class ShortStringTrait {
	private String originalText;
	private String beforeVarKey;	//其originalText的之前的，上一个{xnx3=?}这个?的数
	private String purificationText;	//originalText过滤掉空符后的值，并且在前面后面都加上{xnx3=?}识别。此项设置要在beforeVarKey之后赋值！
	public String getOriginalText() {
		return originalText;
	}
	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}
	public String getBeforeVarKey() {
		return beforeVarKey;
	}
	public void setBeforeVarKey(String beforeVarKey) {
		this.beforeVarKey = beforeVarKey;
	}
	public String getPurificationText() {
		return purificationText;
	}
	public void setPurificationText(String purificationText) {
		this.purificationText = "{xnx3="+getBeforeVarKey()+"}"+purificationText+"{xnx3";
	}
	
}
