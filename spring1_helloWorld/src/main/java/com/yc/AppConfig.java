package com.yc;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//表示当前的类是一个配置类
@Configuration
//将来要托管的bean扫描的包及子包
@ComponentScan(basePackages = "com.yc")
public class AppConfig {
}
