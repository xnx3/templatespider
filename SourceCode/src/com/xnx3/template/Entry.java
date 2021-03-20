package com.xnx3.template;

import org.jsoup.nodes.Entities;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel;
import org.jvnet.substance.theme.SubstanceCremeTheme;

import com.xnx3.util.AutoRun;
import com.xnx3.util.SkinUtil;

public class Entry {
	public static void main(String[] args) {
		new SkinUtil().UseLookAndFeelBySubstance().setCurrentTheme(new SubstanceCremeTheme());
		
		
		Global.mainUI.setVisible(true);
		Global.mainUI.getLblNewLabel_log().setText("<html><div style=\"width:100%; text-align:center; font-size:22px;\">点击查看使用说明</div>");
		AutoRun.versionCheck();//版本检测
	}
}
