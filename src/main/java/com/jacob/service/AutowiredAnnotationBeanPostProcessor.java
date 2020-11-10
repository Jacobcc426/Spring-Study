package com.jacob.service;

import com.jacob.framework.BeanPostProcessor;
import com.jacob.framework.Component;

@Component
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {
    @Override
    public void autowired() {
        System.out.println("处理Autowired注解");
    }

    Object postProcessBeforeInitizlization(){
        return  null;
    }
    Object postProcessAfterInitizlization(){
        return  null;
    }
}
