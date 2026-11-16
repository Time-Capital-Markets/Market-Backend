package com.fourmint.reference.adapter.in.web;
import com.fourmint.reference.domain.Models.HolidayCalendar;
import com.fourmint.platform.port.in.CrudUseCase;
import com.fourmint.platform.spring.CrudController;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/reference/calendars")
public class HolidayCalendarController extends CrudController<HolidayCalendar> {
    public HolidayCalendarController(CrudUseCase<HolidayCalendar> useCase) { super(useCase); }
}
