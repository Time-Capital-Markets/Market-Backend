package com.fourmint.reconciliation.adapter.in.web;
import com.fourmint.reconciliation.domain.Models.PendingFile;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/reconciliation/pending-files")
public class PendingFileController extends CrudController<PendingFile> {
    public PendingFileController(CrudUseCase<PendingFile> useCase) { super(useCase); }
}
