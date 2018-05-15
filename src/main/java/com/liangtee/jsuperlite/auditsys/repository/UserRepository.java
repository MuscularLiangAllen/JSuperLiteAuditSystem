package com.liangtee.jsuperlite.auditsys.repository;

import com.liangtee.jsuperlite.auditsys.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by Allen on 2017/4/18.
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    public User findUserByName(String usernName);

    public List<User> findUserByDeptID(int deptID);

}
