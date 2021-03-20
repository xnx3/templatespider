package com.xnx3.spider;

import org.jvnet.substance.theme.SubstanceCremeTheme;

import com.xnx3.util.AutoRun;
import com.xnx3.util.SkinUtil;

/**
 * 入口
 * @author 管雷鸣
 */
public class Entry {
	public static void main(String[] args) {
//		SubstanceOfficeBlue2007LookAndFeel s = new UI().UseLookAndFeelBySubstance();
//		s.setCurrentTheme(new SubstanceCremeTheme());
//		s.setCurrentBorderPainter(new StandardBorderPainter());
//		s.setCurrentWatermark(new SubstanceBubblesWatermark());
		new SkinUtil().UseLookAndFeelBySubstance().setCurrentTheme(new SubstanceCremeTheme());
		
		Global.mainUI.setVisible(true);
		Global.mainUI.getTextArea_url().setText(Global.TEXTAREA_REMIND);
		//隐藏更多设置，也就是设置 request 参数的面板
		Global.mainUI.moreSetPanel_showAndHidden();
		
		Global.mainUI.setBounds(30, 50, 600, 400);
		
		AutoRun.versionCheck();//版本检测
	}
}
