package com.xnx3.template.ui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JEditorPane;
import javax.swing.JButton;
import javax.swing.text.BadLocationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xnx3.file.FileUtil;
import com.xnx3.template.Global;
import com.xnx3.template.TemplateCompute;
import com.xnx3.template.bean.Template;
import com.xnx3.util.StringUtil;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class diffJeditorPanel extends JPanel {
	private JLabel lblNewLabel;
	private JEditorPane editorPane;
	private JButton btnNewButton_AllEqualThis;
//	public String diffTemplateFileNames;		//跟此进行对比的另其他模版文件的文件名列表。如 index.html,about.html,a.html
	
	public Element originalElement;	//当前的原始元素。后面如果改动的话，会拿此到模版里进行替换找寻
	
	/**
	 * Create the panel.
	 */
	public diffJeditorPanel() {
		
		lblNewLabel = new JLabel("template name");
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 18));
		
		JScrollPane scrollPane = new JScrollPane();
		
		btnNewButton_AllEqualThis = new JButton("保存到文件");
		btnNewButton_AllEqualThis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String templateFileName = getLblNewLabel().getText();
				//对比的
				String[] fns = templateFileName.split(",");
				
				//将list转化为map形式。key：file.name  可以根据模版的名字来取模版
//				Map<String, Template> templateMap = new HashMap<String, Template>();
//				for (int j = 0; j < TemplateCompute.templateList.size(); j++) {
//					Template t = TemplateCompute.templateList.get(j);
//					templateMap.put(t.getFile().getName(), t);
//				}
				
				
//				getEditorPane().setContentType("text/plain");
				String text = null;
				try {
					 text = getEditorPane().getDocument().getText(0, getEditorPane().getDocument().getLength());
				} catch (BadLocationException e2) {
					e2.printStackTrace();
				}
				
//				List<Template> templateList = new ArrayList<Template>();
				for (int i = 0; i < fns.length; i++) {
					String n = fns[i].trim();
					if(n != null && n.length() > 2){
						Template temp = Global.templateMap.get(n);
						if(temp == null){
							System.out.println(n+"模版未发现");
							continue;
						}
//						templateList.add(templateMap.get(n));
//						System.out.println(i+"-->"+n);
						System.out.println(n);
//						if(temp.toString().indexOf(originalElementString) > -1){
//							System.out.println("------发先，进行替换");
							
//							Elements searchEs = temp.getDoc().getElementsByTag(originalElement.tagName());
//							for (int j = 0; j < searchEs.size(); j++) {
//								Element se = searchEs.get(j);
//								if(se.hashCode() == originalElement.hashCode()){
//									//发现了
//									System.out.println("------发先，进行替换");
////									searchEs.get(j) = 
//									
//								}
//							}
							
							String html = temp.getDoc().toString().replace(originalElement.toString(), text);
							
							try {
								FileUtil.write(temp.getFile().getPath(), html, FileUtil.UTF8);
								temp.setDoc(Jsoup.parse(html));
								System.out.println("已经替换："+text);
								Global.templateMap.put(temp.getFile().getName(), temp);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							
//						}
						
					}
				}
				
			}
		});
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
					.addGap(18)
					.addComponent(btnNewButton_AllEqualThis)
					.addContainerGap())
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(btnNewButton_AllEqualThis, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE))
		);
		
		editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		scrollPane.setViewportView(editorPane);
		setLayout(groupLayout);

	}
	public JLabel getLblNewLabel() {
		return lblNewLabel;
	}
	public JEditorPane getEditorPane() {
		return editorPane;
	}
	public JButton getBtnNewButton_AllEqualThis() {
		return btnNewButton_AllEqualThis;
	}
	
	
}
