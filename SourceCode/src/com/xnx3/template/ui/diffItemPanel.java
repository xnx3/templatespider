package com.xnx3.template.ui;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextArea;

public class diffItemPanel extends JPanel {
	private JTextArea textArea;
	private JLabel lblNewLabel;

	/**
	 * Create the panel.
	 */
	public diffItemPanel() {
		
		lblNewLabel = new JLabel("template name");
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 18));
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
					.addGap(6))
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE))
		);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		setLayout(groupLayout);

	}
	public JTextArea getTextArea() {
		return textArea;
	}
	public JLabel getLblNewLabel() {
		return lblNewLabel;
	}
}
