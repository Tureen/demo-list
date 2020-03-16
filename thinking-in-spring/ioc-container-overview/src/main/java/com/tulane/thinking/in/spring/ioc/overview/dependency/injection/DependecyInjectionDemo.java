package com.tulane.thinking.in.spring.ioc.overview.dependency.injection;

import com.tulane.thinking.in.spring.ioc.overview.repository.UserRepository;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;

/**
 * 依赖查找示例
 * 1. 通过名称的方式来查找
 */
public class DependecyInjectionDemo {

    public static void main(String[] args) {
        useApplicationContext();
        useRealBeanFactory();
    }

    private static void useApplicationContext() {
        System.out.println("start:useApplicationContext");

        // 配置 XML 配置文件
        // 启动 Spring 应用上下文
//        BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:/META-INF/dependecy-injection-context.xml");

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/META-INF/dependecy-injection-context.xml");

        // 依赖来源一: 自定义 Bean
        UserRepository userRepository = applicationContext.getBean("userRepository", UserRepository.class);
//        System.out.println(userRepository.getUsers());

        // 依赖来源二: 依赖注入 (内建依赖)
        System.out.println(userRepository.getBeanFactory());

        ObjectFactory userFactory = userRepository.getObjectFactory();
        System.out.println(userFactory.getObject() == applicationContext);

        // 依赖来源三: 容器内建 Bean
        Environment environment = applicationContext.getBean(Environment.class);
        System.out.println("获取 Environment 类型的 Bean: " + environment);

        // 依赖查找 (错误)
//        System.out.println(beanFactory.getBean(BeanFactory.class));

        whoIsIoCContainer(userRepository, applicationContext);
    }

    private static void useRealBeanFactory(){
        System.out.println("start:useRealBeanFactory");

        AbstractRefreshableApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/META-INF/dependecy-injection-context.xml");
        BeanFactory beanFactory = applicationContext.getBeanFactory();
        UserRepository userRepository = applicationContext.getBean("userRepository", UserRepository.class);

        whoIsIoCContainer(userRepository, beanFactory);

    }

    private static void whoIsIoCContainer(UserRepository userRepository, BeanFactory beanFactory){

        // ConfigurableApplicationContext <- ApplicationContext <- BeanFactory

        // ConfigurableApplicationContext#getBeanFactory()

        // 这个表达式为什么不会成立
        System.out.println(userRepository.getBeanFactory() == beanFactory);

        // ApplicationContext is BeanFactory
    }
}
