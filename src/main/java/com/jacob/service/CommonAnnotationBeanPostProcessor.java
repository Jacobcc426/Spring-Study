package com.jacob.service;

import com.jacob.framework.BeanPostProcessor;
import com.jacob.framework.Component;

@Component
public class CommonAnnotationBeanPostProcessor implements BeanPostProcessor {
    @Override
    public void autowired() {
        System.out.println("处理Resourse注解");
    }
}
