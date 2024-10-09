package ru.mtuci.Dubovikov.service.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.Dubovikov.model.License;
import ru.mtuci.Dubovikov.repository.DemoRepository;
import ru.mtuci.Dubovikov.repository.DetailsRepository;
import ru.mtuci.Dubovikov.service.DemoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DemoServiceImpl implements DemoService {

    private final DemoRepository demoRepository;
    private final DetailsRepository detailsRepository;

    @Override
    public void save(License demo) {
        demo.getDetails().forEach(details -> {
            details.setDemo(demo);
            detailsRepository.save(details);
        });
        demoRepository.save(demo);
    }

    @Override
    public List<License> findAll() {
        return demoRepository.findAll();
    }

    @Override
    public License findById(long id) {
        return demoRepository.findById(id).orElse(new License());
    }
}
