package com.jacob;

import com.jacob.framework.JacobApplicationContext;
import com.jacob.service.UserService;

public class Test {
    public static void main(String[] args) {
        //启动--扫描--创建bean（非懒加载的单例bean）
        //原型bean/懒加载bean  每次get时创建
        JacobApplicationContext applicationContext = new JacobApplicationContext(AppConfig.class);

//        System.out.println(applicationContext.getBean("userService"));
//        System.out.println(applicationContext.getBean("userService"));

        UserService userService = (UserService) applicationContext.getBean("userService");
        userService.test();
    }
}
