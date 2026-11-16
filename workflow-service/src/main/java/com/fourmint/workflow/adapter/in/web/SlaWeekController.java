package com.fourmint.workflow.adapter.in.web;
import com.fourmint.workflow.domain.Models.SlaWeek;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/workflow/sla-weeks")
public class SlaWeekController extends CrudController<SlaWeek> {
    public SlaWeekController(CrudUseCase<SlaWeek> useCase) { super(useCase); }
}
