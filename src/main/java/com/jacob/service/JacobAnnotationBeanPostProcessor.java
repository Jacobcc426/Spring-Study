package com.jacob.service;

import com.jacob.framework.BeanPostProcessor;
import com.jacob.framework.Component;

@Component
public class JacobAnnotationBeanPostProcessor implements BeanPostProcessor {
    @Override
    public void autowired() {
        System.out.println("处理Jacob注解");
    }
}
