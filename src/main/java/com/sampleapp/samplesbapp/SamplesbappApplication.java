package com.sampleapp.samplesbapp;

import org.activiti.engine.ProcessEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SamplesbappApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(SamplesbappApplication.class, args);

		ProcessEngine processEngine = applicationContext.getBean(ProcessEngine.class);

		System.out.println("Init the application!");
	}

}
