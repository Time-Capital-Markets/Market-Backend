package com.fourmint.reconciliation.adapter.in.web;
import com.fourmint.reconciliation.domain.Models.InternalSource;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/reconciliation/internal-sources")
public class InternalSourceController extends CrudController<InternalSource> {
    public InternalSourceController(CrudUseCase<InternalSource> useCase) { super(useCase); }
}
