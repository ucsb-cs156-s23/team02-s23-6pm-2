package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.Game;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.GameRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
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

@Api(description = "Games")
@RequestMapping("/api/games")
@RestController
public class GameController extends ApiController {

    @Autowired
    GameRepository gameRepository;

    @ApiOperation(value = "List all games")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<Game> allGames() {
        Iterable<Game> games = gameRepository.findAll();
        return games;
    }

    @ApiOperation(value = "Get a single game")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public Game getById(
            @ApiParam("name") @RequestParam String name) {
        Game game = gameRepository.findById(name)
                .orElseThrow(() -> new EntityNotFoundException(Game.class, name));

        return game;
    }

    @ApiOperation(value = "Create a new game")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public Game postGame(
            @ApiParam("name") @RequestParam String name,
            @ApiParam("publisher") @RequestParam String publisher,
            @ApiParam("rating") @RequestParam String rating)
            throws JsonProcessingException {

        Game game = new Game();
        game.setName(name);
        game.setBreed(publisher);
        game.setGender(rating);

        Game savedGame = gameRepository.save(game);

        return savedGame;
    }

    @ApiOperation(value = "Delete a Game")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteGame(
            @ApiParam("name") @RequestParam String name) {
        Game game = gameRepository.findById(name)
                .orElseThrow(() -> new EntityNotFoundException(Game.class, name));

        gameRepository.delete(game);
        return genericMessage("Game with id %s deleted".formatted(name));
    }

    @ApiOperation(value = "Update a single Game")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public Game updateGame(
            @ApiParam("name") @RequestParam String name,
            @RequestBody @Valid Game incoming) {

        Game game = gameRepository.findById(name)
                .orElseThrow(() -> new EntityNotFoundException(Game.class, name));

                game.updateFrom(incoming);

        gameRepository.save(game);

        return game;
    }
}
