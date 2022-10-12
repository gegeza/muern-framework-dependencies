package com.muern.framework.boot.common;

import com.muern.framework.core.common.Json;
import io.mybatis.provider.Entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author gegeza
 * @date 2020-04-20 4:16 PM
 */
public class BaseDTO implements Serializable {

    private static final long serialVersionUID = 100000000000000001L;

    /** 通用搜索 */
    @Entity.Transient
    private String search;

    /** 分页参数-当前页数 */
    @Entity.Transient
    private Integer pageNum = 1;
    /** 分页参数-每页条数 */
    @Entity.Transient
    private Integer pageSize = 10;

    /** 时间参数-开始日期 */
    @Entity.Transient
    private LocalDate beginDate;
    /** 时间参数-结束日期 */
    @Entity.Transient
    private LocalDate endDate;
    /** 时间参数-开始时间 */
    @Entity.Transient
    private LocalDateTime beginDateTime;
    /** 时间参数-结束时间 */
    @Entity.Transient
    private LocalDateTime endDateTime;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getBeginDateTime() {
        return beginDateTime;
    }

    public void setBeginDateTime(LocalDateTime beginDateTime) {
        this.beginDateTime = beginDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    @Override
    public String toString() {
        return Json.tostr(this);
    }
}

