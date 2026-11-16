package com.fourmint.operations.adapter.in.web;
import com.fourmint.operations.domain.Models.BusinessContext;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/operations/business-contexts")
public class BusinessContextController extends CrudController<BusinessContext> {
    public BusinessContextController(CrudUseCase<BusinessContext> useCase) { super(useCase); }
}
