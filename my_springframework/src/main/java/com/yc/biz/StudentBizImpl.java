package com.yc.biz;

import com.yc.dao.StudentDao;
import com.yc.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@MyService
//@Lazy       //懒加载，除非注入使用了，才会被spring托管    默认eager，即不需要注入就加载
//@Scope("prototype")     singleton默认单例
public class StudentBizImpl {

    private StudentDao studentDao;

    public StudentBizImpl(){
        System.out.println("StudentBizImpl构造方法执行");
    }


    //构造方法后执行
    @MyPostConstruct
    public void PostConstruct(){
        System.out.println("StudentBizImpl的MyPostConstruct方法执行");
    }

    //销毁bean之前执行
    @MyPreDestroy
    public void PreDestroy() {
        System.out.println("PreDestroy方法执行");
    }



    //2.set方式
    //@Inject     //javax中的依赖注入，如果有多个对象，比如这里有StudentDaoJpaImpl和StudentDaoMybatisImpl必须指定   @Named("studentDaoImpl")
   //@Autowired       @Qualifier("studentDaoJpaImpl")
    @MyResource(name="studentDaoJpaImpl")
    public void setStudentDao( StudentDao studentDao){
        this.studentDao=studentDao;
        System.out.println("StudentBizImpl的@MyResource执行");
    }


    @MyAutowired
    public void setStudentDao2( StudentDao studentDao){
        this.studentDao=studentDao;
        System.out.println("StudentBizImpl的@MyAutowired执行");
    }

    public int add(String name){
        System.out.println("============业务层==============");
        System.out.println("用户名是否重名");
        int result=studentDao.add(name);
        System.out.println("============业务层操作结束==========");
        return result;
    }


    public void update(String name){
        System.out.println("============业务层==============");
        System.out.println("用户名是否更新");
        studentDao.update(name);
        System.out.println("============业务层操作结束==========");
    }



}
