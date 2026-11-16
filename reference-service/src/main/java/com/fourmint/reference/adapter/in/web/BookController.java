package com.fourmint.reference.adapter.in.web;
import com.fourmint.reference.domain.Models.Book;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/reference/books")
public class BookController extends CrudController<Book> {
    public BookController(CrudUseCase<Book> useCase) { super(useCase); }
}
