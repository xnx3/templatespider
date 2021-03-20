package com.xnx3.template;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xnx3.DateUtil;
import com.xnx3.Lang;
import com.xnx3.exception.NotReturnValueException;
import com.xnx3.file.FileUtil;
import com.xnx3.swing.DialogUtil;
import com.xnx3.template.Global;
import com.xnx3.template.bean.ElementDiffRecord;
import com.xnx3.template.bean.Template;
import com.xnx3.template.ui.Diff;
import com.xnx3.template.ui.diffJeditorPanel;
import com.xnx3.template.vo.ElementDiffListVO;
import com.xnx3.template.vo.ElementDiffVO;
import com.xnx3.util.StringDiff;
import com.xnx3.util.StringUtil;

/**
 * 其他的一些动作
 * @author 管雷鸣
 */
public class Action {
	
	/**
	 * 加载本地模版
	 */
	public static void loadLocalTemplate(String templateFilePath){
		System.out.println("加载本地模版 : "+ templateFilePath);
		
		Global.templateMap.clear();
		/**加载本地的模板**/
		File file = new File(templateFilePath);
		for (int i = 0; i < file.listFiles().length; i++) {
			File f = file.listFiles()[i];
			if(f.isFile()){
				//如果是文件
				
				if(f.getName().indexOf(".html") > 0){
					//找出所有的html模板文件
					//读取模板文件加载
					Template temp = new Template();
					temp.setFile(f);
					String html = FileUtil.read(f.getPath(), FileUtil.UTF8);
//					temp.setDoc(Jsoup.parse(html));
					try {
						temp.setDoc(Jsoup.parse(f, "UTF-8"));
					} catch (IOException e) {
						e.printStackTrace();
					}
//					templateList.add(temp);
					Global.templateMap.put(f.getName(), temp);
				}
			}
		}
		
		
		//将之前的在table中的清空掉
		Global.mainUI.getTemplatePageTableModel().setRowCount( 0 );
		
		//将新的加入进table中
		for (Map.Entry<String, Template> entry : Global.templateMap.entrySet()) {
			Template temp = entry.getValue();
			Vector rowData = new Vector();
			rowData.add(temp.getFile().getName());
			rowData.add("请选择");
			rowData.add("备注");
			Global.mainUI.getTemplatePageTableModel().insertRow(0, rowData);
		}
		for (int i = 0; i < Global.mainUI.getTemplatePageTableModel().getRowCount(); i++) {
			JComboBox<String> jcb = new JComboBox<String>();   
	        jcb.addItem("首页模版");   
	        jcb.addItem("列表页模版");   
	        jcb.addItem("详情页模版");   
	        jcb.setEditable(true);   
	        TableColumn tableColumn = Global.mainUI.getTemplatePageJTable().getColumnModel().getColumn(1);
	        tableColumn.setCellEditor(new DefaultCellEditor(jcb));
		}
	}
	
	/**
	 * 替换模版源代码，将资源引用的相对路径改为绝对路径
	 */
	public static void replaceResourceQuote(){
		for (Map.Entry<String, Template> entry : Global.templateMap.entrySet()) {
			Template temp = entry.getValue();
			
			Document doc = temp.getDoc();
			String baseUri = Global.mainUI.getTextArea_ResourceUrl().getText().trim();
			if(baseUri == null){
				DialogUtil.showMessageDialog("请先输入资源路径");
				return;
			}
			if(baseUri.lastIndexOf("/")+1 == baseUri.length()){
				baseUri = baseUri.substring(0, baseUri.length()-1);
			}
			
			ResourceQuote rq = new ResourceQuote(baseUri);
			doc = rq.tagReplace(doc, "img", "src");
			doc = rq.tagReplace(doc, "link", "href");
			doc = rq.tagReplace(doc, "script", "src");
			
			String html = rq.htmlFilter(doc.toString(), "src=\"(.*?)\"");
			html = rq.htmlFilter(html, "background-image: *url\\('*(.*?)'*\\)");
			
			temp.setDoc(Jsoup.parse(html));
			Global.templateMap.put(temp.getFile().getName(), temp);
//			FileUtil.write(temp.getFile(), html);
		}
	}
	

	/**
	 * 人工比对，比对极其相似的模版变量
	 */
	public static void previewDiffUI(ElementDiffListVO vo){
		//找出的完全相同的  key:file.name
		Map<String, ElementDiffVO> equalMap = new HashMap<String, ElementDiffVO>();
		//找出不完全相同，但是绝大多数相同的
		Map<String, ElementDiffVO> similarMap = new HashMap<String, ElementDiffVO>();
		
		try {
			for (int i = 0; i < vo.getList().size(); i++) {
				ElementDiffVO ed = vo.getList().get(i);
				if(ed.getD() == 1){
					equalMap.put(ed.getTargetFile().getName(), ed);
				}else if (ed.getD() >= Global.sim) {
					similarMap.put(ed.getTargetFile().getName(), ed);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		
		
		/** diff第一个 **/
		String fileNames1 = "";
		Element firstElement = null;
		for (Map.Entry<String, ElementDiffVO> entry : equalMap.entrySet()) {  
			fileNames1 = fileNames1 + entry.getKey()+", ";
			if(firstElement == null){
				firstElement = entry.getValue().getTargetElement();
			}
		}
		
		Diff d = new Diff();
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
			
			//将两个对比的字符串合并，然后去空格。判断其是否包含特殊符号等，如果不全是汉字或英文及数字，那么返回false，即不能合并
			String diffOriginalStr = (sd.getFirstStrOriginalDiff()+sd.getTwoStrOriginalDiff()).replaceAll(" ", "");	//对比取得的原始的不相同的字符串
			System.out.println("diff----"+diffOriginalStr);
			if(StringUtil.isChinese(diffOriginalStr)){
				//true，那么可以合并！
				Template temp = Global.templateMap.get(entry.getValue().getTargetFile().getName());
				String html = temp.getDoc().toString().replace(entry.getValue().getTargetElement().toString(), entry.getValue().getDiffElement().toString());
				
				try {
					FileUtil.write(temp.getFile().getPath(), html, FileUtil.UTF8);
					temp.setDoc(Jsoup.parse(html));
					System.out.println("已经替换："+StringUtil.purification(entry.getValue().getTargetElement().toString()));
					Global.templateMap.put(temp.getFile().getName(), temp);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
//				entry.getValue().getTargetFile().
			}else{
				System.out.println("不可合并："+StringUtil.purification(entry.getValue().getTargetElement().toString()));
			}
			
			item1.getEditorPane().setText(sd.getFirstStrDiff());
		}
		d.getContentPane().add(item1);
		d.setVisible(true);
		
	}
	
	/**
	 * 计算以下，是否有必要进行人工干预diff
	 * @return int[] int[0]:完全相同的变量的数量，   int[1]:非常相近的变量的数量
	 */
	public static int[] isPreviewDiff(ElementDiffListVO vo){
		int result[] = new int[3];
		
		//找出的完全相同的  key:file.name
		Map<String, ElementDiffVO> equalMap = new HashMap<String, ElementDiffVO>();
		//找出不完全相同，但是绝大多数相同的
		Map<String, ElementDiffVO> similarMap = new HashMap<String, ElementDiffVO>();
		
		try {
			for (int i = 0; i < vo.getList().size(); i++) {
				ElementDiffVO ed = vo.getList().get(i);
				if(ed.getD() == 1){
					equalMap.put(ed.getTargetFile().getName(), ed);
				}else if (ed.getD() >= Global.sim) {
					similarMap.put(ed.getTargetFile().getName(), ed);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		result[0] = equalMap.size();
		result[1] = similarMap.size();
		return result;
	}
	
	/**
	 * 导出.wscso模版文件
	 */
	public static void exportTemplate(){
		/*
		 * 遍历出模版变量
		 */
		Map<String, ElementDiffRecord> templateVarVOMap = new HashMap<String, ElementDiffRecord>();		//存储的是ElementDiffRecord对象。 key:变量名字(JTable重新命名的)。 此在模版页面中将变量替换为{include}时用到
		List<Map<String, String>> templateVarList = new ArrayList<Map<String,String>>();	//存储的是直接变为json的变量内容
		DefaultTableModel varModel = Global.mainUI.getTemplateVarTableModel();
		int varRowCount = varModel.getRowCount();
		for (int i = 0; i < varRowCount; i++) {
			Map<String, String> map = new HashMap<String, String>();
			//模版变量名字
			String name = (String) varModel.getValueAt(i, 0);
			if(name != null && name.length() > 0){
				map.put("var_name", exportTemplateStringDispose(name));
				map.put("remark", exportTemplateStringDispose((String)varModel.getValueAt(i, 5)));
				map.put("text", exportTemplateStringDispose(Global.templateVarMap.get(name).getElementDiffVO().getDiffElement().toString()));
				
				templateVarList.add(map);
				templateVarVOMap.put(name, Global.templateVarMap.get(name));
				writeTemplateFile("var_"+name, Global.templateVarMap.get(name).getElementDiffVO().getDiffElement().toString());	//本地写出文件
			}
		}
		
		
		/*
		 * 遍历出模版页面
		 */
		List<Map<String, String>> templatePageList = new ArrayList<Map<String,String>>();
		DefaultTableModel pageModel = Global.mainUI.getTemplatePageTableModel();
		int pageRowCount = pageModel.getRowCount();
		for (int i = 0; i < pageRowCount; i++) {
			Map<String, String> map = new HashMap<String, String>();
			//模版页面名字
			String name = (String) pageModel.getValueAt(i, 0);
			if(name != null && name.length() > 0){
				String templatePageName = name;
				if(name.indexOf(".") > 0){
					//如果是index.html这样的，那么过滤掉后面的后缀
					templatePageName = name.split("\\.")[0];
				}
				map.put("name", exportTemplateStringDispose(templatePageName));
				//模版页面备注
				map.put("remark", exportTemplateStringDispose((String) pageModel.getValueAt(i, 2)));
				String type = (String) pageModel.getValueAt(i, 1);
				switch (type) {
				case "首页模版":
					type = "1";
					break;
				case "列表页模版":
					type = "2";
					break;
				case "详情页模版":
					type = "3";
					break;
				default:
					type = "3";
					break;
				}
				map.put("type", type);
				String tihuanmobanbianliangdepage = templatePageReplaceVar(name, templateVarVOMap);
				map.put("text", exportTemplateStringDispose(tihuanmobanbianliangdepage));
				map.put("editMode", "2");
				templatePageList.add(map);
				writeTemplateFile("page_"+templatePageName, tihuanmobanbianliangdepage);	//本地写出文件
			}
		}
		
		JSONObject jo = new JSONObject();
		jo.put("systemVersion", exportTemplateStringDispose(Global.VERSION));	// 当前系统版本号
		jo.put("time", DateUtil.timeForUnix10());	//导出的时间，10为时间戳
		jo.put("templateName", exportTemplateStringDispose(Global.mainUI.getTextField_templateName().getText()));	//当前模版的名字
		jo.put("sourceUrl", ""); 	//模版来源的网站，从那个网站导出来的，可以作为预览网站
		jo.put("useUtf8Encode", "true");
		
		jo.put("templatePageList", templatePageList);
		jo.put("templateVarList", templateVarList);
		
		//将.wscso模版文件保存到当前项目文件夹
		FileUtil.write(Global.getAppPath()+getTemplatePathName()+"/template.wscso", jo.toString());
		
		try {
			java.awt.Desktop.getDesktop().open(new File(Global.getAppPath()+getTemplatePathName()+"/"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 在app跟目录写出模版文件
	 * @param fileName 文件名字
	 * @param text 文件内容
	 */
	public static void writeTemplateFile(String fileName, String text){
		File file = new File(Global.getAppPath()+getTemplatePathName()+"/");
		if(!file.exists()){
			file.mkdir();
		}
		
		FileUtil.write(Global.getAppPath()+getTemplatePathName()+"/"+fileName+".html", text);
	}
	
	/**
	 * 将模版页面替换上模版变量，即用{include=??}替换上
	 * @param tempaltePageName 要进行抽取替换的模版页面的名字，Jtable的名字，如index.html
	 * @param templateVarVOMap 当前提取好的模版变量，要保存为模版的模版变量
	 */
	public static String templatePageReplaceVar(String tempaltePageName, Map<String, ElementDiffRecord> templateVarVOMap){
		if(Global.templateMap.get(tempaltePageName) == null){
			System.err.println("----------null------"+tempaltePageName);
			return "";
		}
		Document doc = Jsoup.parse(Global.templateMap.get(tempaltePageName).getDoc().toString());
		
		//当前模版页面所使用的模版变量的名字。会在下面for中判断出
		List<String> haveVarNameList = new ArrayList<String>();
		
		//遍历当前的模版变量，将页面中，每个模版变量都找一遍
		for (Map.Entry<String, ElementDiffRecord> entry : templateVarVOMap.entrySet()) {
			ElementDiffRecord record = entry.getValue();
			String varName = entry.getKey();	//变量名
			ElementDiffVO diffVO = null;	//当前模版页面中，拿来对比的元素
			
			for (int i = 0; i < record.getElementDiffListVO().getList().size(); i++) {
				ElementDiffVO edVO = record.getElementDiffListVO().getList().get(i);
				
				if(edVO.getTargetFile().getName().equals(tempaltePageName)){
					//针对遍历的某个变量，找到其在 tempaltePageName 这个模版页面中的记录。当然， 若找不到记录，那就是这个模版页面没有使用这个变量，忽略即可
					if(edVO.getD() >= Global.sim){
						//其相似度符合标准，那么取之
						diffVO = edVO;
						//发现了，那么退出本次for循环，继续找下一个变量在页面中是否存在
						break;
					}
				}
			}
			
			if(diffVO != null){
				//找到页面中的模版变量了，将变量替换为变量标签
				
				Element varElement = diffVO.getTargetElement();	//变量Element
				
				//元素搜索
				Elements eles = doc.getElementsByTag(varElement.tagName()); 		//页面中搜寻的变量的element
				for (int i = 0; i < eles.size(); i++) {
					Element e = eles.get(i);
					
					double s = StringUtil.similarity(e, varElement);
					if(s > Global.sim){
						System.out.println("发现相同＝"+s+"＝"+varName+"＝＝＝＝"+e.toString().length());
						haveVarNameList.add(varName);	//加入使用列表
						e.before("<h1>include_varName="+varName+"</h1>");
//						e.after("<h2>include_after_varName="+varName+"</h2>");
						e.remove(); //将当前找到的元素删除掉
					}
				}

			}
		}
		
		String html = doc.toString();
		for (int i = 0; i < haveVarNameList.size(); i++) {
			String varName = haveVarNameList.get(i);
			System.out.println("－－替换模版："+varName);
			html = StringUtil.replaceAll(html, "<h1>include_varName="+varName+"</h1>", " {include="+varName+"} ");
//			html = com.xnx3.StringUtil.subStringReplace(html, "<h1>include_before_varName="+varName+"</h1>", "<h2>include_after_varName="+varName+"</h2>", " {include="+varName+"} ");
		}
		
		return html;
	}
	
	/**
	 * 导出模版文件，对String类型的数据进行处理
	 * @return
	 */
	private static String exportTemplateStringDispose(String value){
		return com.xnx3.StringUtil.StringToUtf8(value);
	}
	
	
	
	static String site_name = "{site.name}";
	static String siteColumn_name = "{siteColumn.name}";
	static String news_title = "{news.title}";
	/**
	 * 替换模版页面中的动态标签
	 * 1.替换title标签
	 * 2.删除keywords 、 description
	 */
	public static void replaceDongtaiTag(){
		/*
		 * 遍历出模版页面
		 */
		List<Map<String, String>> templatePageList = new ArrayList<Map<String,String>>();
		
		DefaultTableModel pageModel = Global.mainUI.getTemplatePageTableModel();
		int pageRowCount = pageModel.getRowCount();
		for (int i = 0; i < pageRowCount; i++) {
			Map<String, String> map = new HashMap<String, String>();
			//模版页面名字
			String name = (String) pageModel.getValueAt(i, 0);
			if(name != null && name.length() > 0){
				
				Template temp = Global.templateMap.get(name);
				if(temp != null){
					//有这个模版页面
					Document doc = temp.getDoc();
					
					//删除 keywords 、 description
					Elements metaEles = doc.getElementsByTag("meta");
					Iterator<Element> it = metaEles.iterator();
					while(it.hasNext()){
						Element metaEle = it.next();
						String metaName = metaEle.attr("name");
						if(metaEle != null && metaName != null){
							if(metaName.equalsIgnoreCase("keywords") || metaName.equalsIgnoreCase("description")){
								try {
									metaEle.remove();
									it.remove();
								} catch (Exception e) {
									e.printStackTrace();
									System.out.println(metaEle);
								}
							}
						}
					}
					
					//替换title标签
					Elements titleEles = doc.getElementsByTag("title");
					Element titleEle = null;
					if(titleEles != null && titleEles.size() > 0){
						titleEle = titleEles.first();
					}else{
						//若没有这个title，那么需要新增加一个
						Elements headElements = doc.getElementsByTag("head");
						if(headElements == null || headElements.size() == 0){
							DialogUtil.showMessageDialog("模版页面"+temp.getFile().getName()+"中无head标签！模版页估计不完整！请手动补上head标签");
							return;
						}else{
//							titleEle = new Element(tag, baseUri)
//							headElements.first().appendElement(tagName)
							/*
							 * 待加入
							 */
						}
					}
					if(titleEle != null){
						//替换title标签为动态标签
						String type = (String) pageModel.getValueAt(i, 1);
						switch (type) {
						case "首页模版":
							titleEle.text(site_name);
							break;
						case "列表页模版":
							titleEle.text(siteColumn_name+"_"+site_name);
							break;
						case "详情页模版":
							titleEle.text(news_title+"_"+site_name);
							break;
						default:
							titleEle.text(site_name);
							break;
						}
					}
					
					Global.templateMap.put(temp.getFile().getName(), temp);
				}
			}
		}
	}
	
	/**
	 * 获取当前操作的模版，保存路径的模版名，将要保存到文件夹的名称
	 * @return
	 */
	public static String getTemplatePathName(){
		String uiTN = Global.mainUI.getTextField_templateName().getText();
		if(uiTN == null || uiTN.length() == 0){
			try {
				uiTN = "template_"+DateUtil.dateFormat(DateUtil.timeForUnix10(), "yyyy_MM_dd__hh_mm");
			} catch (NotReturnValueException e) {
				e.printStackTrace();
			}
		}
		return uiTN;
	}
	
	/**
	 * 显示当前最新的模版变量，将Global中存的模版变量显示到JTable中。也可以作为刷新使用
	 */
	public static void showUITemplateVarJTabel(){
		Global.mainUI.getTemplateVarTableModel().setRowCount( 0 );
		
		for (Map.Entry<String, ElementDiffRecord> entry : Global.templateVarMap.entrySet()) {
			ElementDiffRecord record = entry.getValue();
			Vector rowData = new Vector();
			int[] diff = Action.isPreviewDiff(record.getElementDiffListVO());
			rowData.add(entry.getKey());
			rowData.add(diff[0]);
			rowData.add(diff[1]);
			rowData.add("删除");
			rowData.add(diff[1] > 0? "对比":"");
			rowData.add("");
			
			Global.mainUI.getTemplateVarTableModel().insertRow(0, rowData);
		}
		
	}
	
	/**
	 * 第3步，提取模版变量，对提取的模版变量进行过滤，如过滤其字符串长度、相似度文件占的比例
	 */
	public static void templateVarListFilter(){
		//得到模版变量过滤的最长字符数
		int len = Lang.stringToInt(Global.templateVarGainJframe.getVarTextLengthtextField().getText(), 0);
		System.out.println("len -- >"+len);
		
		//相似度百分比，不过这里是整数， 如相似度70%，这里的数字是 70
		int percent = Lang.stringToInt(Global.templateVarGainJframe.getXiangsiduPercentTextField().getText(), 0);
		
		/*
		 * 进行筛选
		 */
		//要删除的列表。 item： map.key
		List<String> removeMapList = new ArrayList<String>();
		//从map中找到要删除的key，放入list
		for (Map.Entry<String, ElementDiffRecord> entry : Global.templateVarMap.entrySet()) {
			ElementDiffRecord record = entry.getValue();
			
			//进行长度筛选
			String html = record.getElementDiffVO().getDiffElement().html();
			System.out.println("---------html---------"+html);
			if(html.length() < len){
				removeMapList.add(entry.getKey());
			}
			
			//进行相似度筛选
			int[] diff = Action.isPreviewDiff(record.getElementDiffListVO());
			int findNum = diff[0]+diff[1];	//发现相同的条数，等于 完全相同的数量+相似度达标的数量
			double baifenbi= Math.ceil(findNum*100/Global.templateMap.size());
			System.out.println("模版变量数目："+Global.templateMap.size()+", 相同条数："+findNum+", 相似度达标的条数，占模版页面总条数的百分比："+baifenbi);
			if(baifenbi < percent){
				//百分比跟模版变量-高级设置中的百分比还小，那么舍弃，删除（过滤掉）这个模版变量
				removeMapList.add(entry.getKey());
				System.out.println("----------删除掉这个模版变量："+record);
			}
			
		}
		//从map中删除
		for (int i = 0; i < removeMapList.size(); i++) {
			Global.templateVarMap.remove(removeMapList.get(i));
		}
		
		Global.log("模版变量提取完毕！");
		showUITemplateVarJTabel();
	}
	
}
