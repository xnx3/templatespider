package com.xnx3.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.table.TableColumn;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.xnx3.file.FileUtil;
import com.xnx3.template.bean.ElementDiffRecord;
import com.xnx3.template.bean.Template;
import com.xnx3.template.vo.ElementDiffListVO;
import com.xnx3.template.vo.ElementDiffVO;
import com.xnx3.util.StringDiff;
import com.xnx3.util.StringUtil;

/**
 * 提取模版变量
 * @author 管雷鸣
 */
public class GainTemplateVar {
	
	/**
	 * 开启运行入口
	 */
	public static void start(){
		//汇总所有模版页面对比的结果，并排重。得到所有预计可进行独立出的模版变量
		List<ElementDiffRecord> recordList = new ArrayList<ElementDiffRecord>();	
		
		/**
		 * key：模板页面名，如index.html
		 * value TemplateCompute
		 */
		Map<String, TemplateCompute> map = new HashMap<String, TemplateCompute>();
		
		int currentDiJige = 0;	//当前第几个模版页面对比了，计算进度条使用。
		Global.mainUI.getLblNewLabel_progress().setText("0%");
		/**** 第一步，找出所有模版页面的所有的模版变量，并排重后，存入 recordList ****/
		for (Map.Entry<String, Template> entry : Global.templateMap.entrySet()) {
			Template t = entry.getValue();
			TemplateCompute d = new TemplateCompute(t);
			
			//用第一个模板页面中, body的一级Tag，遍历所有模板页面，看是否使用到了
			d.diffChindElement(t.getBodyElement());
			map.put(t.getFile().getName(), d);
			Global.log("计算模版页面："+t.getFile().getName());
			
			//遍历当前模版页面下，自动计算出的绝大数相似的模版变量
			for (int j = 0; j < d.recordList.size(); j++) {
				//模版中提取的
				ElementDiffRecord templateRecord = d.recordList.get(j);
				
				//进行排重、汇总。将所有模版页面中提取的模版变量汇集到一块进行处理
				boolean findSim = false;	//是否发现了相似的，但是在汇总列表中却是没有的，要加入汇总列表。若发现了要加入的，则为true。若没有发现，则为false，为false时，那么就要将其加入进汇总的对比列表了  在以下for循环中判断
				for (int i = 0; i < recordList.size(); i++) {
					//总的，汇集起来的，Entry中的
					ElementDiffRecord entryRecord = recordList.get(i);
					//判断其相似程度，是否已经加入进总的record中了。
					if(StringUtil.similarity(entryRecord.getElementDiffVO().getDiffElement(), templateRecord.getElementDiffVO().getDiffElement()) > Global.sim){
						//已经加入进过，找到相似度大于 Global.sim 的，那么标注findSim，已经有相似的了
						findSim = true;
						break;
					}
				}
				if(!findSim){
					recordList.add(templateRecord);
					System.out.println("++++"+StringUtil.purification(templateRecord.getElementDiffVO().getDiffElement().toString()));
				}
				
				/****1.整个判断是否有通用模块****/
//						Element ele = r.getElementDiffVO().getDiffElement();
//						String key = com.xnx3.util.StringUtil.purification(ele.toString());
//						
//						TemplateVarFilter filter = filterMap.get(key);
//						if(filter != null){
//							filter.setSimSum(filter.getSimSum()+1);
//						}else{
//							filter = new TemplateVarFilter();
//							filter.setKey(key);
//							filter.setElement(ele);
//							filter.setSimSum(1);
//						}
//						filterMap.put(key, filter);
				
			}
			
			currentDiJige++;
			
			int jindu = (int) Math.floor(currentDiJige*100/Global.templateMap.size());
			Global.mainUI.getLblNewLabel_progress().setText(jindu+"%");
		}
		
		Global.mainUI.getLblNewLabel_progress().setText("98%");
		System.out.println("组合完毕,共:"+recordList.size());
		
		Global.log("变量抽取完成，进行互相包含、排重计算");
		/*
		 * 
		 * 模版变量互相包含的计算筛选
		 * 1.被包含的模版变量，是否其使用的模版页面个数一样，若一样，则子模版变量删除
		 * 2.若被包含的模版变量，其使用的模版页面个数不一样，那么子模版变量保留，父模版变量要动态引用子模版变量
		 * 
		 */
		String allTemplateVarGroupString = "，";	//所有模版变量的组合，将其组合成一个字符串。中间以"，，"分割。以便下面的变量在其中搜寻
		for (int i = 0; i < recordList.size(); i++) {
			ElementDiffRecord record = recordList.get(i);
			allTemplateVarGroupString = allTemplateVarGroupString + record.getElementDiffVO().getDiffElement().toString()+ "，" ;
		}
		//将allTemplateVarGroupString进行净化
		allTemplateVarGroupString = StringUtil.purification(allTemplateVarGroupString);
		
		//第二步所得到的，排重后，过滤掉子变量后，得到的新的变量列表
		List<ElementDiffRecord> twoRecordList = new ArrayList<ElementDiffRecord>();
		
		//将每个模版变量，从净化好的allTemplateVarGroupString中找，看是否能找到大于等于2的数量
		for (int i = 0; i < recordList.size(); i++) {
			ElementDiffRecord record = recordList.get(i);
			//当前模版变量净化后的字符串
			String varString = StringUtil.purification(record.getElementDiffVO().getDiffElement().toString());
			//从第一次发现的变量，往后开始截取字符串，组合总的净化变量，以排重
			String nt = allTemplateVarGroupString.substring(allTemplateVarGroupString.indexOf(varString), allTemplateVarGroupString.length());
			int index1 = allTemplateVarGroupString.indexOf(varString);
			int index2 = allTemplateVarGroupString.lastIndexOf(varString);
			if(index1 != -1 && index1 != index2){
				//是子变量，将子变量过滤，不做处理。
				
				
				/*
				 * 
				 * 待考虑，判断子变量的个数，以及父变量的个数。如果子变量个数小于父变量个数，那么还是要保留的
				 * 
				 */
//				System.out.println("过滤子变量："+index1+","+index2+"--"+varString);
			}else{
				//不是子变量，那么加入新的变量列表
				twoRecordList.add(record);
			}
		}
		Global.mainUI.getLblNewLabel_progress().setText("99%");
		
		System.out.println("第二部组合的："+twoRecordList.size());
		
		/**** 第三部，人工筛选 *****/
//		for (int i = 0; i < twoRecordList.size(); i++) {
//			ElementDiffRecord record = twoRecordList.get(i);
//			System.out.println("=======");
//			System.out.println(record.getElementDiffVO().getDiffElement());
//		}
		
		
		/**** 第四部，赋予UI界面显示出来 *****/
		for (int i = 0; i < twoRecordList.size(); i++) {
			ElementDiffRecord record = twoRecordList.get(i);
			Element ele = record.getElementDiffVO().getDiffElement();
			//给模版变量起个名字。名字要唯一
			String tagName = ele.tagName();
			if(tagName.equals("script")){
				tagName = "js";
			}
			String templateVarName = tagName+"_"+ele.id()+"_"+ele.toString().length();
			//将模版变量的结果保存
			Global.templateVarMap.put(templateVarName, record);
			
			//下面交给Action.showUITemplateVarJTabel()
//			Vector rowData = new Vector();
//			int[] diff = Action.isPreviewDiff(record.getElementDiffListVO());
//			rowData.add(templateVarName);
//			rowData.add(diff[0]);
//			rowData.add(diff[1]);
//			rowData.add("删除");
//			rowData.add(diff[1] > 0? "对比":"");
//			rowData.add("");
//			
//			Global.mainUI.getTemplateVarTableModel().insertRow(0, rowData);
		}
		//刷新，到UI界面显示最新模版变量
		Action.showUITemplateVarJTabel();
		
		Global.mainUI.getLblNewLabel_progress().setText("100%");
//		for (int i = 0; i < Global.mainUI.getTemplateVarTableModel().getRowCount(); i++) {
//			JCheckBox jcb = new JCheckBox("使用");
//	        TableColumn tableColumn = Global.mainUI.getTemplateVarJTable().getColumnModel().getColumn(2);
//	        tableColumn.setCellEditor(new DefaultCellEditor(jcb));
//		}
		
		Global.mainUI.getBtnNewButton_run().setEnabled(true);
		Global.mainUI.getBtnNewButton_run().setText("提取模版变量");
		Global.log("模版变量计算提取完毕");
	}
}
