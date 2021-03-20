package com.xnx3.util;

import com.xnx3.SystemUtil;
import com.xnx3.swing.DialogUtil;
import com.xnx3.G;
import com.xnx3.version.VersionUtil;
import com.xnx3.version.VersionVO;

/**
 * 检查新版本
 * @author 管雷鸣
 */
public class CheckVersion {
	
	/**
	 * 云端检测
	 * @return false：没有最新的，当前是最新版本
	 */
	public static boolean cloudCheck(){
		VersionVO vo = VersionUtil.cloudContrast("http://version.xnx3.com/templatespider.html", G.VERSION);
		if(vo.isFindNewVersion()){
			if(DialogUtil.showConfirmDialog("发现新版本：v"+vo.getNewVersion()) == DialogUtil.CONFIRM_YES){
				SystemUtil.openUrl(vo.getPreviewUrl());
			}
			return true;
		}
		return false;
	}
	
}
