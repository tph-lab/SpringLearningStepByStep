package com.yc.biz;

import com.yc.AppConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

public class HelloWorldTest {

    //spring容器(基于接口)
    private ApplicationContext ac;

    @Before
    public void setUp(){
        //AnnotationConfigApplicationContext基于注解的配置容器类
        ac=new AnnotationConfigApplicationContext(AppConfig.class);
        //读取AppConfig.class-->basePackages="com.yc"-->得到要扫描的路径
        //要检查这些包中的是否有@Component注解，如有则实例化
        //存到一个map<String,Object>    键名--->类名小写
    }

    @Test
    public void hello() {
        HelloWorld hw=(HelloWorld) ac.getBean("helloWorld");
        hw.hello();
        HelloWorld hw2= (HelloWorld) ac.getBean("helloWorld");
        hw2.hello();
    }
}