package ru.mtuci.Dubovikov.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.Dubovikov.model.License;
import ru.mtuci.Dubovikov.model.Details;
import ru.mtuci.Dubovikov.repository.DetailsRepository;
import ru.mtuci.Dubovikov.service.DemoService;

@RestController
@RequestMapping("/details")
@RequiredArgsConstructor
public class DetailsController {

    private final DetailsRepository detailsRepository;
    private final DemoService demoService;

    @PostMapping("/{License_id}/save")
    public void save(@PathVariable(value = "License_id") Long demoId,
                     @RequestBody Details details) {
        License demo = demoService.findById(demoId);
        details.setDemo(demo);
        detailsRepository.save(details);
    }
}
