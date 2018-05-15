package com.liangtee.jsuperlite.auditsys.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.org.apache.poi.util.IOUtils;

import java.io.*;
import java.util.Iterator;

/**
 * Created by Allen on 2017/8/1.
 */
public class PDFUtils {

    public static void convertExcelToPDF(String srcExcel, String destPdf) throws Exception {
        FileInputStream input_document = new FileInputStream(new File(srcExcel));
        // Read workbook into HSSFWorkbook
        HSSFWorkbook my_xls_workbook = new HSSFWorkbook(input_document);
        // Read worksheet into HSSFSheet
        HSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0);
        // To iterate over the rows
        Iterator<Row> rowIterator = my_worksheet.iterator();
        //We will create output PDF document objects at this point
        Document iText_xls_2_pdf = new Document();
        PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream(destPdf));
        iText_xls_2_pdf.open();
        //we have two columns in the Excel sheet, so we create a PDF table with two columns
        //Note: There are ways to make this dynamic in nature, if you want to.
        PdfPTable my_table = new PdfPTable(2);
        //We will use the object below to dynamically add new data to the table
        PdfPCell table_cell;
        //Loop through rows.
        while(rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while(cellIterator.hasNext()) {
                Cell cell = cellIterator.next(); //Fetch CELL
                switch(cell.getCellType()) { //Identify CELL type
                    //you need to add more code here based on
                    //your requirement / transformations
                    case Cell.CELL_TYPE_STRING:
                        //Push the data from Excel to PDF Cell
                        table_cell=new PdfPCell(new Phrase(cell.getStringCellValue()));
                        //feel free to move the code below to suit to your needs
                        my_table.addCell(table_cell);
                        break;
                }
                //next line
            }

        }
        //Finally add the table to PDF document
        iText_xls_2_pdf.add(my_table);
        iText_xls_2_pdf.close();
        //we created our pdf file..
        input_document.close(); //close xls
    }


//    public static void convertDocxToPDF(String docxPath, String pdfPath) throws Exception {
//        OutputStream os = null;
//        try {
//            WordprocessingMLPackage mlPackage = WordprocessingMLPackage.load(new File(docxPath));
//
//            //Mapper fontMapper = new BestMatchingMapper();
//            Mapper fontMapper = new IdentityPlusMapper();
//            fontMapper.put("华文行楷", PhysicalFonts.get("STXingkai"));
//            fontMapper.put("华文仿宋", PhysicalFonts.get("STFangsong"));
//            fontMapper.put("隶书", PhysicalFonts.get("LiSu"));
//            mlPackage.setFontMapper(fontMapper);
//
//            os = new java.io.FileOutputStream(pdfPath);
//
//            FOSettings foSettings = Docx4J.createFOSettings();
//            foSettings.setWmlPackage(mlPackage);
//            Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);
//
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }finally {
//            IOUtils.closeQuietly(os);
//        }
//    }
}
