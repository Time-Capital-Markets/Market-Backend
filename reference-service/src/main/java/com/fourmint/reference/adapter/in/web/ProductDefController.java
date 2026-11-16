package com.fourmint.reference.adapter.in.web;
import com.fourmint.reference.domain.Models.ProductDef;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/reference/products")
public class ProductDefController extends CrudController<ProductDef> {
    public ProductDefController(CrudUseCase<ProductDef> useCase) { super(useCase); }
}
