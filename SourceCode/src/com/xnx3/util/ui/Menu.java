package com.xnx3.util.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.xnx3.SystemUtil;
import com.xnx3.UI;
import com.xnx3.util.CheckVersion;

/**
 * 菜单
 * @author 管雷鸣
 */
public class Menu {
	
	/**
	 * 关于我们的菜单
	 * @return
	 */
	public static JMenu aboutMenu(){
		JMenu aboutMenu = new JMenu("关于");
		
		JMenuItem mntmNewMenuItem = new JMenuItem("关于我们");
		aboutMenu.add(mntmNewMenuItem);
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UI.showMessageDialog(""
						+ "\n当前版本：v"+com.xnx3.G.VERSION
						+ "\n作者：管雷鸣"
						+ "\nQQ：921153866"
						+ "\n微信：xnx3com"
						+ "\n邮箱：mail@xnx3.com"
						+ "\n官网：www.wang.market"
						+ "\n微信公众号: wangmarket"
						+ "\n开源发布：https://github.com/xnx3/templatespider");
			}
		});
		
		JMenuItem hezuoMenuItem = new JMenuItem("各种合作");
		aboutMenu.add(hezuoMenuItem);
		hezuoMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UI.showMessageDialog(""
						+ "无论你是哪个行业，若我们对你有用，欢迎联系我们！只要对我方有好处，能推动我方前进，都有机会合作！");
			}
		});
		
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("检查更新");
		aboutMenu.add(mntmNewMenuItem_1);
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!CheckVersion.cloudCheck()){
					UI.showMessageDialog("当前已是最新版本！");
				}
			}
		});
		
		return aboutMenu;
	}
	
	/**
	 * 建站的菜单
	 * @return
	 */
	public static JMenu wangmarketMenu(){
		JMenu siteMenu = new JMenu("建站");
		
		JMenuItem mianfeiMenuItem = new JMenuItem("免费开通网站");
		siteMenu.add(mianfeiMenuItem);
		mianfeiMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SystemUtil.openUrl("http://wang.market/regByPhone.do?inviteid=50");
			}
		});
		JMenuItem wscMenuItem = new JMenuItem("网市场云建站系统官网");
		siteMenu.add(wscMenuItem);
		wscMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SystemUtil.openUrl("http://www.wang.market");
			}
		});
		
		
		return siteMenu;
	}
}
