package com.fourmint.settlement.adapter.in.web;
import com.fourmint.settlement.domain.Models.Transfer;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/settlement/transfers")
public class TransferController extends CrudController<Transfer> {
    public TransferController(CrudUseCase<Transfer> useCase) { super(useCase); }
}
