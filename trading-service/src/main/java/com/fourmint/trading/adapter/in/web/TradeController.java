package com.fourmint.trading.adapter.in.web;
import com.fourmint.trading.domain.Models.Trade;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/trading/trades")
public class TradeController extends CrudController<Trade> {
    public TradeController(CrudUseCase<Trade> useCase) { super(useCase); }
}
