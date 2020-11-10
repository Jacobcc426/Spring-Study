package com.jacob.framework;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JacobApplicationContext {

    private Class configClass;

    private Map<String,BeanDefinition> beanDefinitionMap = new HashMap<>();

    private Map<String,Object> singletonObjects = new HashMap<>();

    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public JacobApplicationContext(Class configClass) {
        this.configClass = configClass;
        //扫描每一个类 得到beanDefinition对象，存入beanDefinitionMap
        Scan(configClass);
        //创建非懒加载单例bean
        createNonLazySingleton();
    }

    //TODO 创建非懒加载单例bean
    private void createNonLazySingleton() {
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if("singleton".equals(beanDefinition.getScope())&&!beanDefinition.isLazy()){
                //创建bean
                Object bean = createBean(beanDefinition,beanName);
                singletonObjects.put(beanName,bean);
            }
        }
    }

    //TODO 创建bean
    private Object createBean(BeanDefinition beanDefinition,String beanName) {
        Class beanClass = beanDefinition.getBeanClass();
        try {
            Object instance = beanClass.getDeclaredConstructor().newInstance();

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.autowired();
            }
            //填充属性
            //AutowiredAnnotationBeanPostProcessor
            //CommonAnnotationBeanPostProcessor <-Resource
            //bean后置处理器

            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }
            //aop

            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO 扫描类
    private void Scan(Class configClass) {
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            //根据ComponentScan获取扫描路径
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = componentScanAnnotation.value();
            path = path.replace(".","/");

            //AppClassLoader
            ClassLoader classLoader = JacobApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);
            //根据path找到文件夹
            File file = new File(resource.getFile());

            for (File f : file.listFiles()){
                //Spring源码 是使用ASM 字节码   判断注解

                String s = f.getAbsolutePath();
                if(s.endsWith(".class")){
                    s = s.substring(s.indexOf("com"),s.indexOf(".class"));
                    s = s.replace("\\",".");

                    //使用AppClassLoader加载class文件
                    Class clazz = null;
                    try {
                        clazz = classLoader.loadClass(s);
                        if (clazz.isAnnotationPresent(Component.class)) {
                            //这是一个Bean
                            if(BeanPostProcessor.class.isAssignableFrom(clazz)){
                                BeanPostProcessor o = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                                beanPostProcessorList.add(o);
                            }

                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setBeanClass(clazz);

                            //Spring中 BeanNameGenerator 生成默认beanName
                            Component componentAnnotation = (Component) clazz.getAnnotation(Component.class);
                            String beanName = componentAnnotation.value();

                            if (clazz.isAnnotationPresent(Lazy.class)){
                                beanDefinition.setLazy(true);
                                //懒加载
                            }
                            if (clazz.isAnnotationPresent(Scope.class)){
                                Scope scopeAnnotation = (Scope) clazz.getAnnotation(Scope.class);
                                String value = scopeAnnotation.value();
                                beanDefinition.setScope(value);
                            }else {
                                beanDefinition.setScope("singleton");
                            }
                            //扫描到的bean生成BeanDefinition，存入BeanDefinitionMap
                            beanDefinitionMap.put(beanName,beanDefinition);

                        }
                    } catch (ClassNotFoundException | NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Object getBean(String beanName){
        //Map
        //Singleton||Prototype
        //需要BeanDefinition
        if (!beanDefinitionMap.containsKey(beanName)){
            throw new NullPointerException();
        }else{
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if("singleton".equals(beanDefinition.getScope())){
                //单例池取bean
                Object o = singletonObjects.get(beanName);
                if(null==o){
                    Object bean = createBean(beanDefinition,beanName);
                    singletonObjects.put(beanName,bean);
                    o=bean;
                }
                return o;
            }else{
                //创建bean
                Object bean = createBean(beanDefinition,beanName);
                return bean;
            }
        }
    }
}
