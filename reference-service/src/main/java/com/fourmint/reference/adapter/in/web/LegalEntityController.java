package com.fourmint.reference.adapter.in.web;
import com.fourmint.reference.domain.Models.LegalEntity;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/reference/entities")
public class LegalEntityController extends CrudController<LegalEntity> {
    public LegalEntityController(CrudUseCase<LegalEntity> useCase) { super(useCase); }
}
