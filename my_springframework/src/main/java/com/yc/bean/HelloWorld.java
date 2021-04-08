package com.yc.bean;

import com.yc.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@MyComponent
public class HelloWorld {

    private HelloWorld hello;

    @MyPostConstruct
    public void setup(){
        System.out.println("MyPostConstruct构造方法执行");

    }


    @MyPreDestroy
    public void destory(){
        System.out.println("MyPreDestroy构造方法执行");
    }

    @MyAutowired
    public void setHello(HelloWorld hello){
        System.out.println("@MyAutowired注解起效");
        this.hello=hello;
    }

    @MyResource(name = "hw")
    public void setHello2(HelloWorld hello){
        System.out.println("@MyResource注解起效");
        this.hello=hello;
    }

    public HelloWorld() {

        System.out.println("hello world 构造");
    }

    public void show(){
        System.out.println("show");
    }


}
