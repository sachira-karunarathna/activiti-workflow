package com.sampleapp.samplesbapp.controller;

import com.sampleapp.samplesbapp.service.SampleProcessService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ViewController {
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private SampleProcessService sampleProcessService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("name", "Sachira");
        return "index";
    }

    // Endpoint to display deployed processes
    @GetMapping("/deployed-processes")
    public String getDeployedProcesses(Model model) {
        List<ProcessDefinition> processDefinitions = sampleProcessService.getDeployedProcesses();

        model.addAttribute("processDefinitions", processDefinitions);

        return "deployed-processes";
    }

    @GetMapping("/active-processes")
    public String getActiveProcesses(Model model) {
        List<ProcessInstance> activeProcesses = runtimeService.createProcessInstanceQuery().active().list();

        model.addAttribute("activeProcesses", activeProcesses);

        return "active-processes";
    }

    @GetMapping("/completed-processes")
    public String getCompletedProcesses(Model model) {
        List<HistoricProcessInstance> completedProcesses = historyService.createHistoricProcessInstanceQuery().finished().list();

        model.addAttribute("completedProcesses", completedProcesses);

        return "completed-processes";
    }

    @GetMapping("/completed-tasks")
    public String getCompletedTasks(Model model) {
        List<HistoricTaskInstance> completedTasks = historyService.createHistoricTaskInstanceQuery().finished().list();

        model.addAttribute("completedTasks", completedTasks);

        return "completed-tasks";
    }

    @GetMapping("/process-instance/{id}")
    public String getProcessInstance(@PathVariable String id, Model model) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();

        if (processInstance == null) {
            model.addAttribute("errorMessage", "Process Instance with ID '" + id + "' not found.");
            return "error";
        }

        System.out.println("Process Instance ID: " + processInstance.getProcessInstanceId());
        System.out.println("Process Definition ID: " + processInstance.getProcessDefinitionId());
        System.out.println("Process Instance Business Key: " + processInstance.getBusinessKey());
        System.out.println("Process Instance Start Time: " + processInstance.getStartTime());
        System.out.println("Process Instance Process Variables: " + processInstance.getProcessVariables());

        model.addAttribute("processInstance", processInstance);
        return "process-instance";
    }

    @GetMapping("/tasks-by-process/{processInstanceId}")
    public String getTasksByProcessInstanceId(@PathVariable String processInstanceId, Model model) {
        List<Task> tasksByProcess = taskService.createTaskQuery().processInstanceId(processInstanceId).list();

        model.addAttribute("tasksByProcess", tasksByProcess);

        model.addAttribute("processInstanceId", processInstanceId);

        return "tasks-by-process";
    }

    @GetMapping("/process-instance/{id}/tasks")
    public String getTasksByProcessInstance(@PathVariable String id, Model model) {

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(id).singleResult();

        if (processInstance == null) {
            model.addAttribute("errorMessage", "Process Instance with ID '" + id + "' not found.");
            return "error";
        }

        // Fetch current user tasks
        List<Task> currentTasks = taskService.createTaskQuery()
                .processInstanceId(id)
                .list();

        // Fetch historic tasks (including service tasks)
        List<HistoricActivityInstance> historicActivities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(id)
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();

        model.addAttribute("processInstance", processInstance);
        model.addAttribute("currentTasks", currentTasks);
        model.addAttribute("historicActivities", historicActivities);

        return "process-tasks";
    }

    @GetMapping("/process-instance/{id}/elements")
    public String getProcessElements(@PathVariable("id") String processInstanceId, Model model) {
        // Get the process instance
        var proc = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (proc == null) {
            model.addAttribute("errorMessage", "Process Instance with ID '" + processInstanceId + "' not found.");
            return "error"; // Handle error appropriately
        }

        // Get the BPMN model for the process definition
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(proc.getProcessDefinitionId())
                .singleResult();

        if (processDefinition == null) {
            model.addAttribute("errorMessage", "Process Definition not found.");
            return "error"; // Handle error appropriately
        }

        // Get the BPMN model
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        Process process = bpmnModel.getProcesses().get(0);
        Collection<FlowElement> elements = process.getFlowElements();

        // Get tasks for the process instance
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();

        // Create a map to hold the task status
        Map<String, String> taskStatusMap = new HashMap<>();
        for (FlowElement element : elements) {
            String status = "Not Executed"; // Default status
            for (Task task : tasks) {
                if (task.getTaskDefinitionKey().equals(element.getId())) {
                    status = task.getName() + " - " + (task.isSuspended() ? "Completed" : "Active");
                    break;
                }
            }
            taskStatusMap.put(element.getId(), status);
        }

        // Add the elements and their statuses to the model
        model.addAttribute("elements", elements);
        model.addAttribute("taskStatusMap", taskStatusMap);

        return "process-elements"; // Return the view name
    }

    @GetMapping("/task/{taskId}")
    public String getTaskForm(@PathVariable String taskId, Model model) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            model.addAttribute("errorMessage", "Task not found.");
            return "error"; // Handle error appropriately
        }

        model.addAttribute("task", task);
        return "task-form"; // Return the view name
    }

//    // Handle the form submission
//    @GetMapping("/task/complete/{taskId}")
//    public String completeTask(@PathVariable String taskId, Model model) {
//        model.addAttribute("message", "Task completed successfully.");
//
//        return "task-completed";
//    }

//    @PostMapping("/task/complete/{taskId}")
//    public String completeTask(@PathVariable String taskId, @RequestParam("comment") String comment) {
//        // Retrieve the task using the taskId and complete it
//        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//
//        if (task != null) {
//            // Completing the task
//            Map<String, Object> variables = new HashMap<>();
//            variables.put("comment", comment);
//
//            taskService.complete(taskId);
//        }
//
//        return "rtask-completed"; // Redirect after completion
//    }

//    @GetMapping("/user-task")
//    public String getUserTaskForm(Model model) {
//        Task task = taskService.createTaskQuery().taskAssignee("admin").singleResult();
//        if (task != null) {
//            model.addAttribute("taskId", task.getId());
//            model.addAttribute("taskName", task.getName());
//            return "userTaskForm";
//        }
//        return "noTask";
//    }
//
//    // Complete the user task when the form is submitted
//    @PostMapping("/complete-user-task")
//    public String completeTask(String taskId) {
//        taskService.complete(taskId);
//        return "taskCompleted";
//    }

}
