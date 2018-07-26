package com.skaz;

import com.skaz.utils.Apps;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author jungle
 */
@SpringBootApplication(scanBasePackages = App.PACKAGES_TO_SCAN)
public class App implements ApplicationContextAware {

    public static final String PACKAGES_TO_SCAN = "com.skaz";


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Apps.setApplicationContextEnv(applicationContext);
    }
}
