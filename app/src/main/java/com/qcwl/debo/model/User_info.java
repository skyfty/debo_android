/**
  * Copyright 2018 bejson.com 
  */
package com.qcwl.debo.model;

import java.io.Serializable;

/**
 * Auto-generated: 2018-09-18 14:29:28
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class User_info implements Serializable{

    private String user_nickname;
    private String avatar;
    private String sex;
    private String signature;
    private String mobile;
    private String province;
    private String city;
    public void setUser_nickname(String user_nickname) {
         this.user_nickname = user_nickname;
     }
     public String getUser_nickname() {
         return user_nickname;
     }

    public void setAvatar(String avatar) {
         this.avatar = avatar;
     }
     public String getAvatar() {
         return avatar;
     }

    public void setSex(String sex) {
         this.sex = sex;
     }
     public String getSex() {
         return sex;
     }

    public void setSignature(String signature) {
         this.signature = signature;
     }
     public String getSignature() {
         return signature;
     }

    public void setMobile(String mobile) {
         this.mobile = mobile;
     }
     public String getMobile() {
         return mobile;
     }

    public void setProvince(String province) {
         this.province = province;
     }
     public String getProvince() {
         return province;
     }

    public void setCity(String city) {
         this.city = city;
     }
     public String getCity() {
         return city;
     }

}