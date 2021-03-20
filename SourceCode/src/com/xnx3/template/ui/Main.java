package com.xnx3.template.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
import com.xnx3.swing.DialogUtil;
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
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

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
	private JTextField textField_templateName;
	private JLabel lblNewLabel_progress;
	private JTextArea textArea_ResourceUrl;

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
		setTitle("模版计算工具");
		setBounds(100, 100, 1074, 617);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(Menu.aboutMenu());
		
		JMenu helpMenu = new JMenu("帮助");
		menuBar.add(helpMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("使用说明");
		helpMenu.add(mntmNewMenuItem);
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SystemUtil.openUrl("http://tag.wscso.com/4647.html");
			}
		});
		
		menuBar.add(Menu.wangmarketMenu());
		
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
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(editPanel, GroupLayout.DEFAULT_SIZE, 764, Short.MAX_VALUE)
						.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 764, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(editPanel, GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 266, GroupLayout.PREFERRED_SIZE))
				.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
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
					if(DialogUtil.showConfirmDialog("确定要删除［"+value+"］吗") == DialogUtil.CONFIRM_YES){
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
						DialogUtil.showMessageDialog("该变量已被删除");
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
				SystemUtil.openUrl("http://tag.wscso.com/4647.html");
			}
		});
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "2. \u5BFC\u5165\u6A21\u7248", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "3. \u8D44\u6E90\u66FF\u6362", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "5. \u5BFC\u51FA\u6A21\u7248", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "4. \u63D0\u53D6\u6A21\u7248\u53D8\u91CF", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		lblNewLabel_progress = new JLabel("进度");
		lblNewLabel_progress.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_progress.setToolTipText("显示模版变量计算的进度");
		lblNewLabel_progress.setFont(new Font("Lucida Grande", Font.BOLD, 17));
		
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "1. \u9009\u62E9\u5EFA\u7AD9\u7CFB\u7EDF", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"网市场云建站系统"}));
		
		JLabel label_1 = new JLabel("帮助说明");
		label_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SystemUtil.openUrl("http://tag.wscso.com/8139.html");
			}
		});
		label_1.setForeground(Color.BLUE);
		label_1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		GroupLayout gl_panel_5 = new GroupLayout(panel_5);
		gl_panel_5.setHorizontalGroup(
			gl_panel_5.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_5.createSequentialGroup()
					.addGroup(gl_panel_5.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panel_5.createSequentialGroup()
							.addContainerGap()
							.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
						.addComponent(comboBox, 0, 117, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_5.setVerticalGroup(
			gl_panel_5.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_5.createSequentialGroup()
					.addContainerGap()
					.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 140, Short.MAX_VALUE)
					.addComponent(label_1)
					.addContainerGap())
		);
		panel_5.setLayout(gl_panel_5);
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(panel_5, GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel, GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel_4, GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel_3, GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblNewLabel_log, GroupLayout.DEFAULT_SIZE, 679, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblNewLabel_progress, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)))
					.addGap(0))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE, false)
						.addComponent(panel_5, GroupLayout.PREFERRED_SIZE, 219, GroupLayout.PREFERRED_SIZE)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 219, GroupLayout.PREFERRED_SIZE)
						.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 219, GroupLayout.PREFERRED_SIZE)
						.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 219, GroupLayout.PREFERRED_SIZE)
						.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 219, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(lblNewLabel_progress, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lblNewLabel_log, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		btnNewButton_run = new JButton("开始提取");
		btnNewButton_run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//相似度赋值
				Global.sim = Double.valueOf(Global.templateVarGainJframe.getTextField_sim().getText()).doubleValue();
				
				//将现有的模版变量清空掉
				Global.templateVarMap.clear();	//清理内存数据
				Global.mainUI.getTemplateVarTableModel().setRowCount(0);	//清理table数据
				
				getBtnNewButton_run().setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						GainTemplateVar.start();
						
						//提取完毕之后，进行自动过滤
						Global.log("提取完毕，进行过滤");
						Action.templateVarListFilter();
					}
				}).start();
				getBtnNewButton_run().setText("自动提取中...");
			}
		});
		
		JButton button_3 = new JButton("过滤条件设定");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Global.templateVarGainJframe.setVisible(true);
			}
		});
		
		JLabel label_4 = new JLabel("帮助说明");
		label_4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SystemUtil.openUrl("http://tag.wscso.com/8142.html");
			}
		});
		label_4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label_4.setForeground(Color.BLUE);
		
		GroupLayout gl_panel_4 = new GroupLayout(panel_4);
		gl_panel_4.setHorizontalGroup(
			gl_panel_4.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
						.addComponent(button_3, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
						.addComponent(btnNewButton_run, GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
						.addComponent(label_4, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_panel_4.setVerticalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addContainerGap()
					.addComponent(button_3, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton_run, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
					.addGap(78)
					.addComponent(label_4)
					.addContainerGap())
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
		
		JLabel label_5 = new JLabel("帮助说明");
		label_5.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SystemUtil.openUrl("http://tag.wscso.com/8143.html");
			}
		});
		label_5.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label_5.setForeground(Color.BLUE);
		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3.setHorizontalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addComponent(button, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
						.addComponent(label, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
						.addComponent(textField_templateName, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
						.addComponent(label_5, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_panel_3.setVerticalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addComponent(label, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textField_templateName, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(button, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
					.addComponent(label_5)
					.addContainerGap())
		);
		panel_3.setLayout(gl_panel_3);
		
		JButton btnNewButton = new JButton("替换");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//动态标签替换，如title等
				Action.replaceDongtaiTag();
				//将js、css的相对路径变为绝对路径
				Action.replaceResourceQuote();
			}
		});
		
		JLabel lblNewLabel = new JLabel("请设置资源路径");
		
		JLabel label_3 = new JLabel("帮助说明");
		label_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SystemUtil.openUrl("http://tag.wscso.com/8141.html");
			}
		});
		label_3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label_3.setForeground(Color.BLUE);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_panel_2.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
						.addGroup(Alignment.TRAILING, gl_panel_2.createSequentialGroup()
							.addGap(1)
							.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
						.addGroup(Alignment.TRAILING, gl_panel_2.createSequentialGroup()
							.addContainerGap(75, Short.MAX_VALUE)
							.addComponent(label_3, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
				.addComponent(scrollPane_3, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_3, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(label_3)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		textArea_ResourceUrl = new JTextArea();
		scrollPane_3.setViewportView(textArea_ResourceUrl);
		textArea_ResourceUrl.setToolTipText("请输入url");
		textArea_ResourceUrl.setLineWrap(true);
		panel_2.setLayout(gl_panel_2);
		
		JButton button_2 = new JButton("导入");
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
		
		JLabel label_2 = new JLabel("帮助说明");
		label_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SystemUtil.openUrl("http://tag.wscso.com/8140.html");
			}
		});
		label_2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label_2.setForeground(Color.BLUE);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addComponent(button_2, GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
					.addGap(3))
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addContainerGap(54, Short.MAX_VALUE)
					.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(button_2)
					.addPreferredGap(ComponentPlacement.RELATED, 138, Short.MAX_VALUE)
					.addComponent(label_2)
					.addContainerGap())
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
					if(DialogUtil.showConfirmDialog("确定要删除［"+value+"］吗") == DialogUtil.CONFIRM_YES){
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
						DialogUtil.showMessageDialog("该变量已被删除");
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
	
	public JTextField getTextField_templateName() {
		return textField_templateName;
	}
	public JLabel getLblNewLabel_progress() {
		return lblNewLabel_progress;
	}
	public JTextArea getTextArea_ResourceUrl() {
		return textArea_ResourceUrl;
	}
}
