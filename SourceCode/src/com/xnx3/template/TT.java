package com.xnx3.template;

import java.awt.Font;
import java.io.IOException;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xnx3.BaseVO;
import com.xnx3.SystemUtil;
import com.xnx3.UI;
import com.xnx3.file.FileUtil;
import com.xnx3.template.ui.Diff;
import com.xnx3.template.ui.diffItemPanel;
import com.xnx3.template.ui.diffJeditorPanel;
import com.xnx3.template.vo.StringDiffVO;
import com.xnx3.util.Computeclass;
import com.xnx3.util.SimHash;
import com.xnx3.util.StringDiff;
import com.xnx3.util.StringUtil;
import com.xnx3.util.diff.FileDiff;

public class TT {
	public static void main(String[] args) {
		String a = StringUtil.getDomainByUrl("http://www.chaqudao.com/index.php?u=3847");
		System.out.println(a);
	}
	
}
