package com.fourmint.reconciliation.adapter.in.web;
import com.fourmint.reconciliation.domain.Models.SourceRecord;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/reconciliation/records")
public class SourceRecordController extends CrudController<SourceRecord> {
    public SourceRecordController(CrudUseCase<SourceRecord> useCase) { super(useCase); }
}
