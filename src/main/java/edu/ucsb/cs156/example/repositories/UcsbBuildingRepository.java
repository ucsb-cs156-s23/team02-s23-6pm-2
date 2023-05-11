package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.UcsbBuilding;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UcsbBuildingRepository extends CrudRepository<UcsbBuilding, Long> {
}