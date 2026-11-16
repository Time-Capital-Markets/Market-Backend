package com.fourmint.marketdata.adapter.in.web;
import com.fourmint.marketdata.domain.Models.MarketCurve;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/market-data/curves")
public class MarketCurveController extends CrudController<MarketCurve> {
    public MarketCurveController(CrudUseCase<MarketCurve> useCase) { super(useCase); }
}
