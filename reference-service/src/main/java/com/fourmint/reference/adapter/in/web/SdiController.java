package com.fourmint.reference.adapter.in.web;
import com.fourmint.reference.domain.Models.Sdi;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/reference/sdis")
public class SdiController extends CrudController<Sdi> {
    public SdiController(CrudUseCase<Sdi> useCase) { super(useCase); }
}
