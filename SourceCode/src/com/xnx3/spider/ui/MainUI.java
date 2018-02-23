package com.xnx3.spider.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JButton;

import com.xnx3.SystemUtil;
import com.xnx3.UI;
import com.xnx3.UrlUtil;
import com.xnx3.spider.Global;
import com.xnx3.spider.Initialize;
import com.xnx3.spider.cache.PageSpider;
import com.xnx3.util.StringUtil;
import com.xnx3.util.ui.Menu;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.awt.Toolkit;

public class MainUI extends JFrame {

	private JPanel contentPane;
	private JTextArea textArea_url;
	private JButton btnNewButton;
	private JPanel panel_explain;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUI frame = new MainUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainUI() {
		setTitle("扒网站工具");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 496, 309);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(Menu.aboutMenu());
		
		JMenu helpMenu = new JMenu("帮助");
		menuBar.add(helpMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("使用说明");
		helpMenu.add(mntmNewMenuItem);
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SystemUtil.openUrl("http://www.wang.market/2712.html");
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		
		JPanel panel = new JPanel();
		
		panel_explain = new JPanel();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_explain, GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE))
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
						.addComponent(panel_explain, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		
		btnNewButton = new JButton("开始抓取");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String t = getTextArea_url().getText();
				if(t.indexOf("请将要抓取") > -1){
					if(UI.showConfirmDialog("提示：请先设定要抓取的URL<br/>是否查看帮助说明？") == UI.CONFIRM_YES){
						SystemUtil.openUrl("http://www.wang.market/2712.html");
					}
					return;
				}
				
				
				final String[] urls = t.split("\r|\n");
				
				if(urls.length > 0 && urls[0].length() > 0){
					
				}else{
					UI.showMessageDialog("请输入目标网址");
					return;
				}
				
				Global.log("开始对输入网址检测...");
				Global.logUI.setVisible(true);
				getBtnNewButton_Start().setText("提取中..");
				getBtnNewButton_Start().setEnabled(false);
				
				com.xnx3.spider.Global.templateDomain = UrlUtil.getDomain(urls[0]);
				Global.log("初始化创建存放文件夹： "+Global.getLocalTemplatePath());
				Initialize.createCacheFile();
				
				
				new Thread(new Runnable() {
					public void run() {
						for (int i = 0; i < urls.length; i++) {
							if(urls[i].length() > 6){
								new PageSpider(urls[i]);
							}
						}
						getBtnNewButton_Start().setText("开始提取");
						getBtnNewButton_Start().setEnabled(true);
						Global.log("抓取完毕");
						
						try {
							java.awt.Desktop.getDesktop().open(new File(Global.getLocalTemplatePath()));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
				
			}
		});
		
		JButton button = new JButton("日志");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Global.logUI == null){
					UI.showMessageDialog("工具尚未运行，暂无日志");
					return;
				}
				Global.logUI.setVisible(true);
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(button, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(38, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(button, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
						.addComponent(btnNewButton, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE))
					.addContainerGap())
		);
		panel.setLayout(gl_panel);
		
		JLabel lblNewLabel = new JLabel("作者：管雷鸣    微信公众号:wangmarket");
		
		JLabel lblNewLabel_1 = new JLabel("开源发布 gitee.com/mail_osc/templatespider");
		lblNewLabel_1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblNewLabel_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SystemUtil.openUrl("https://gitee.com/mail_osc/templatespider");
			}
		});
		GroupLayout gl_panel_explain = new GroupLayout(panel_explain);
		gl_panel_explain.setHorizontalGroup(
			gl_panel_explain.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel_explain.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_explain.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
						.addComponent(lblNewLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_explain.setVerticalGroup(
			gl_panel_explain.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_explain.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel_1)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_explain.setLayout(gl_panel_explain);
		
		textArea_url = new JTextArea();
		scrollPane.setViewportView(textArea_url);
		contentPane.setLayout(gl_contentPane);
	}
	public JTextArea getTextArea_url() {
		return textArea_url;
	}

	public JButton getBtnNewButton_Start() {
		return btnNewButton;
	}
	public JPanel getPanel_explain() {
		return panel_explain;
	}
}
