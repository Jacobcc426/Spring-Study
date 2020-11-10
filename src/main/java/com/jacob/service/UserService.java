package com.jacob.service;

import com.jacob.framework.*;

@Component("userService")
public class UserService implements BeanNameAware, InitializingBean {

    @Autowired
    private OrderService orderService;

    private String beanName;

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void test(){
        System.out.println(orderService);
        System.out.println(beanName);
    }

    @Override
    public void afterPropertiesSet() {
        //检查属性是否符合规则
        if (null==orderService) {

        }
    }
}
