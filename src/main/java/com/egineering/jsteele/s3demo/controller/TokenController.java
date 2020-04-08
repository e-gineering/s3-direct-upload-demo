package com.egineering.jsteele.s3demo.controller;

import com.amazonaws.services.securitytoken.model.Credentials;
import com.egineering.jsteele.s3demo.service.AwsSecurityTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Temporary credentials access token endpoint.
 */
@RestController
public class TokenController {
  @Autowired
  private AwsSecurityTokenService tokenService;

  @PostMapping("/token")
  public Credentials create() {
    return tokenService.getToken();
  }
}
