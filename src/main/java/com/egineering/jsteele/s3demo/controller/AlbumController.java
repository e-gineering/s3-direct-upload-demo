package com.egineering.jsteele.s3demo.controller;

import com.egineering.jsteele.s3demo.dto.Album;
import com.egineering.jsteele.s3demo.dto.CreateAlbumRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * CRUD for albums.
 */
@RestController
public class AlbumController {
  @GetMapping("/albums")
  public List<Album> all() {
    return new ArrayList<>();
  }

  @GetMapping("/albums/{name}")
  public Album get(@PathVariable final String name) {
    return new Album();
  }

  @PostMapping("/albums")
  public Album create(@RequestBody final CreateAlbumRequest album) {
    return new Album();
  }

  @PutMapping("/albums/{name}")
  public Album replace(@RequestBody final Album album) {
    return new Album();
  }

  @DeleteMapping("/albums/{name}")
  void delete(@PathVariable final String name) {
  }
}
