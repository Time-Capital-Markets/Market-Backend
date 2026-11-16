package com.fourmint.reconciliation.adapter.in.web;
import com.fourmint.reconciliation.domain.Models.ReconEventRecord;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/reconciliation/events")
public class ReconEventRecordController extends CrudController<ReconEventRecord> {
    public ReconEventRecordController(CrudUseCase<ReconEventRecord> useCase) { super(useCase); }
}
