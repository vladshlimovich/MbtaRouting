package com.example.vlad.basic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

/**
 * @author vshlimovich
 * The very basic spring-boot application.  Potentially the starting point of any application
 * Was initially created with spring-boot CLI:
 * spring init -g com.example.vlad -a newApp -v 0.0.1-SNAPSHOT -b 2.1.1.RELEASE --dependencies actuator,web --package-name com.example.vlad.basic NewApp
 *
 * supports static HTML and actuator
 */
@SpringBootApplication
@ComponentScan(basePackages={
	"com.example.vlad.transit",
	"com.example.vlad.basic"
})
public class Application {
	private static final Logger LOG = LogManager.getLogger(Application.class);

	public static void main(String[] args) {
		final long startTime = System.currentTimeMillis();
		final ApplicationContext ctx = SpringApplication.run(Application.class, args);
		if (LOG.isDebugEnabled()) {
			final String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				LOG.debug(beanName);
			}
		}
		final Application application = ctx.getBean(Application.class);
		LOG.info("main(...) method completed in " + (System.currentTimeMillis() - startTime) + " ms.");
	}
}
