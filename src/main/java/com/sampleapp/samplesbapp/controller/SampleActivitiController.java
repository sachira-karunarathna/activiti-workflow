package com.sampleapp.samplesbapp.controller;

import com.sampleapp.samplesbapp.dto.TaskFormDTO;
import com.sampleapp.samplesbapp.service.SampleProcessService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class SampleActivitiController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private SampleProcessService sampleProcessService;

    @PostMapping("/submit-task-form")
    public void submitTaskForm(TaskFormDTO taskFormDTO) {
        String processInstanceId = this.sampleProcessService.launchSampleProcess(taskFormDTO).getId();
        System.out.println("Your process instance ID: " + processInstanceId);
    }

//    @PostMapping("/complete/{taskId}")
//    public String completeTask(@PathVariable String taskId,
//                               @RequestParam Map<String, String> formData,
//                               Model model) {
//        Map<String, Object> variables = new HashMap<>();
//        variables.putAll(formData);
//
//        // Complete the task
//        taskService.complete(taskId, variables);
//        return "Completed";
//    }

}
