package com.liangtee.jsuperlite.auditsys.utils;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Allen on 2017/7/31.
 */
public class HTML2PDF {

    public static void parseHtml2Pdf(String srcHtml, String pdfFile) throws IOException, DocumentException {
        // step 1
        Document document = new Document();
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        // step 3
        document.open();
        // step 4
        XMLWorkerHelper.getInstance().parseXHtml(writer, document,
                new FileInputStream(srcHtml), Charset.forName("UTF-8"));
        // step 5
        document.close();
    }

}
