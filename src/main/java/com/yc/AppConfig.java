package com.yc;

import com.yc.biz.StudentBizImpl;
import com.yc.dao.StudentDaoJpaImpl;
import com.yc.dao.StudentDaoMybatisImpl;
import org.springframework.context.annotation.*;

//表示当前的类是一个配置类
@Configuration
//将来要托管的bean扫描的包及子包
@ComponentScan(basePackages = "com.yc")
public class AppConfig {

    @Bean
    public StudentBizImpl studentBizImpl(){
        StudentBizImpl st=new StudentBizImpl();
        return st;
    }
//    @Bean
//   // @DependsOn("eventListenerBean")
//    public StudentDaoJpaImpl eventPublisherBean () {
//        return new StudentDaoJpaImpl();
//    }

    //以方法名为键名，返回值为value
//    @Bean
//    @Lazy
//    public StudentDaoMybatisImpl eventListenerBean () {
//        return new StudentDaoMybatisImpl();
//    }


}
