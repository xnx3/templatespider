package com.xnx3.util;

/**
 * 应用运行时，自动启动
 * @author 管雷鸣
 */
public class AutoRun {
	
	/**
	 * 新版本检测。若发现新版本，弹出提示框
	 */
	public static void versionCheck(){
		new Thread(new Runnable() {
			public void run() {
				CheckVersion.cloudCheck();
			}
		}).start();
	}
	
	public static void main(String[] args) {
		versionCheck();
	}
}
