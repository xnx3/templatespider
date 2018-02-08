package com.xnx3.template;

import com.xnx3.util.AutoRun;

public class Entry {
	public static void main(String[] args) {
		Global.mainUI.setVisible(true);
		Global.mainUI.getLblNewLabel_log().setText("<html>作者：管雷鸣"
				+ "<br/>官网：www.wang.market");
		AutoRun.versionCheck();//版本检测
	}
}
