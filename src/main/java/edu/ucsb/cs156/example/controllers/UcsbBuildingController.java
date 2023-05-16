package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UcsbBuilding;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UcsbBuildingRepository;
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

@Api(description = "UcsbBuilding") //literally just a description
@RequestMapping("/api/ucsbbuildings") //define sURL mapping for backend end points
@RestController
@Slf4j
public class UcsbBuildingController extends ApiController {

    @Autowired
    UcsbBuildingRepository ucsbBuildingRepository;

    @ApiOperation(value = "List all ucsbBuildings")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UcsbBuilding> allUcsbBuildings() { 
    // ^the UcsbBuilding  inside angled brackets <> refers to the Entity class;
    // line below defines var of type UcsbBuilding entity, with Iterable interface acting on it
        Iterable<UcsbBuilding> ucsbBuildings = ucsbBuildingRepository.findAll();
        //note: the above line works despite UcsbBuildingRepository class being empty
        //because UcsbBuildingRepoistory is inheriting from CrudRepository, which defines .findAll()
        return ucsbBuildings;
    }

    @ApiOperation(value = "Get a single ucsbBuilding")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UcsbBuilding getById(
            @ApiParam("id") @RequestParam Long id) {
        UcsbBuilding ucsbBuilding = ucsbBuildingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UcsbBuilding.class, id));

        return ucsbBuilding;
    }

    @ApiOperation(value = "Create a new ucsbBuilding")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UcsbBuilding postUcsbBuilding(
            @ApiParam("name") @RequestParam String name,
            @ApiParam("description") @RequestParam String description,
            @ApiParam("architecture") @RequestParam String architecture,
            @ApiParam("location") @RequestParam String location)
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        //log.info("localDateTime={}", localDateTime);

        UcsbBuilding ucsbBuilding = new UcsbBuilding();
        ucsbBuilding.setName(name);
        ucsbBuilding.setDescription(description);
        ucsbBuilding.setArchitecture(architecture);
        ucsbBuilding.setLocation(location);

        UcsbBuilding savedUcsbBuilding = ucsbBuildingRepository.save(ucsbBuilding);
        return savedUcsbBuilding;
    }








    @ApiOperation(value = "Delete a UcsbBuilding")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteUcsbBuilding(
            @ApiParam("id") @RequestParam Long id) {
        UcsbBuilding ucsbBuilding = ucsbBuildingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UcsbBuilding.class, id));

        ucsbBuildingRepository.delete(ucsbBuilding);
        return genericMessage("UcsbBuilding with id %s deleted".formatted(id));
    }

    @ApiOperation(value = "Update a single ucsbBuilding")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UcsbBuilding updateUcsbBuilding(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid UcsbBuilding incoming) {

        UcsbBuilding ucsbBuilding = ucsbBuildingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UcsbBuilding.class, id));

        ucsbBuilding.setName(incoming.getName());
        ucsbBuilding.setDescription(incoming.getDescription());
        ucsbBuilding.setArchitecture(incoming.getArchitecture());
        ucsbBuilding.setLocation(incoming.getLocation());

        ucsbBuildingRepository.save(ucsbBuilding);

        return ucsbBuilding;
    }
}