package ru.mtuci.DubovikovIV.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.DubovikovIV.model.Details;

@Repository
public interface DetailsRepository extends JpaRepository<Details, Long> {
}
