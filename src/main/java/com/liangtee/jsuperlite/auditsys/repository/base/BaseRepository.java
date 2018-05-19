package com.liangtee.jsuperlite.auditsys.repository.base;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/7/2.
 * All rights Reserved
 */

@NoRepositoryBean
//public interface BaseRepository<T, ID extends Serializable> extends CrudRepository<T, ID>, ElasticsearchRepository<T, ID> {
//}
public interface BaseRepository<T, ID extends Serializable> extends CrudRepository<T, ID>{
}
