package com.netas.jtapi;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ComponentScan(basePackages={"com.netas.jtapi.*"})
@PropertySources(
		@PropertySource("classpath:application.properties")
		)
public class ForwardOnBusyConfig {

}
