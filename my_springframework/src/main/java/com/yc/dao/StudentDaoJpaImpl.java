package com.yc.dao;

import com.yc.springframework.stereotype.MyRepository;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Repository;

import java.util.Random;

//异常转化：从Exception转为RuntimeException
@MyRepository
//@DependsOn("studentDaoMybatisImpl")
public class StudentDaoJpaImpl implements StudentDao{

    public StudentDaoJpaImpl(){
    System.out.println("StudentDaoJpaImpl构造方法");
}

    @Override
    public int add(String name) {
        System.out.println("jpa添加学生："+name);
        Random r=new Random();
        return r.nextInt();

    }

    @Override
    public void update(String name) {
        System.out.println("jpa更新学生："+name);
    }
}
