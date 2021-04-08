package com.yc.biz;

import com.yc.AppConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
//指定在单元测试启动的时候创建spring的工厂类对象
@ContextConfiguration(classes =AppConfig.class )
//RunWith的value属性指定以spring test的SpringJUnit4ClassRunner作为启动类
//如果不指定启动类，默认启用的junit中的默认启动类
@RunWith(value = SpringJUnit4ClassRunner.class)
public class StudentBizImplTest {

//    //spring容器(基于接口)
//    private ApplicationContext ac;

    public StudentBizImplTest(){
        System.out.println("StudentBizImplTest构造方法");
    }


    @Autowired
    private StudentBizImpl studentBiz;

//    @Autowired
//    private StudentBizImpl studentBi;

//    @Before
//    public void setUp(){
//        //AnnotationConfigApplicationContext基于注解的配置容器类
//        ac=new AnnotationConfigApplicationContext(AppConfig.class);
//        //读取AppConfig.class-->basePackages="com.yc"-->得到要扫描的路径
//        //要检查这些包中的是否有@Component注解，如有则实例化
//        //存到一个map<String,Object>    键名--->类名小写
//        studentBiz= (StudentBizImpl) ac.getBean("studentBizImpl");
//
//
//    }



    @Test
    public void add() {
        studentBiz.add("李四");
        System.out.println("aaaaaaaaaa");
    }

    @Test
    public void update() {
        studentBiz.update("李四");
    }
}