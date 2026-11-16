package com.fourmint.marketdata.adapter.in.web;
import com.fourmint.marketdata.domain.Models.QuoteSeries;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/market-data/quotes")
public class QuoteSeriesController extends CrudController<QuoteSeries> {
    public QuoteSeriesController(CrudUseCase<QuoteSeries> useCase) { super(useCase); }
}
