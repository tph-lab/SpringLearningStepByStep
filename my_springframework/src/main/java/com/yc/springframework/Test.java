package com.yc.springframework;

import com.yc.bean.HelloWorld;
import com.yc.biz.StudentBizImpl;
import com.yc.springframework.context.MyAnnotationConfigApplicationContext;
import com.yc.springframework.context.MyApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

public class Test {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        MyApplicationContext ac=new MyAnnotationConfigApplicationContext(MyAppConfig.class);
        StudentBizImpl studentBizImpl= (StudentBizImpl) ac.getBean("studentBizImpl");
        studentBizImpl.add("tph");

    }
}
