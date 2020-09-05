package com.xnx3.template.ui;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.xnx3.template.Global;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import java.awt.Font;

public class TemplateVarGainJframe extends JFrame {

	private JPanel contentPane;
	private JTextField varTextLengthtextField;
	private JLabel label;
	private JTextField xiangsiduPercentTextField;
	private JLabel label_1;
	private JTextField textField_sim;
	private JTextArea textArea_2;
	private JLabel lblNewLabel_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TemplateVarGainJframe frame = new TemplateVarGainJframe();
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
	public TemplateVarGainJframe() {
		setTitle("高级设置-模版变量提取");
		setBounds(100, 100, 532, 544);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		varTextLengthtextField = new JTextField();
		varTextLengthtextField.setText("10");
		varTextLengthtextField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("变量字符串的长度 > ");
		
		label = new JLabel("变量的相似度个数 > ");
		
		xiangsiduPercentTextField = new JTextField();
		xiangsiduPercentTextField.setText("80");
		xiangsiduPercentTextField.setColumns(10);
		
		label_1 = new JLabel("%");
		
		JButton button = new JButton("完成设定");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Global.templateVarGainJframe.setVisible(false);
			}
		});
		
		textField_sim = new JTextField();
		textField_sim.setToolTipText("提取模版变量的相似度识别，取值0～1之间。数值越大，识别精确度越高");
		textField_sim.setText("0.98");
		textField_sim.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("相似度");
		
		JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		textArea.setEditable(false);
		textArea.setEnabled(false);
		textArea.setLineWrap(true);
		textArea.setText("从模版页面中，自动寻找页面中共用的模版变量时，所找到的模版变量，并不是完全一样一个字也不差的。这里便是所找到的模版变量允许的字符误差百分比。如，默认是0.98，便是 98% 的相似度，就认为是有同样的模版变量");
		
		JLabel label_2 = new JLabel("个字符");
		
		JTextArea textArea_1 = new JTextArea();
		textArea_1.setText("对提取出的模版变量进行过滤。这里默认为 10 个字符，您可以填写一个整数。 当提取出模版变量后，会自动对模版变量进行过滤，将模版变量的内容（html内容）字符长度低于10个字符的，都会自动过滤掉。只显示模版变量的变量内容，字符长度大于10个字符的。");
		textArea_1.setLineWrap(true);
		textArea_1.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		textArea_1.setEnabled(false);
		textArea_1.setEditable(false);
		
		textArea_2 = new JTextArea();
		textArea_2.setText("对提取出的模版变量进行过滤。这里默认相似度大于  80% ，您可以在此填写一个整数。 当提取出模版变量后，会自动对模版变量进行过滤。\n比如，要提取的模版页面一共有7个，提取出的模版变量 相同数+相似数 （也就是某个模版变量一共有几个模版页面共用）有5个，那么这个相似度个数便是  5/7 = 0.71 = 71% , 上面设置的是80%， 那么这个71%不满足，就会被过滤掉， 只显示相似度大于80%的，所提取的模版变量");
		textArea_2.setLineWrap(true);
		textArea_2.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		textArea_2.setEnabled(false);
		textArea_2.setEditable(false);
		
		lblNewLabel_2 = new JLabel("友情提示：如果您不是很懂，您可以使用默认的即可");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textField_sim, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(varTextLengthtextField, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(248, Short.MAX_VALUE))
				.addComponent(textArea, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(textArea_1, GroupLayout.PREFERRED_SIZE, 522, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(label, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(xiangsiduPercentTextField, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
					.addGap(318))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(textArea_2, GroupLayout.PREFERRED_SIZE, 522, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(18)
					.addComponent(button, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblNewLabel_2, GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
						.addComponent(textField_sim, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(29)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
								.addComponent(varTextLengthtextField, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(30)
							.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textArea_1, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
					.addGap(31)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(label, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
						.addComponent(xiangsiduPercentTextField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textArea_2, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_2)
						.addComponent(button, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
	}
	public JTextField getVarTextLengthtextField() {
		return varTextLengthtextField;
	}
	public JTextField getXiangsiduPercentTextField() {
		return xiangsiduPercentTextField;
	}
	public JTextField getTextField_sim() {
		return textField_sim;
	}
}
