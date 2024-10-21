package com.sampleapp.samplesbapp.config;

import org.activiti.engine.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivitiConfig {

    @Bean
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    @Bean
    public TaskService taskService(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }

    @Bean
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    @Bean
    public HistoryService historyService(ProcessEngine processEngine) {
        return processEngine.getHistoryService();
    }

    @Bean
    public ProcessEngine processEngine(ProcessEngineConfiguration processEngineConfiguration) {
        return processEngineConfiguration.buildProcessEngine();
    }

    @Bean
    public ProcessEngineConfiguration processEngineConfiguration() {
        return ProcessEngineConfiguration
                .createStandaloneInMemProcessEngineConfiguration()
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
                .setJdbcUrl("jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000")
                .setJdbcDriver("org.h2.Driver")
                .setJdbcUsername("sa")
                .setJdbcPassword("")
                .setAsyncExecutorActivate(false);
    }
}
