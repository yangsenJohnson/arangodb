package com.bigdata.springboot.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "用户对象", description = "user2")
public class User  {
  
    @ApiModelProperty(value = "商品信息", required = true)
    private String name;  
    @ApiModelProperty(value = "密码", required = true)  
    private String password;  
  
    @ApiModelProperty(value = "性别")  
    private Integer sex;  
    @ApiModelProperty(value = "密码", required = true)  
    private String token;  
  
    public String getToken() {  
        return token;  
    }  
  
    public void setToken(String token) {  
        this.token = token;  
    }  
  
    public String getName() {  
        return name;  
    }  
  
    public void setName(String name) {  
        this.name = name;  
    }  
  
    public String getPassword() {  
        return password;  
    }  
  
    public void setPassword(String password) {  
        this.password = password;  
    }  
  
    public Integer getSex() {  
        return sex;  
    }  
  
    public void setSex(Integer sex) {  
        this.sex = sex;  
    }  
  
}  