package com.egineering.jsteele.s3demo.service;

import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * Wrapper for AWS security token service to allow temporary credentials for accessing S3 bucket.
 */
@Service
public class AwsSecurityTokenService {
  private static final Logger log = LoggerFactory.getLogger(AwsSecurityTokenService.class);

  private AWSSecurityTokenService stsClient;

  @Value("${aws.region:us-east-1}")
  private String awsRegion;
  @Value("${aws.account:612060874604}")
  private String awsAccount;
  @Value("${aws.sts.session.ttl:900}")
  private int stsTokenTtl;
  @Value("${aws.role.s3.upload:neighborlink.dev.demo.s3.upload}")
  private String uploadRole;

  @PostConstruct
  public void init() {
    stsClient = AWSSecurityTokenServiceClientBuilder
          .standard()
          .withRegion(awsRegion)
          .build();
  }

  public Credentials getToken() {
    final String roleArn = "arn:aws:iam::" + awsAccount + ":role/" + uploadRole;
    final String sessionId = UUID.randomUUID().toString();
    final AssumeRoleRequest request = new AssumeRoleRequest();
    request.setDurationSeconds(stsTokenTtl);
    request.setRoleArn(roleArn);
    request.setRoleSessionName(sessionId);

    log.info("Requesting credentials to access to " + roleArn + " for " + stsTokenTtl + " seconds");

    final AssumeRoleResult result = stsClient.assumeRole(request);
    return result.getCredentials();
  }
}
