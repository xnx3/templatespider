package com.xnx3.spider.ui;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

/**
 * MainUI的右下角 JPanel_explain 面板
 * @author 管雷鸣
 */
public class JPanelExplain extends JPanel {

	/**
	 * Create the panel.
	 */
	public JPanelExplain() {
		
		JButton btnNewButton = new JButton("New button");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnNewButton)
					.addContainerGap(209, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
					.addContainerGap())
		);
		setLayout(groupLayout);

	}

}
