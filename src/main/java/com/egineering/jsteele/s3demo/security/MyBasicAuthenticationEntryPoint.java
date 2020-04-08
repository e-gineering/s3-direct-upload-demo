package com.egineering.jsteele.s3demo.security;

import com.google.gson.Gson;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@Component
public class MyBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
  @Override
  public void commence(
        HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
        throws IOException {
    response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName());
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    PrintWriter writer = response.getWriter();
    writer.println("{\"status\":401,\"error\":\"Unauthorized\",\"message\":" + new Gson().toJson(authEx.getMessage()));
  }

  @Override
  public void afterPropertiesSet() {
    setRealmName("s3demo");
    super.afterPropertiesSet();
  }
}
