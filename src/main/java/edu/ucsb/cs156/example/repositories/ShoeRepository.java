package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.Shoe;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ShoeRepository extends CrudRepository<Shoe, Long> {
}