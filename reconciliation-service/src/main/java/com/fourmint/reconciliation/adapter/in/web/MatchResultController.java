package com.fourmint.reconciliation.adapter.in.web;
import com.fourmint.reconciliation.domain.Models.MatchResult;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/reconciliation/results")
public class MatchResultController extends CrudController<MatchResult> {
    public MatchResultController(CrudUseCase<MatchResult> useCase) { super(useCase); }
}
