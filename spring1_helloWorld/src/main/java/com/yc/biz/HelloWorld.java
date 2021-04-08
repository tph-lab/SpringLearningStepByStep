package com.yc.biz;

import org.springframework.stereotype.Component;

//只要加了这个注解，则这个类就会被spring容器创建并托管
@Component
public class HelloWorld {

    public HelloWorld(){
        System.out.println("无参构造方法");
    }


    public void hello(){
        System.out.println("hello world");
    }

}
