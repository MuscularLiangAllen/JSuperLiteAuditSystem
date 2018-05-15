package com.liangtee.jsuperlite.auditsys;

import com.liangtee.jsuperlite.auditsys.utils.HTML2PDF;
import com.liangtee.jsuperlite.auditsys.utils.Office2HTML;
import com.liangtee.jsuperlite.auditsys.utils.PDFUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JSuperLiteAuditSystemApplicationTests {

	@Test
	public void split() {
		System.out.println("xxx.docx".split(".").length);
		System.out.println("xxx.docx".split("\\.")[0]);
	}


	@Test
	public void testWrod2Pdf() {
		try {

			PDFUtils.convertExcelToPDF("C:\\Users\\Allen\\Desktop\\教师用书预订表--梁天一.xls", "C:\\Users\\Allen\\JSuperLiteFS\\workplace\\20170730测试项目-篮球馆改造\\教师用书预订表222.pdf");

//			PDFUtils.convertDocxToPDF("C:\\Users\\Allen\\Desktop\\test.doc", "C:\\Users\\Allen\\JSuperLiteFS\\workplace\\20170730测试项目-篮球馆改造\\学期.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Test
	public void testHtml2Pdf() {
		try {
			HTML2PDF.parseHtml2Pdf("C:\\Users\\Allen\\JSuperLiteFS\\workplace\\20170730测试项目-篮球馆改造\\aaa.xlsx\\test.html",
					"C:\\Users\\Allen\\JSuperLiteFS\\workplace\\20170730测试项目-篮球馆改造\\pdftest222.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testWord07ToHtml() {
		try {
			Office2HTML.word07ToHtml("C:\\Users\\Allen\\Desktop\\学期.docx", "C:\\Users\\Allen\\JSuperLiteFS\\workplace\\20170730测试项目-篮球馆改造", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testExcel07ToHtml() {
		try {
			Office2HTML.excel07ToHtml("C:\\Users\\Allen\\Desktop\\test.xlsx", "C:\\Users\\Allen\\JSuperLiteFS\\workplace\\20170730测试项目-篮球馆改造\\aaa.xlsx", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testWord2Html() {
		try {
			Office2HTML.word03ToHtml("C:\\Users\\Allen\\Desktop\\test.doc", "C:\\Users\\Allen\\JSuperLiteFS\\workplace\\20170730测试项目-篮球馆改造", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testExcel2Html() {
		try {
			Office2HTML.excel03ToHtml("C:\\Users\\Allen\\Desktop\\教师用书预订表--梁天一.xls", "C:\\Users\\Allen\\JSuperLiteFS\\workplace\\20170730测试项目-篮球馆改造", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Test
	public void contextLoads() {
	}

}
