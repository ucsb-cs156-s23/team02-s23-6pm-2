package edu.ucsb.cs156.example.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "game")
public class Game {

  public void updateFrom(Game other) {
    this.name = other.name;
    this.publisher = other.publisher;
    this.rating=other.rating;
}

  @Id
  private String name;
  private String publisher;
  private String rating;
}
