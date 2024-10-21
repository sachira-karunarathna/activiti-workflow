package com.sampleapp.samplesbapp.service;

import com.sampleapp.samplesbapp.dto.TaskFormDTO;
import com.sampleapp.samplesbapp.repository.SampleProcessRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@Transactional
public class SampleProcessService {
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private SampleProcessRepository sampleProcessRepository;

    @Autowired
    private RepositoryService repositoryService;

    public List<ProcessDefinition> getDeployedProcesses() {
        return repositoryService.createProcessDefinitionQuery().list();
    }

    @PostConstruct
    public void deployProcesses() {
        // Check if the sample process is already deployed
        long count = repositoryService.createProcessDefinitionQuery().processDefinitionKey("Process_gBajnY0SR").count();

        if (count == 0) {
            // Deploy the sample process if not already deployed
            InputStream bpmnStream = this.getClass().getClassLoader().getResourceAsStream("processes/sampleprocess.bpmn20.xml");
            repositoryService.createDeployment()
                    .addInputStream("sampleprocess.bpmn20.xml", bpmnStream)
                    .deploy();
            System.out.println("Process 'Process_gBajnY0SR' deployed successfully!");
        } else {
            System.out.println("Process 'Process_gBajnY0SR' is already deployed.");
        }

        long count_onboarding = repositoryService.createProcessDefinitionQuery().processDefinitionKey("Process_n3fKSR1ZA").count();

        if (count_onboarding == 0) {
            // Deploy the onboarding process if not already deployed
            InputStream bpmnStream = this.getClass().getClassLoader().getResourceAsStream("processes/onboarding.bpmn20.xml");
            repositoryService.createDeployment()
                    .addInputStream("onboarding.bpmn20.xml", bpmnStream)
                    .deploy();
            System.out.println("Process 'Process_n3fKSR1ZA' deployed successfully!");
        } else {
            System.out.println("Process 'Process_n3fKSR1ZA' is already deployed.");
        }

        long count_sample = repositoryService.createProcessDefinitionQuery().processDefinitionKey("Process_kHjRCI4rv").count();

        if (count_sample == 0) {
            // Deploy the onboarding process if not already deployed
            InputStream bpmnStream = this.getClass().getClassLoader().getResourceAsStream("processes/sampleprocess2.bpmn20.xml");
            repositoryService.createDeployment()
                    .addInputStream("sampleprocess2.bpmn20.xml", bpmnStream)
                    .deploy();
            System.out.println("Process 'Process_kHjRCI4rv' deployed successfully!");
        } else {
            System.out.println("Process 'Process_kHjRCI4rv' is already deployed.");
        }
    }

    public String createForm(TaskFormDTO taskFormDTO) {
        return "Task form created!";
    }

    public ProcessInstance launchSampleProcess(TaskFormDTO taskFormDTO) {
        return runtimeService.startProcessInstanceByKey("Process_gBajnY0SR");
    }

    public String runServiceTask(String value) {
        return "Service task run successfully!";
    }
}
