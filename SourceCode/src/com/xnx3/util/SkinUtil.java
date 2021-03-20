package com.xnx3.util;

import java.io.InputStream;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;
import org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel;
import org.jvnet.substance.utils.SubstanceConstants.ImageWatermarkKind;
import org.jvnet.substance.watermark.SubstanceImageWatermark;
import com.xnx3.Log;

/**
 * Swing 皮肤相关工具类
 * @author 管雷鸣
 *
 */
public class SkinUtil {
	

	/**
	 * 使用第三方外观包,同时设置背景图。 {@link UI#UseLookAndFeelBySubstance()}
	 * <li>需导入substance.jar
	 * <li>设置主题 SubstanceLookAndFeel.setCurrentTheme(new SubstanceCremeTheme());
	 * <li>设置按钮外观 SubstanceLookAndFeel.setCurrentButtonShaper(new StandardButtonShaper());
	 * <li>设置边框 SubstanceLookAndFeel.setCurrentBorderPainter(new StandardBorderPainter());
	 * <li>设置渐变渲染 SubstanceLookAndFeel.setCurrentGradientPainter(new StandardGradientPainter());  
	 * <li>设置标题 SubstanceLookAndFeel.setCurrentTitlePainter(new MatteHeaderPainter()); 
	 * <li>设置水印 SubstanceOfficeBlue2007LookAndFeel.setCurrentWatermark(new SubstanceMarbleVeinWatermark());
	 * @param watermarkBackgroundImage 水印背景图片 ，传入如：MainEntry.class.getResourceAsStream("res/bg.jpg") 使用当前目录下res内的bg.jpg作为水印图
	 * @param watermarkOpacity 水印图片的透明度，取值范围0.1~1之间，越接近1越真实，数字越小越模糊
	 * @return SubstanceOfficeBlue2007LookAndFeel外观包操作对象，可继续扩展
	 */
	public SubstanceOfficeBlue2007LookAndFeel UseLookAndFeelBySubstance(final InputStream watermarkBackgroundImage,final float watermarkOpacity){
		SubstanceOfficeBlue2007LookAndFeel substance = null;
		try {
			//外观设置
			substance=new SubstanceOfficeBlue2007LookAndFeel();
			UIManager.setLookAndFeel(substance);
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			if(watermarkBackgroundImage!=null){
				class mySkin extends BusinessBlackSteelSkin{
					public mySkin(){
						super();
						//新建一个图片水印，路径可以自己该，使用自己喜欢的图片来做应用程序的水印图片、  
						SubstanceImageWatermark watermark =  new SubstanceImageWatermark(watermarkBackgroundImage);
						watermark.setOpacity(watermarkOpacity);
				        watermark.setKind(ImageWatermarkKind.APP_CENTER);
				        this.watermark=watermark;
					}
				}
				SubstanceLookAndFeel.setSkin(new mySkin());
			}
		} catch (Exception e) {
			new Log().debug("com.xnx3.UI", "UseLookAndFeelBySubstance()", e.getMessage());
		}
		
		return substance;
	}
	

	/**
	 * 使用第三方外观包
	 * <br/>需在Jframe创建之前调用
	 * <li>需导入substance.jar
	 * <li>设置主题 SubstanceLookAndFeel.setCurrentTheme(new SubstanceCremeTheme());
	 * <li>设置按钮外观 SubstanceLookAndFeel.setCurrentButtonShaper(new StandardButtonShaper());
	 * <li>设置边框 SubstanceLookAndFeel.setCurrentBorderPainter(new StandardBorderPainter());
	 * <li>设置渐变渲染 SubstanceLookAndFeel.setCurrentGradientPainter(new StandardGradientPainter());  
	 * <li>设置标题 SubstanceLookAndFeel.setCurrentTitlePainter(new MatteHeaderPainter()); 
	 * <li>设置水印 SubstanceOfficeBlue2007LookAndFeel.setCurrentWatermark(new SubstanceMarbleVeinWatermark());
	 * @return SubstanceOfficeBlue2007LookAndFeel 外观包操作对象，可继续扩展
	 * @see UI#UseLookAndFeelBySubstance(InputStream, float)
	 */
	public SubstanceOfficeBlue2007LookAndFeel UseLookAndFeelBySubstance(){
		return UseLookAndFeelBySubstance(null, 0);
	}
	

	
}
