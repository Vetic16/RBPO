package ru.mtuci.DubovikovIV.service;

import ru.mtuci.DubovikovIV.model.Demo;

import java.util.List;

public interface DemoService {
    void save(Demo demo);
    List<Demo> findAll();
    Demo findById(long id);
}
