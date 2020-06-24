package com.example.demo.modules.generator.dao;

import com.example.demo.modules.generator.entity.Page;
import com.example.demo.modules.generator.entity.Query;

import java.util.List;

/**
 * @Auther: liuc
 * @Date: 18-12-18 14:47
 * @email i@liuchaoboy.com
 * @Description:
 */
public interface BaseMapper<T>{
    /**
     * 新增
     * @param t
     * @return
     */
    int save(T t);

    /**
     * 新增
     * @param query
     * @return
     */
    int save(Query query);

    /**
     * 更新
     * @param t
     * @return
     */
    int update(T t);

    /**
     * 更新
     * @param query
     * @return
     */
    int update(Query query);

    /**
     * 删除
     * @param id
     * @return
     */
    int remove(Object id);

    /**
     * 批量删除
     * @param id
     * @return
     */
    int batchRemove(Object[] id);

    /**
     * 分页查询列表
     * @param page
     * @param query
     * @return
     */
    List<T> listForPage(Page<T> page, Query query);

    /**
     * 根据id查询详情
     * @param id
     * @return
     */
    T getObjectById(Object id);

    /**
     * 查询列表
     * @param query
     * @return
     */
    List<T> list(Query query);

    /**
     * 查询列表
     * @return
     */
    List<T> list();
}
