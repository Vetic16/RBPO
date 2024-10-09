package ru.mtuci.Dubovikov.service;

import ru.mtuci.Dubovikov.model.License;

import java.util.List;

public interface DemoService {
    void save(License demo);
    List<License> findAll();
    License findById(long id);
}
