package com.fourmint.reference.adapter.in.web;
import com.fourmint.reference.domain.Models.User;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/reference/users")
public class UserController extends CrudController<User> {
    public UserController(CrudUseCase<User> useCase) { super(useCase); }
}
