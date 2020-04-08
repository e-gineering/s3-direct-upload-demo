package com.egineering.jsteele.s3demo.dto;

import lombok.Data;

@Data
public class CreateAlbumRequest {
  private String name;

  public CreateAlbumRequest() {
  }

  public CreateAlbumRequest(final String name) {
    this.name = name;
  }
}
