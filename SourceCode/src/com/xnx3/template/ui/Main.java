package com.xnx3.template.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.TableModel;
import javax.swing.JButton;

import com.xnx3.SystemUtil;
import com.xnx3.UI;
import com.xnx3.template.Action;
import com.xnx3.template.GainTemplateVar;
import com.xnx3.template.Global;
import com.xnx3.template.bean.ElementDiffRecord;
import com.xnx3.template.bean.Template;
import com.xnx3.util.CheckVersion;
import com.xnx3.util.ui.Menu;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextArea;
import javax.swing.JLabel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

import java.beans.VetoableChangeListener;
import java.io.File;
import java.util.Map;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.Font;

import javax.swing.SwingConstants;
import java.awt.Cursor;

public class Main extends JFrame {

	//最后一次鼠标点击的模版变量的table，其行的变量名
	public static String lastClickTemplateVarName;
	//最后一次鼠标点击的模版页面的table，其行的页面名
	public static String lastClickTemplatePageName;
	
	private JPanel contentPane;
	private JTable templatePageJTable;
	private DefaultTableModel templatePageTableModel;
	private DefaultTableModel templateVarTableModel;
	private JScrollPane scrollPane_1;
	private JScrollPane scrollPane;
	private JTable templateVarJTable;
	private JButton btnNewButton_run;
	private JTextArea textArea;
	private JLabel lblNewLabel_log;
	private JTextField textField;
	private JTextField textField_sim;
	private JTextField textField_templateName;
	private JLabel lblNewLabel_progress;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
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
	public Main() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("模版计算工具  -  作者：管雷鸣");
		setBounds(100, 100, 792, 557);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(Menu.aboutMenu());
		
		JMenu helpMenu = new JMenu("帮助");
		menuBar.add(helpMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("使用说明");
		helpMenu.add(mntmNewMenuItem);
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SystemUtil.openUrl("http://www.wang.market/4234.html");
			}
		});
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		
		JPanel panel_1 = new JPanel();
		
		JPanel editPanel = new JPanel();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(editPanel, GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(0)
					.addComponent(editPanel, GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 218, GroupLayout.PREFERRED_SIZE))
				.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
		);
		
		scrollPane = new JScrollPane();
		scrollPane.setBorder(new TitledBorder(null, "\u6A21\u7248\u9875\u9762", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		templatePageOperate();
		templatePageJTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				int row=getTemplatePageJTable().getSelectedRow();
			    String value =(String) getTemplatePageJTable().getValueAt(row, 0);	//模版页面的名字
			    if(value != null && value.length() > 0){
			    	lastClickTemplatePageName = value;
			    	getTextArea().setText(Global.templateMap.get(value).getDoc().toString());
			    }
			}
			
		});
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "\u81EA\u52A8\u63D0\u53D6\u7684\u6A21\u7248\u53D8\u91CF", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		templateVarOperate();
		templateVarJTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				int row=getTemplateVarJTable().getSelectedRow();
			    String value =(String) getTemplateVarJTable().getValueAt(row, 0);	//模版变量的名字
			    if(value != null && value.length() > 0){
			    	lastClickTemplateVarName = value;
			    }else{
			    	return;
			    }
			    
				if(getTemplateVarJTable().getSelectedColumn() == 3){
					if(UI.showConfirmDialog("确定要删除［"+value+"］吗") == UI.CONFIRM_YES){
						System.out.println("选择了删除："+value);
						getTemplateVarTableModel().removeRow(row);
						//删除缓存的模版变量
						Global.templateVarMap.remove(value);
						//刷新，到UI界面显示最新模版变量
						Action.showUITemplateVarJTabel();
					}
				}else if (getTemplateVarJTable().getSelectedColumn() == 4) {
					Action.previewDiffUI(Global.templateVarMap.get(value).getElementDiffListVO());
				}else{
					if(Global.templateVarMap.get(value) == null){
						UI.showMessageDialog("该变量已被删除");
						//刷新，到UI界面显示最新模版变量
						Action.showUITemplateVarJTabel();
					}else{
						getTextArea().setText(Global.templateVarMap.get(value).getElementDiffVO().getDiffElement().toString());
					}
				}
			}
			
		});
		GroupLayout gl_editPanel = new GroupLayout(editPanel);
		gl_editPanel.setHorizontalGroup(
			gl_editPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_editPanel.createSequentialGroup()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_editPanel.setVerticalGroup(
			gl_editPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_editPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_editPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE))
					.addContainerGap())
		);
		editPanel.setLayout(gl_editPanel);
		
		lblNewLabel_log = new JLabel("log");
		lblNewLabel_log.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblNewLabel_log.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SystemUtil.openUrl("http://www.wang.market/4234.html");
			}
		});
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "1. \u5BFC\u5165\u6A21\u7248", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "2. \u8D44\u6E90\u66FF\u6362", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "4. \u5BFC\u51FA\u6A21\u7248", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "3. \u63D0\u53D6\u6A21\u7248\u53D8\u91CF", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		lblNewLabel_progress = new JLabel("进度");
		lblNewLabel_progress.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_progress.setToolTipText("显示模版变量计算的进度");
		lblNewLabel_progress.setFont(new Font("Lucida Grande", Font.BOLD, 17));
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_log, GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(panel, GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 133, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel_4, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 119, Short.MAX_VALUE)
							.addGap(1))
						.addComponent(lblNewLabel_progress, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE))
					.addGap(5))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
							.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 147, Short.MAX_VALUE)
							.addComponent(panel_3, GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
						.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 147, Short.MAX_VALUE)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lblNewLabel_progress, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_log, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		
		JLabel label_1 = new JLabel("相似度：");
		
		textField_sim = new JTextField();
		textField_sim.setToolTipText("提取模版变量的相似度识别，取值0～1之间。数值越大，识别精确度越高");
		textField_sim.setText("0.98");
		textField_sim.setColumns(10);
		
		btnNewButton_run = new JButton("开始提取");
		btnNewButton_run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//相似度赋值
				Global.sim = Double.valueOf(getTextField_sim().getText()).doubleValue();
				
				//将现有的模版变量清空掉
				Global.templateVarMap.clear();	//清理内存数据
				Global.mainUI.getTemplateVarTableModel().setRowCount(0);	//清理table数据
				
				getBtnNewButton_run().setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						GainTemplateVar.start();
					}
				}).start();
				getBtnNewButton_run().setText("自动提取中...");
			}
		});
		GroupLayout gl_panel_4 = new GroupLayout(panel_4);
		gl_panel_4.setHorizontalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_4.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_4.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnNewButton_run, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
						.addGroup(gl_panel_4.createSequentialGroup()
							.addComponent(label_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textField_sim, GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_panel_4.setVerticalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE, false)
						.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
						.addComponent(textField_sim, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton_run, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
					.addGap(40))
		);
		panel_4.setLayout(gl_panel_4);
		
		textField_templateName = new JTextField();
		textField_templateName.setColumns(10);
		
		JButton button = new JButton("导出模版");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Action.exportTemplate();
			}
		});
		
		JLabel label = new JLabel("模版名字：");
		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3.setHorizontalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_3.createSequentialGroup()
							.addComponent(button, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(Alignment.TRAILING, gl_panel_3.createSequentialGroup()
							.addGroup(gl_panel_3.createParallelGroup(Alignment.TRAILING)
								.addComponent(label, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
								.addComponent(textField_templateName, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
							.addContainerGap())))
		);
		gl_panel_3.setVerticalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addComponent(label, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textField_templateName, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(button, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(12, Short.MAX_VALUE))
		);
		panel_3.setLayout(gl_panel_3);
		
		textField = new JTextField();
		textField.setToolTipText("资源文件在网上的绝对路径。一般都会将资源进行CDN加速");
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("替换");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Action.replaceResourceQuote();
			}
		});
		
		JButton button_1 = new JButton("动态标签替换");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Action.replaceDongtaiTag();
			}
		});
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
						.addComponent(textField, 0, 0, Short.MAX_VALUE)
						.addComponent(button_1, GroupLayout.PREFERRED_SIZE, 116, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, gl_panel_2.createSequentialGroup()
							.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
							.addGap(2)))
					.addGap(5))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_2.createSequentialGroup()
					.addComponent(textField, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(button_1, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		panel_2.setLayout(gl_panel_2);
		
		JButton button_2 = new JButton("导入模版");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();// 文件选择器  
				jfc.setFileSelectionMode(1);// 设定只能选择到文件夹  
				int state = jfc.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句  
	            if (state == 1) {  
	                return;
	            } else {  
	                File f = jfc.getSelectedFile();// f为选择到的目录  
	                Global.path = f.getAbsolutePath();
	                //加载模版
	                Action.loadLocalTemplate(Global.path+"/");
//	                System.out.println(f.getAbsolutePath());  
	            }
				
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addComponent(button_2, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(button_2, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(82, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		panel_1.setLayout(gl_panel_1);
		
		textArea = new JTextArea();
		textArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.isPopupTrigger()){
				}
			}
		});
		scrollPane_2.setViewportView(textArea);
		
		
		contentPane.setLayout(gl_contentPane);
		
//		tableModel.getDataVector().clear();  //清空里面所有数据
	}
	

	/**
	 * 模版页面面板的相关操作
	 */
	public void templatePageOperate(){

		String[] templatePageHeads = {"模版文件","类型","备注"};
		String[] s1 = { "", "",""};
        String data1[][] = { s1};
        this.templatePageTableModel = new DefaultTableModel(data1,templatePageHeads);
		templatePageJTable = new JTable(templatePageTableModel);
		scrollPane.setViewportView(templatePageJTable);
		templatePageJTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				int row=getTemplatePageJTable().getSelectedRow();
			    String value =(String) getTemplatePageJTable().getValueAt(row, 0);	//模版页面的名字
			    if(value != null && value.length() > 0){
			    	lastClickTemplatePageName = value;
			    	getTextArea().setText(Global.templateMap.get(value).getDoc().toString());
			    }
			}
			
		});
		templatePageTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if(e.getColumn() == 0){
					String value = (String) templatePageTableModel.getValueAt(e.getFirstRow(), e.getColumn());
					if(lastClickTemplatePageName == null || lastClickTemplatePageName.length() == 0){
						//么有发现旧的模版变量，那么应该只是点击进去，退出来了，没有修改
						Global.log("更改模版变量名字，未修改，放弃-"+value);
					}else{
						//如果之前的跟改完的不一样，那就是有改动
						if(!value.equals(lastClickTemplatePageName)){
							//缓存新的模版变量
							Global.templateMap.put(value, Global.templateMap.get(lastClickTemplatePageName));
							//删除旧的模版变量
							Global.templateMap.remove(lastClickTemplatePageName);
							Global.log("更改模版页面名字："+lastClickTemplatePageName+" --> "+value);
						}
					}
				}
			}
		});
	}
	

	/**
	 * 模版变量相关面板操作
	 */
	public void templateVarOperate(){
		String[] templateVarHeads = {"变量名称","相同数","相似数","删除","人工对比","备注"};
		String[] s2 = { "","","","","",""};
        String data2[][] = { s2};
        this.templateVarTableModel = new DefaultTableModel(data2,templateVarHeads);
        templateVarJTable = new JTable(templateVarTableModel);
       
		scrollPane_1.setViewportView(templateVarJTable);
		templateVarJTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				int row=getTemplateVarJTable().getSelectedRow();
			    String value =(String) getTemplateVarJTable().getValueAt(row, 0);	//模版变量的名字
			    if(value != null && value.length() > 0){
			    	lastClickTemplateVarName = value;
			    }else{
			    	return;
			    }
			    
				if(getTemplateVarJTable().getSelectedColumn() == 3){
					if(UI.showConfirmDialog("确定要删除［"+value+"］吗") == UI.CONFIRM_YES){
						System.out.println("选择了删除："+value);
						getTemplateVarTableModel().removeRow(row);
						//删除缓存的模版变量
						Global.templateVarMap.remove(value);
						//刷新，到UI界面显示最新模版变量
						Action.showUITemplateVarJTabel();
					}
				}else if (getTemplateVarJTable().getSelectedColumn() == 4) {
					Action.previewDiffUI(Global.templateVarMap.get(value).getElementDiffListVO());
				}else{
					if(Global.templateVarMap.get(value) == null){
						UI.showMessageDialog("该变量已被删除");
						//刷新，到UI界面显示最新模版变量
						Action.showUITemplateVarJTabel();
					}else{
						getTextArea().setText(Global.templateVarMap.get(value).getElementDiffVO().getDiffElement().toString());
					}
				}
			}
			
		});
		templateVarTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if(e.getColumn() == 0){
					String value = (String) templateVarTableModel.getValueAt(e.getFirstRow(), e.getColumn());
					//判断是否是修改了
					if(lastClickTemplateVarName.equalsIgnoreCase(value)){
						//未有修改
						return;
					}
					
					if(lastClickTemplateVarName == null || lastClickTemplateVarName.length() == 0){
						//么有发现旧的模版变量，那么应该只是点击进去，退出来了，没有修改
						Global.log("更改模版变量名字，未修改，放弃-"+value);
					}else{
						//缓存新的模版变量
						Global.templateVarMap.put(value, Global.templateVarMap.get(lastClickTemplateVarName));
						//删除旧的模版变量
						Global.templateVarMap.remove(lastClickTemplateVarName);
						Global.log("更改模版变量名字："+lastClickTemplateVarName+" --> "+value);
						
						//刷新，到UI界面显示最新模版变量
						Action.showUITemplateVarJTabel();
					}
					
				}
				
			}
		});
	}
	
	public JTable getTemplatePageJTable() {
		return templatePageJTable;
	}
	public DefaultTableModel getTemplatePageTableModel(){
		return this.templatePageTableModel;
	}
	public DefaultTableModel getTemplateVarTableModel(){
		return this.templateVarTableModel;
	}
	public JTable getTemplateVarJTable() {
		return templateVarJTable;
	}
	public JButton getBtnNewButton_run() {
		return btnNewButton_run;
	}
	public JTextArea getTextArea() {
		return textArea;
	}
	public JLabel getLblNewLabel_log() {
		return lblNewLabel_log;
	}
	public JTextField getTextField_ResourceUrl() {
		return textField;
	}
	public JTextField getTextField_sim() {
		return textField_sim;
	}
	public JTextField getTextField_templateName() {
		return textField_templateName;
	}
	public JLabel getLblNewLabel_progress() {
		return lblNewLabel_progress;
	}
}
