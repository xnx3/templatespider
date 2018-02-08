package com.xnx3.template.vo;

import java.util.List;

import org.jsoup.nodes.Element;

import com.xnx3.BaseVO;


/**
 * {@link Element} 的对比结果,只不过这个是一个列表，将所有模板页面的对比都记录下来
 * @author 管雷鸣
 *
 */
public class ElementDiffListVO extends BaseVO{
	private double diffEqualNum;	//记录有相同行的模板页的相似度的总值
	private int ipentityNum;		//完全相同的个数
	private List<ElementDiffVO> list;

	public List<ElementDiffVO> getList() {
		return list;
	}

	public void setList(List<ElementDiffVO> list) {
		this.list = list;
	}

	public double getDiffEqualNum() {
		return diffEqualNum;
	}

	public void setDiffEqualNum(double diffEqualNum) {
		this.diffEqualNum = diffEqualNum;
	}

	public int getIpentityNum() {
		return ipentityNum;
	}

	public void setIpentityNum(int ipentityNum) {
		this.ipentityNum = ipentityNum;
	}

	@Override
	public String toString() {
		return "ElementDiffListVO [diffEqualNum=" + diffEqualNum
				+ ", ipentityNum=" + ipentityNum + ", list=" + list + "]";
	}
}
