package com.xnx3.template.bean;

import org.jsoup.nodes.Element;

import com.xnx3.template.vo.ElementDiffListVO;
import com.xnx3.template.vo.ElementDiffVO;

/**
 * 模板对比的结果记录集。将合适的进行记录
 * @author 管雷鸣
 *
 */
public class ElementDiffRecord {
	//当前对比的结果
	private ElementDiffVO elementDiffVO;
	private ElementDiffListVO elementDiffListVO;
	
	//当前对比的模板元素的，上级元素
//	private Element parentElement;

	public ElementDiffVO getElementDiffVO() {
		return elementDiffVO;
	}

	public void setElementDiffVO(ElementDiffVO elementDiffVO) {
		this.elementDiffVO = elementDiffVO;
	}

	public ElementDiffListVO getElementDiffListVO() {
		return elementDiffListVO;
	}

	public void setElementDiffListVO(ElementDiffListVO elementDiffListVO) {
		this.elementDiffListVO = elementDiffListVO;
	}

	@Override
	public String toString() {
		return "ElementDiffRecord [elementDiffVO=" + elementDiffVO
				+ ", elementDiffListVO=" + elementDiffListVO + "]";
	}

//	public Element getParentElement() {
//		return parentElement;
//	}
//
//	public void setParentElement(Element parentElement) {
//		this.parentElement = parentElement;
//	}
	
	
}
