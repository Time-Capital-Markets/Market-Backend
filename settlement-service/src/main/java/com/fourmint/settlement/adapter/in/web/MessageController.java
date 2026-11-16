package com.fourmint.settlement.adapter.in.web;
import com.fourmint.settlement.domain.Models.Message;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/settlement/messages")
public class MessageController extends CrudController<Message> {
    public MessageController(CrudUseCase<Message> useCase) { super(useCase); }
}
