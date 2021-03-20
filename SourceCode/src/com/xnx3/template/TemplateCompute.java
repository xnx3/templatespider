package com.xnx3.template;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xnx3.BaseVO;
import com.xnx3.StringUtil;
import com.xnx3.file.FileUtil;
import com.xnx3.template.bean.ElementDiffRecord;
import com.xnx3.template.bean.Template;
import com.xnx3.template.ui.Diff;
import com.xnx3.template.ui.diffJeditorPanel;
import com.xnx3.template.vo.ElementDiffListVO;
import com.xnx3.template.vo.ElementDiffVO;
import com.xnx3.util.StringDiff;

/**
 * 模版页面计算，模版计算，将不同处计算出来
 * @author 管雷鸣
 */
public class TemplateCompute {
	private Template template;	//当前操作的模板页面
	
//	public static List<Template> templateList;
//	public static Map<String, Template> templateMap;
	
	public List<ElementDiffRecord> recordList;	//对比的结果
	
	public TemplateCompute(Template template) {
		this.template = template;
		recordList = new ArrayList<ElementDiffRecord>();
	}
	
	/**
	 * 传入一个元素，对比传入的元素与所有模版页面中是否有相同的或者相近的
	 */
	public void diffChindElement(Element e){
		Global.log(template.getFile().getName()+"："+e.tagName()+"-->"+e.toString().length());
		Elements childElements = e.children();
		if(childElements.size() > 0){
			//如果其子元素数量》1，或者子元素跟父元素不是完全一样的，那肯定就是有子元素了，判断子元素
			
			//将所有子元素便利出来
			for (int j = 0; j < childElements.size(); j++) {
				Element diffE = childElements.get(j);
				
				//将其子元素再所有模板中寻找，看是否存在。若存在，且相似度>0.98，直接进行替换操作
				ElementDiffListVO vo = findElementByAllTemplate(diffE);
				
				//所有页面通用模版的相似度，0~1
				double sim = vo.getDiffEqualNum()/Global.templateMap.size();
				
				int ipentityNum = vo.getIpentityNum();	//模板变量在模板页面中，完全相同的的个数

				//进行模版变量的提取，看是否有符合的
				extraceTemplateVar(vo);
				
				if(sim == 1){
					//完全相同，终止继续下级元素的扫描
				}else{
					//只要不是完全相同，那么就继续往下扫描
					//判断其是否还有子元素，若有子元素，将子元素找出来，找到到底是哪。如过没有子元素了，那估计就是此处的问题了，将此处暴露出来，以供人工审查
					if(diffE.children().size() == 0){
						//没有下级子元素了，那就是此处了
//						previewDiffUI(vo);
					}else{
						//还有子元素，继续搜索其子元素
						diffChindElement(diffE);
					}
					
				}
					
			}
		}
	}
	
	/**
	 * 提取模版变量
	 */
	public void extraceTemplateVar(ElementDiffListVO vo){
		//判断是否有可提取的模版变量，认为可提取的，有两张或者以上，相似度超过Global.sim的
		int fuheNum = 0;
		for (int i = 0; i < vo.getList().size(); i++) {
			ElementDiffVO edvo =  vo.getList().get(i);
			if(edvo.getD() > Global.sim){
				fuheNum++;
			}
		}
		if(fuheNum > 1){
			//有超过1个的，那么进行记录
			//完全相同，记录，同时终止继续下级元素的扫描
			ElementDiffRecord record = new ElementDiffRecord();
			record.setElementDiffVO(vo.getList().get(0));
			record.setElementDiffListVO(vo);
			recordList.add(record);
		}
	}
	
	public void previewDiffUI(ElementDiffListVO vo){
		//找出的完全相同的  key:file.name
		Map<String, ElementDiffVO> equalMap = new HashMap<String, ElementDiffVO>();
		//找出不完全相同，但是绝大多数相同的
		Map<String, ElementDiffVO> similarMap = new HashMap<String, ElementDiffVO>();
		
		for (int i = 0; i < vo.getList().size(); i++) {
			ElementDiffVO ed = vo.getList().get(i);
			if(ed.getD() == 1){
				equalMap.put(ed.getTargetFile().getName(), ed);
			}else if (ed.getD() > 0.9) {
				similarMap.put(ed.getTargetFile().getName(), ed);
			}
		}
		
		if(similarMap.size() == 0 ){
			//根本没有相似的要进行对比，退出判断
			return;
		}
		
		//判断一下，是完全相同的数量多，还是极近相同的多
		if(similarMap.size() > equalMap.size()){
			//极近相同的多，那么，对比一下这些极近相同的里面，是否这些是一样的，而完全相同的只是个例而已
			/*
			 * 
			 */
			System.out.println("极近相同的多，那么，对比一下这些极近相同的里面，是否这些是一样的，而完全相同的只是个例而已");
		}
		
		//进行相似度diff展示
		
		Diff d = new Diff();
		/** diff第一个 **/
		String fileNames1 = "";
		Element firstElement = null;
		for (Map.Entry<String, ElementDiffVO> entry : equalMap.entrySet()) {  
			fileNames1 = fileNames1 + entry.getKey()+", ";
			if(firstElement == null){
				firstElement = entry.getValue().getTargetElement();
			}
		}
//		diffItemPanel item1 = new diffItemPanel();
//		item1.getTextArea().setText(text1);
//		item1.getLblNewLabel().setText(fileNames1);
//		d.getContentPane().add(item1);
		diffJeditorPanel item1 = new diffJeditorPanel();
		item1.getLblNewLabel().setText(fileNames1);
		item1.originalElement = firstElement;
		
		/** diff其他不相似的列表 **/
		for (Map.Entry<String, ElementDiffVO> entry : similarMap.entrySet()) {  
			StringDiff sd = new StringDiff(firstElement.toString(), entry.getValue().getTargetElement().toString());
			
			diffJeditorPanel jp = new diffJeditorPanel();
			jp.getEditorPane().setText(sd.getTwoStrDiff());
			jp.getLblNewLabel().setText(entry.getValue().getTargetFile().getName());
			jp.originalElement = entry.getValue().getTargetElement();
			d.getContentPane().add(jp);
			
			
			item1.getEditorPane().setText(sd.getFirstStrDiff());
		}
		d.getContentPane().add(item1);
		d.setVisible(true);
		while(d.use){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 将某行的字符串，在所有模板页面中进行对比，看此行在模板页面中有几个模板页匹配
	 * @param list 要挨个对比的模板
	 * @param diffElement 对比的元素
	 * @return 字符串匹配的模板的数量，个数
	 */
	public ElementDiffListVO findElementByAllTemplate(Element diffElement){
		ElementDiffListVO vo = new ElementDiffListVO();
		List<ElementDiffVO> list = new ArrayList<ElementDiffVO>();
		
		//首先便利出所有模板来
		for (Map.Entry<String, Template> entry : Global.templateMap.entrySet()) {
			Template t = entry.getValue();
			
			ElementDiffVO edVO = findElementByTemplate(t, diffElement);
			list.add(edVO);
			if(edVO.getResult() - BaseVO.SUCCESS == 0){
				vo.setDiffEqualNum(vo.getDiffEqualNum()+edVO.getD());
//				System.out.println(edVO.getD()+"----"+(edVO.getD()));
				if(edVO.getD() - 0.99999 > 0){
					vo.setIpentityNum(vo.getIpentityNum()+1);
				}
			}
		}
		
		
		vo.setList(list);
		return vo;
	}
	
	/**
	 * 判断某个模板(body)中，是否有此Element存在
	 * @param bodyElement 要判断的模板的body元素
	 * @param diff 要判断寻找的元素
	 * @return 0~1 越相似，越靠近1
	 */
	public ElementDiffVO findElementByTemplate(Template template, Element diff){
		Element bodyElement = template.getBodyElement();
		
		ElementDiffVO vo = new ElementDiffVO();
		vo.setDiffElement(diff);
		vo.setTargetFile(template.getFile());
		
		Elements es = bodyElement.getElementsByTag(diff.tagName());
		if(es.size() == 0){
			//没有找到这个Element的Tag，那么直接返回0
			vo.setResult(BaseVO.FAILURE);
			return vo;
		}
		
		for (int i = 0; i < es.size(); i++) {
			Element e = es.get(i);
			
			//进行对比
			double d1 = com.xnx3.util.StringUtil.similarity(diff, e);
			if(d1 > vo.getD()){
				vo.setD(d1);
				vo.setTargetElement(e);
			}
		}
		
		return vo;
	}
	
}
