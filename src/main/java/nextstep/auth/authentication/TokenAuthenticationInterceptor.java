package nextstep.auth.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.auth.context.Authentication;
import nextstep.auth.token.JwtTokenProvider;
import nextstep.auth.token.TokenRequest;
import nextstep.auth.token.TokenResponse;
import nextstep.member.application.CustomUserDetailsService;
import nextstep.member.domain.LoginMember;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenAuthenticationInterceptor implements HandlerInterceptor {

  private final CustomUserDetailsService customUserDetailsService;
  private final JwtTokenProvider jwtTokenProvider;

  public TokenAuthenticationInterceptor(CustomUserDetailsService customUserDetailsService, JwtTokenProvider jwtTokenProvider) {
    this.customUserDetailsService = customUserDetailsService;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
    AuthenticationToken authenticationToken = convert(request);
    Authentication authentication = authenticate(authenticationToken);
    ObjectMapper objectMapper = new ObjectMapper();

    String payload = objectMapper.writeValueAsString(authentication.getPrincipal());
    String token = jwtTokenProvider.createToken(payload);
    TokenResponse tokenResponse = new TokenResponse(token);

    String responseToClient = objectMapper.writeValueAsString(tokenResponse);
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getOutputStream().print(responseToClient);

    return false;
  }

  public AuthenticationToken convert(HttpServletRequest request) throws IOException {
    TokenRequest tokenRequest = new ObjectMapper().readValue(request.getInputStream(), TokenRequest.class);
    String principal = tokenRequest.getEmail();
    String credentials = tokenRequest.getPassword();

    return new AuthenticationToken(principal, credentials);
  }

  public Authentication authenticate(AuthenticationToken authenticationToken) {
    LoginMember loginMember = customUserDetailsService.loadUserByUsername(authenticationToken.getPrincipal());

    validateAuthentication(loginMember, authenticationToken.getCredentials());
    return new Authentication(loginMember);
  }

  private void validateAuthentication(LoginMember loginMember, String credentials) {
    if (!loginMember.checkPassword(credentials)) {
      throw new AuthenticationException();
    }
  }
}