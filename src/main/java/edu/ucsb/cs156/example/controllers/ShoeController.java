package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.Shoe;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.ShoeRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.time.LocalDateTime;

@Api(description = "Shoe") //literally just a description
@RequestMapping("/api/shoes") //define sURL mapping for backend end points
@RestController
@Slf4j
public class ShoeController extends ApiController {

    @Autowired
    ShoeRepository shoeRepository;

    @ApiOperation(value = "List all shoes")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<Shoe> allShoes() { 
    // ^the Shoe inside angled brackets <> refers to the Entity class;
    // line below defines var of type Shoe entity, with Iterable interface acting on it
        Iterable<Shoe> shoes = shoeRepository.findAll();
        //note: the above line works despite ShoeRepository class being empty
        //because ShoeRepoistory is inheriting from CrudRepository, which defines .findAll()
        return shoes;
    }

    @ApiOperation(value = "Get a single shoe")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public Shoe getById(
            @ApiParam("id") @RequestParam Long id) {
        Shoe shoe = shoeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Shoe.class, id));

        return shoe;
    }

    @ApiOperation(value = "Create a new shoe")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public Shoe postShoe(
            @ApiParam("name") @RequestParam String name,
            @ApiParam("color") @RequestParam String color,
            @ApiParam("brand") @RequestParam String brand
            )
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        //log.info("localDateTime={}", localDateTime);

        Shoe shoe = new Shoe();
        shoe.setName(name);
        shoe.setColor(color);
        shoe.setBrand(brand);

        Shoe savedShoe = shoeRepository.save(shoe);
        return savedShoe;
    }


    @ApiOperation(value = "Delete a Shoe")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteShoe(
            @ApiParam("id") @RequestParam Long id) {
        Shoe shoe = shoeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Shoe.class, id));

        shoeRepository.delete(shoe);
        return genericMessage("Shoe with id %s deleted".formatted(id));
    }

    @ApiOperation(value = "Update a single shoe")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public Shoe updateShoe(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid Shoe incoming) {

        Shoe shoe = shoeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Shoe.class, id));

        shoe.setName(incoming.getName());
        shoe.setColor(incoming.getColor());
        shoe.setBrand(incoming.getBrand());

        shoeRepository.save(shoe);

        return shoe;
    }
}