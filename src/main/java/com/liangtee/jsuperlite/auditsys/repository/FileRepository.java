package com.liangtee.jsuperlite.auditsys.repository;

import com.liangtee.jsuperlite.auditsys.model.FileInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/5/29.
 * All rights Reserved
 */

public interface FileRepository extends CrudRepository<FileInfo, String> {


}
