
package com.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.baomidou.mybatisplus.plugins.Page;

/**
 * 查询参数
 */
public class Query<T> extends LinkedHashMap<String, Object> {
	private static final long serialVersionUID = 1L;
    /**
     * mybatis-plus分页参数
     */
    private Page<T> page;
    /**
     * 当前页码
     */
    private int currPage = 1;
    /**
     * 每页条数
     */
    private int limit = 10;

    public Query(JQPageInfo pageInfo) {
    	//分页参数
        if(pageInfo.getPage()!= null){
            currPage = pageInfo.getPage();
        }
        if(pageInfo.getLimit()!= null){
            limit = pageInfo.getLimit();
        }

    
        //防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
        String sidx = SQLFilter.sqlInject(pageInfo.getSidx());
        String order = SQLFilter.sqlInject(pageInfo.getOrder());
        

        //mybatis-plus分页
        this.page = new Page<>(currPage, limit);

        //排序
        if(StringUtils.isNotBlank(sidx) && StringUtils.isNotBlank(order)){
            this.page.setOrderByField(sidx);
            this.page.setAsc("ASC".equalsIgnoreCase(order));
        }
    }
    
    
    public Query(Map<String, Object> params){
        if (params == null) {
            params = new LinkedHashMap<>();
        }
        this.putAll(params);
        if(params!=null) {
            this.putAll(params);
        }
        //分页参数
        if(params.get("page") != null){
            try {
                currPage = Integer.parseInt((String) params.get("page"));
            }catch (NumberFormatException e){
                currPage = 1;
            }
        }
        if(params.get("limit") != null){
            try {
                limit = Integer.parseInt((String) params.get("limit"));
            }catch (NumberFormatException e){
                limit = 10;
            }
        }

        this.put("offset", (currPage - 1) * limit);
        this.put("page", currPage);
        this.put("limit", limit);

        //防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
        String sidx = null;
        String order = null;
        if (params != null) {
            Object sidxObj = params.get("sidx");
            if (sidxObj != null) {
                sidx = SQLFilter.sqlInject((String) sidxObj);
            }
            Object orderObj = params.get("order");
            if (orderObj != null) {
                order = SQLFilter.sqlInject((String) orderObj);
            }
        }
        this.put("sidx", sidx);
        this.put("order", order);

        //mybatis-plus分页
        this.page = new Page<>(currPage, limit);

        //排序
        if(StringUtils.isNotBlank(sidx) && StringUtils.isNotBlank(order)){
            this.page.setOrderByField(sidx);
            this.page.setAsc("ASC".equalsIgnoreCase(order));
        }

    }

    public Page<T> getPage() {
        return page;
    }

    public int getCurrPage() {
        return currPage;
    }

    public int getLimit() {
        return limit;
    }
}
