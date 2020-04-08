package com.egineering.jsteele.s3demo.controller;

import com.egineering.jsteele.s3demo.service.AwsS3AlbumService;
import com.egineering.jsteele.s3demo.service.AwsSecurityTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to process and move uploaded images from the holding area.
 *
 * @author Jason Steele
 */
@RestController
public class ProcessUploadController {
  @Autowired
  private AwsS3AlbumService albumService;

  @PostMapping("/upload")
  public void processUpload(@RequestParam final String file, @RequestParam final String album,
                            @RequestParam final String targetFile) {
    albumService.processUpload(file, album, targetFile);
  }
}
