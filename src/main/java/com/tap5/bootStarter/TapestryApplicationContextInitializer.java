package com.tap5.bootStarter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

public class TapestryApplicationContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    private static final Logger logger = LoggerFactory.getLogger(TapestryApplicationContextInitializer.class);
    private TapestryBeanFactoryPostProcessor tapestryBeanFactoryPostProcessor = null;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (applicationContext instanceof EmbeddedWebApplicationContext) {
            if (tapestryBeanFactoryPostProcessor != null) {
                throw new RuntimeException("T applicationContext already set");
            }
            tapestryBeanFactoryPostProcessor = new TapestryBeanFactoryPostProcessor(
                    (EmbeddedWebApplicationContext) applicationContext);
            applicationContext.addBeanFactoryPostProcessor(tapestryBeanFactoryPostProcessor);
        } else {
            logger.warn("TB: tapestry-spring-boot works only with EmbeddedWebApplicationContext (Supplied context class was"
                    + applicationContext.getClass() + ") delaying initialization");
            return;
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
