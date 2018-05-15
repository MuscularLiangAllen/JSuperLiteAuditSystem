package com.liangtee.jsuperlite.auditsys.model;

import javax.persistence.*;

import com.alibaba.fastjson.annotation.JSONField;
import com.liangtee.jsuperlite.auditsys.utils.TimeFormater;
import com.liangtee.jsuperlite.auditsys.values.UserConfs;

/**
 * author: liangtee 梁天一
 *
 * all rights reserved
 *
 */

@Entity
@Table(name = "T_USER")
public class User {

//	@JSONField(name="id")
	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "UID")
	private long UID;

//	@JSONField(name="name")
	@Column(name = "USER_NAME", nullable = false, unique = true)       
	private String name;

//	@JSONField(serialize=false)
	@Column(name = "PASSWD", nullable = false, unique = false)   
	private String passwd;

//	@JSONField(serialize=false)
	@Column(name = "EMAIL", unique = true)
	private String EMail;

//	@JSONField(serialize=false)
	@Column(name = "PHONE_NUMBER", nullable = false)
	private String phoneNumber;

//	@JSONField(serialize=false)
	@Column(name = "USER_TYPE", unique = false)
	private int userType;

//	@JSONField(name="rolename")
	@Column(name = "USER_TYPE_NAME", unique = false)
	private String userTypeName;

//	@JSONField(name="pid")
	@Column(name = "DEPT_ID",  unique = false)
	private int deptID;

//	@JSONField(serialize=false)
	@Column(name = "CREATE_TIME", unique = false)
	private String createTime;

//	@JSONField(serialize=false)
	@Column(name = "LAST_ACTIVE_TIME", unique = false)
	private String lastActiveTime;
	
	/**
	 * The account is disabled if the property "disabled" = 1
	 */
	@Column(name = "IS_ACTIVE", unique = false)
	private boolean isActive;

	@Transient
	public static final int USER_TYPE_ROOT_ADMIN = 12;

	@Transient
	public static final int USER_TYPE_ADMIN = 10;

	@Transient
	public static final int USER_TYPE_VIEWER = 9;

	@Transient
	public static final int USER_TYPE_INTERNAL_AUDITER = 8;

	@Transient
	public static final int USER_TYPE_EXTERNAL_AUDITER = 7;

	@Transient
	public static final int USER_TYPE_DEPT_SENIOR = 6;

	@Transient
	public static final int USER_TYPE_DEPT_STUFF = 5;

	@Transient
	public static final int USER_TYPE_PROJ_CONTROCTOR = 4;

	protected User() {
		// TODO Auto-generated constructor stub
	}
	
	public User (String name, String passwd, int deptID, int userType, String email, String phoneNumber, boolean isActive) {
		this.UID = Long.parseLong(TimeFormater.format("yyMMddHHmmss"));
		this.name = name;
		this.passwd = passwd;
		this.deptID = deptID;
		this.userType = userType;
		this.userTypeName = UserConfs.getRoleDesc(userType);
		this.EMail = email;
		this.phoneNumber = phoneNumber;
		this.isActive = isActive;
		this.createTime = TimeFormater.format("yyyy/MM/dd");
	}

	public User (Long userID, String name, String passwd, int deptID, int userType, String email, String phoneNumber, boolean isActive) {
		this.UID = userID;
		this.name = name;
		this.passwd = passwd;
		this.deptID = deptID;
		this.userType = userType;
		this.userTypeName = UserConfs.getRoleDesc(userType);
		this.EMail = email;
		this.phoneNumber = phoneNumber;
		this.isActive = isActive;
		this.createTime = TimeFormater.format("yyyy/MM/dd");
	}

	public long getUID() {
		return UID;
	}

	public void setUID(long UID) {
		this.UID = UID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public String getUserTypeName() {
		return userTypeName;
	}

	public void setUserTypeName(String userTypeName) {
		this.userTypeName = userTypeName;
	}

	public String getEMail() {
		return EMail;
	}

	public void setEMail(String eMail) {
		EMail = eMail;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getDeptID() {
		return deptID;
	}

	public void setDeptID(int deptID) {
		this.deptID = deptID;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getLastActiveTime() {
		return lastActiveTime;
	}

	public void setLastActiveTime(String lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}
}
