package com.fourmint.reconciliation.adapter.in.web;
import com.fourmint.reconciliation.domain.Models.Counterparty;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/reconciliation/counterparties")
public class CounterpartyController extends CrudController<Counterparty> {
    public CounterpartyController(CrudUseCase<Counterparty> useCase) { super(useCase); }
}
