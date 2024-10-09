package ru.mtuci.Dubovikov.controller;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.Dubovikov.model.License;
import ru.mtuci.Dubovikov.service.DemoService;

import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping
    public List<License> findAll() {
        return demoService.findAll();
    }

    @PostMapping("/save")
    public void save(@RequestBody License demo) {
        demoService.save(demo);
    }
}