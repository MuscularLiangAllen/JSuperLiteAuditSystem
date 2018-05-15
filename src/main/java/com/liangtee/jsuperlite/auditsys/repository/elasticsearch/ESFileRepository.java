package com.liangtee.jsuperlite.auditsys.repository.elasticsearch;

import com.liangtee.jsuperlite.auditsys.model.FileInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by Allen on 2017/7/15.
 */
public interface ESFileRepository extends ElasticsearchRepository<FileInfo, String> {
}
