package com.richard.dev.common.mvvm;

import androidx.collection.ArrayMap;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * @author: Administrator
 * @createDate: 2022/3/24 11:10
 * @version: 1.0
 * @description: 描述
 */
public class User extends BaseObservable implements Serializable {

    private static final long serialVersionUID = -7768056925079767995L;

    //如果是 public 修饰符，则可以直接在成员变量上方加上 @Bindable 注解
    //如果是 private 修饰符，则在成员变量的 get 方法上添加 @Bindable 注解
    private String name;

    private String job;

//    @JSONField(name = "isSex")
    private boolean isSex;
    private boolean blue;
    private ArrayMap<String, User> map;

    public boolean isSex() {
        return isSex;
    }

    public void setSex(boolean sex) {
        isSex = sex;
    }

    public boolean isBlue() {
        return blue;
    }

    public void setBlue(boolean blue) {
        this.blue = blue;
    }

    public ArrayMap<String, User> getMap() {
        return map;
    }

    public void setMap(ArrayMap<String, User> map) {
        this.map = map;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
//        super.notifyChange();
        super.notifyPropertyChanged(BR.name);
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
