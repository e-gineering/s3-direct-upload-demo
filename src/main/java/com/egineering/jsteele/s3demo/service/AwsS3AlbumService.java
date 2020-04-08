package com.egineering.jsteele.s3demo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Wrapper for S3 bucket organized as a set of image albums.
 */
@Service
public class AwsS3AlbumService {
  private static final Logger log = LoggerFactory.getLogger(AwsSecurityTokenService.class);

  @Value("${aws.region:us-east-1}")
  private String awsRegion;
  @Value("${aws.account:612060874604}")
  private String awsAccount;
  @Value("${aws.album.bucket:neighborlink.dev.demo}")
  private String awsBucket;

  private AmazonS3 s3Client;

  @PostConstruct
  public void init() {
    s3Client = AmazonS3Client.builder()
          .withRegion(awsRegion)
          .build();
  }

  public void processUpload(final String sourceFile, final String album, final String targetFile) {
    log.info("moving uploads/" + sourceFile + " to " + album + "/" + targetFile + " in " + awsBucket);
    final CopyObjectResult copyResponse = s3Client.copyObject(awsBucket, "uploads/" + sourceFile, awsBucket, album + "/" + targetFile);
    log.info("response: " + copyResponse);
    s3Client.deleteObject(awsBucket, "uploads/" + sourceFile);
  }
}
