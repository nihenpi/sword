package com.skaz.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * @author jungle
 */
@Slf4j
@Configuration
@Component
@Lazy(false)
public class Apps implements ApplicationContextAware {

    public static ApplicationContext APPLICATION_CONTEXT;


    private static void printing(ConfigurableEnvironment env) {
        try {
            String protocol = "http";
            if (env.getProperty("server.ssl.key-store") != null) {
                protocol = "https";
            }
            // @formatter:off
            log.info("Access URLs:" +
                            "\n----------------------------------------------------------\n\t"
                            + "Application '{}' on profile '{}' is running .\n\t"
                            + "Local: \t\t{}://127.0.0.1:{}\n\t"
                            + "External: \t{}://{}:{}" +
                            "\n----------------------------------------------------------",
                    env.getProperty("app.name", env.getProperty("spring.application.name", "dev")),
                    Strings.join(env.getActiveProfiles(), ","),
                    protocol, env.getProperty("server.port"),
                    protocol, InetAddress.getLocalHost().getHostAddress(), env.getProperty("server.port")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setApplicationContextEnv(applicationContext);
    }

    public static void setApplicationContextEnv(ApplicationContext applicationContext) {
        if (Apps.APPLICATION_CONTEXT == null) {
            Apps.APPLICATION_CONTEXT = applicationContext;
        }
        setApplicationContextEnv(applicationContext.getEnvironment());
    }

    private static void setApplicationContextEnv(Environment environment) {
        if (Envs.ENV == null) {
            Envs.ENV = environment;
        }
    }

    /**
     * 添加了两个listener,一个传入context，一个打印启动运行信息
     *
     * @param clazz
     * @return
     * @throws Exception
     */
    public static SpringApplicationBuilder newSpringApplicationBuilder(Class<?> clazz) throws Exception {
        return new SpringApplicationBuilder(clazz).listeners(new ApplicationListener<ApplicationPreparedEvent>() {
            @Override
            public void onApplicationEvent(ApplicationPreparedEvent applicationPreparedEvent) {
                ConfigurableApplicationContext applicationContext = applicationPreparedEvent.getApplicationContext();
                setApplicationContextEnv(applicationContext);
            }
        }).listeners(new ApplicationListener<ApplicationReadyEvent>() {
            @Override
            public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
                ApplicationContext applicationContext = applicationReadyEvent.getApplicationContext();
                ConfigurableEnvironment env = (ConfigurableEnvironment) applicationContext.getEnvironment();
                printing(env);
            }
        }).listeners(new ApplicationListener<ApplicationStartingEvent>() {
            @Override
            public void onApplicationEvent(ApplicationStartingEvent applicationStartingEvent) {
                System.out.println("Starting Application ..................... ");
            }
        });
    }
}
