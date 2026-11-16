package com.fourmint.workflow.adapter.in.web;
import com.fourmint.workflow.domain.Models.Task;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/workflow/tasks")
public class TaskController extends CrudController<Task> {
    public TaskController(CrudUseCase<Task> useCase) { super(useCase); }
}
