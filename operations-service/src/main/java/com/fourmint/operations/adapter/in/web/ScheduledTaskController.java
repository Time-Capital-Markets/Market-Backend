package com.fourmint.operations.adapter.in.web;
import com.fourmint.operations.domain.Models.ScheduledTask;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/operations/schedules")
public class ScheduledTaskController extends CrudController<ScheduledTask> {
    public ScheduledTaskController(CrudUseCase<ScheduledTask> useCase) { super(useCase); }
}
