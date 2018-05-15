package com.liangtee.jsuperlite.auditsys.service.base;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/7/1.
 * All rights Reserved
 */
public class PageModel {

    private int pageSize;

    private int pageNumber;

    public PageModel(int pageNumber) {
        this.pageSize = 10;
        this.pageNumber = pageNumber;
    }

    public PageModel(int pageSize, int pageNumber) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    public int getStartIndex() {
        return  pageSize*(pageNumber-1);
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
