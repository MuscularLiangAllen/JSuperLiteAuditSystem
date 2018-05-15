package com.liangtee.jsuperlite.auditsys.utils.wordtemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.liangtee.jsuperlite.auditsys.utils.wordtemplate.XWPFHandler.XWPFParagraphHandler;
import com.liangtee.jsuperlite.auditsys.utils.wordtemplate.XWPFHandler.XWPFTableHandler;
import org.apache.poi.xwpf.usermodel.*;


/**
 * 仅支持对docx文件的文本及表格中的内容进行替换
 * 模板仅支持 ${key} 标签
 *
 */
public class WordTemplate {
	
	private XWPFDocument document;
	
	/**
	 * 初始化模板内容
	 * @param inputStream 模板的读取流(docx文件)
	 * @throws IOException
	 */
	public WordTemplate(InputStream inputStream) throws IOException{
		document = new XWPFDocument(inputStream);
	}
	
	/**
	 * 替换模板中的标签为实际的内容
	 * @param map 
	 */
	public void replaceTag(Map<String, String> map){
		replaceParagraphs(map);
//		replaceBreakLine();
		replaceTables(map);
//		replaceTableBreakLine();
	}
	
	/**
	 * 将处理后的内容写入到输出流中
	 * @param outputStream 输出流
	 * @throws IOException
	 */
	public void write(OutputStream outputStream) throws IOException{
		document.write(outputStream);
	}
	
	/**
	 * 替换文本中的标签
	 * @param map key(待替换标签)-value(文本内容)
	 */
	private void replaceParagraphs(Map<String, String> map){
		List<XWPFParagraph> allXWPFParagraphs = document.getParagraphs();
		for (XWPFParagraph xwpfParagraph : allXWPFParagraphs) {
			XWPFParagraphHandler xwpfParagraphUtils = new XWPFParagraphHandler(xwpfParagraph);
			xwpfParagraphUtils.replaceAll(map);
		}
	}
	
	/**
	 * 替换表格中的标签 
	 * @param map key(待替换标签)-value(文本内容)
	 */
	private void replaceTables(Map<String, String> map){
		List<XWPFTable> xwpfTables = document.getTables();
		for(XWPFTable xwpfTable : xwpfTables){
			XWPFTableHandler xwpfTableUtils = new XWPFTableHandler(xwpfTable);
			xwpfTableUtils.replace(map);
		}
	}

	private void replaceBreakLine() {
		List<XWPFParagraph> allXWPFParagraphs = document.getParagraphs();
		for (XWPFParagraph xwpfParagraph : allXWPFParagraphs) {
			if(xwpfParagraph.getParagraphText().contains("<wbr>")) {
				List<XWPFRun> xwpfRunList = xwpfParagraph.getRuns();
				for(int i=0; i<xwpfRunList.size(); i++) {
					if(xwpfRunList.get(i).text().contains("<wbr>")) {
						XWPFRun xRun = xwpfRunList.get(i);
						xRun.setText(xRun.text().replace("<wbr>", ""));
						xRun.addCarriageReturn();
						xwpfRunList.set(i, xwpfRunList.set(i, xRun));
					}
				}
			}
		}
	}

	private void replaceTableBreakLine() {
		List<XWPFTable> xwpfTables = document.getTables();
		for(XWPFTable xwpfTable : xwpfTables){
			System.out.println("table " + xwpfTable.getText());
			if(xwpfTable.getText().contains("<wbr>")) {
				List<XWPFTableRow> xwpfTableRowList = xwpfTable.getRows();
				for(int i=0; i<xwpfTableRowList.size(); i++) {
					List<XWPFTableCell> xwpfTableCellList = xwpfTableRowList.get(i).getTableCells();
					for(int j=0; j<xwpfTableCellList.size(); j++) {
						if(xwpfTableCellList.get(j).getText().contains("<wbr>")) {
							for(XWPFParagraph xwpfParagraph : xwpfTableCellList.get(j).getParagraphs()) {
								if(xwpfParagraph.getText().contains("<wbr>")) {
									List<XWPFRun> xwpfRunList = xwpfParagraph.getRuns();
									for(int k=0; k<xwpfRunList.size(); k++) {
										if(xwpfRunList.get(k).text().contains("<wbr>")) {
											XWPFRun xRun = xwpfRunList.get(k);
											xRun.setText(xRun.text().replace("<wbr>", ""));
											xRun.addCarriageReturn();
											xwpfRunList.set(k, xwpfRunList.set(k, xRun));
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}


}
