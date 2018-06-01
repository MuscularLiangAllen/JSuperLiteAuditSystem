package com.liangtee.jsuperlite.auditsys.service;

import com.alibaba.fastjson.JSONObject;
import com.liangtee.jsuperlite.auditsys.model.Organization;
import com.liangtee.jsuperlite.auditsys.model.User;
import com.liangtee.jsuperlite.auditsys.repository.UserRepository;
import com.liangtee.jsuperlite.auditsys.service.base.BaseService;
import com.liangtee.jsuperlite.auditsys.service.base.PageModel;
import com.liangtee.jsuperlite.auditsys.utils.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Allen on 2017/4/18.
 */

@Component("userService")
@Transactional
public class UserService extends BaseService<User, Long> {

    private UserRepository userRepository;

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate, UserRepository userRepository) {
        super(jdbcTemplate);
        this.userRepository = userRepository;
    }

    public User get(String userName, String MD5EncodedPasswd) {
        User user = userRepository.findUserByName(userName);
        return user.getPasswd().equals(MD5EncodedPasswd) ? user : null;
    }

    public User get(long UID) {
        return userRepository.findOne(UID);
    }

    public User add(String userName, String MD5EncodedPasswd, int deptID, int userType, String email, String phoneNumber, boolean isActive) {
        if(super.isExist("USER_NAME = ?", userName)) return null;
        return userRepository.save(new User(userName, MD5EncodedPasswd, deptID, userType, email, phoneNumber, isActive));
    }

    public User update(Long userID, String userName, String passwd, int deptID, int userType, String email, String phoneNumber, boolean isActive) {
        User user = userRepository.findOne(userID);
        if(user.getPasswd().equalsIgnoreCase(passwd))
            return userRepository.save(new User(userID, userName, passwd, deptID, userType, email, phoneNumber, isActive));
        return userRepository.save(new User(userID, userName, MD5Encoder.get2RoundsHash(passwd, "MD5"), deptID, userType, email, phoneNumber, isActive));
    }

    public Map<Integer, List<User>> getAll() {

        Map<Integer, List<User>> userMap = new HashMap<Integer, List<User>>();

        Iterator<User> iter = userRepository.findAll().iterator();

        while (iter.hasNext()) {
            User user = iter.next();
            if(userMap.containsKey(user.getDeptID())) userMap.get(user.getDeptID()).add(user);
            else {
                List<User> list = new ArrayList<User>();
                list.add(user);
                userMap.put(user.getDeptID(), list);
            }
        }

        return userMap;
    }

    public Map<Integer, List<User>> groupByDeptID(PageModel pageModel, String sortProperty, int sequence, String conditions, Object ...params) {
        return findByPage(pageModel, sortProperty, sequence, conditions, params).
                stream().collect(Collectors.groupingBy(User::getDeptID));
    }

    public List<Object> getUserTree(PageModel pageModel, List<Organization> organizations, String conditions, Object ...params) {
        List<Object> result = null;
        if(organizations != null) {
            result = new LinkedList<Object>();
            Map<Integer, List<User>> userMap = groupByDeptID(pageModel, "CREATE_TIME", ASC, conditions, params);
            for(Organization org : organizations) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", org.getOrgName());
                jsonObject.put("userTypeName", "");
                jsonObject.put("eMail", "");
                jsonObject.put("phoneNumber", "");
                jsonObject.put("createTime", "");
                jsonObject.put("isActive", "");
                jsonObject.put("type", org.getOrgType());
                jsonObject.put("id", org.getID());
                jsonObject.put("pid", org.getBelongTo() == -1 ? 0 : org.getBelongTo());
                result.add(jsonObject);
                if (userMap.containsKey(org.getID())) {
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("name", org.getOrgName());
//                    jsonObject.put("userTypeName", "");
//                    jsonObject.put("eMail", "");
//                    jsonObject.put("phoneNumber", "");
//                    jsonObject.put("createTime", "");
//                    jsonObject.put("isActive", "");
//                    jsonObject.put("type", org.getOrgType());
//                    jsonObject.put("id", org.getID());
//                    jsonObject.put("pid", org.getBelongTo() == -1 ? 0 : org.getBelongTo());
//                    result.add(jsonObject);
                    result.addAll(userMap.get(org.getID()));
                }
            }
        }

        return result;
    }

    public List<User> findAll() {
        List<User> list = new ArrayList<User>();
        userRepository.findAll().forEach(user -> list.add(user));
        return list;
    }

    public List<User> findByDeptID(int deptID) {
        return userRepository.findUserByDeptID(deptID);
    }

}
