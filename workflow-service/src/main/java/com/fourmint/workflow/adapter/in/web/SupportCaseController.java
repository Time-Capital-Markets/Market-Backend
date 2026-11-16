package com.fourmint.workflow.adapter.in.web;
import com.fourmint.workflow.domain.Models.SupportCase;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/workflow/support-cases")
public class SupportCaseController extends CrudController<SupportCase> {
    public SupportCaseController(CrudUseCase<SupportCase> useCase) { super(useCase); }
}
