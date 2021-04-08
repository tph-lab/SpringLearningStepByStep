package com.yc.springframework;

import com.yc.bean.HelloWorld;
import com.yc.springframework.stereotype.MyBean;
import com.yc.springframework.stereotype.MyComponentScan;
import com.yc.springframework.stereotype.MyConfiguration;

@MyConfiguration
@MyComponentScan(basePackages={"com.yc.bean","com.yc.biz","com.yc.dao"})
public class MyAppConfig {

//    @MyBean
//    public HelloWorld hw(){ //method.invoke(MyAppConfig对象，xxx)
//        return new HelloWorld();
//    }
//    @MyBean
//    public HelloWorld hw2(){ //method.invoke(MyAppConfig对象，xxx)
//        return new HelloWorld();
//    }

}
