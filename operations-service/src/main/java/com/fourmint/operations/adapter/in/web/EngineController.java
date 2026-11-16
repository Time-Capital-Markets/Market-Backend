package com.fourmint.operations.adapter.in.web;
import com.fourmint.operations.domain.Models.Engine;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/operations/engines")
public class EngineController extends CrudController<Engine> {
    public EngineController(CrudUseCase<Engine> useCase) { super(useCase); }
}
