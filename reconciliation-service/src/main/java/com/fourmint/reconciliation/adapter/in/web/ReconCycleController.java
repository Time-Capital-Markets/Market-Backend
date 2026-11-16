package com.fourmint.reconciliation.adapter.in.web;
import com.fourmint.reconciliation.domain.Models.ReconCycle;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/reconciliation/cycles")
public class ReconCycleController extends CrudController<ReconCycle> {
    public ReconCycleController(CrudUseCase<ReconCycle> useCase) { super(useCase); }
}
