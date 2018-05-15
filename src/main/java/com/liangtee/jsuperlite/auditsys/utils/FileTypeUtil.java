package com.liangtee.jsuperlite.auditsys.utils;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/6/23.
 * All rights Reserved
 */
public class FileTypeUtil {

    public static final String WORDX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    public static final String WORD = "application/msword";

    public static final String EXCELX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static final String EXCEL = "application/vnd.ms-excel";

    public static final String PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";

    public static final String PDF = "application/pdf";

    public interface ZIP {
        String type1 = "application/octet-stream";
        String type2 = "application/x-zip-compressed";
    }


    public static final String PNG = "image/png";

    public static final String JPG = "image/jpeg";


    public static String getCommonFileType(String rawType) {
        String commonType = null;

        switch (rawType) {
            case EXCELX:
                commonType = "xlsx";
                break;
            case WORDX:
                commonType = "docx";
                break;
            case WORD:
                commonType = "doc";
                break;
            case PPTX:
                commonType = "pptx";
                break;
            case EXCEL:
                commonType = "xls";
                break;
            case PDF:
                commonType = "pdf";
                break;
            case ZIP.type1:
                commonType = "zip";
                break;
            case ZIP.type2:
                commonType = "zip";
                break;
            case PNG:
                commonType = "png";
                break;
            case JPG:
                commonType = "jpg";
                break;
            default:
                break;
        }

        return commonType;
    }


}
