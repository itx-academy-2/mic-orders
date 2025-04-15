package com.academy.orders.apirest.auth.controller;

import com.academy.orders.apirest.auth.mapper.SignUpRequestDTOMapper;
import com.academy.orders.domain.account.usecase.CreateUserAccountUseCase;
import com.academy.orders.domain.account.usecase.GetAccountDetailsUseCase;
import com.academy.orders_api_rest.generated.api.SecurityApi;
import com.academy.orders_api_rest.generated.model.AuthTokenResponseDTO;
import com.academy.orders_api_rest.generated.model.SignInRequestDTO;
import com.academy.orders_api_rest.generated.model.SignUpRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AuthTokenController implements SecurityApi {
  private final JwtEncoder encoder;

  private final PasswordEncoder passwordEncoder;

  private final AuthenticationManager authenticationManager;

  private final CreateUserAccountUseCase createUserAccountUseCase;

  private final SignUpRequestDTOMapper signUpRequestDTOMapper;

  @Override
  public ResponseEntity<AuthTokenResponseDTO> signUp(SignUpRequestDTO signUpRequestDTO) {
    SignInRequestDTO signInRequestDto = signUpRequestDTOMapper.toSignInRequestDto(signUpRequestDTO);
    signUpRequestDTO.setPassword(passwordEncoder.encode(signUpRequestDTO.getPassword()));
    createUserAccountUseCase.create(signUpRequestDTOMapper.fromDto(signUpRequestDTO));

    final AuthTokenResponseDTO authTokenResponseDTO = signIn(signInRequestDto).getBody();
    return ResponseEntity.status(HttpStatus.CREATED).body(authTokenResponseDTO);
  }

  @Override
  public ResponseEntity<AuthTokenResponseDTO> signIn(SignInRequestDTO credentials) {
    var authentication = authenticate(credentials);
    var claims = buildClaims(authentication);
    var token = this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

    AuthTokenResponseDTO authTokenResponseDTO = new AuthTokenResponseDTO(token);
    return ResponseEntity.ok(authTokenResponseDTO);
  }

  private Authentication authenticate(SignInRequestDTO credentials) {
    return authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword()));
  }

  private JwtClaimsSet buildClaims(Authentication authentication) {
    Instant now = Instant.now();
    long expiry = 3600L;

    GetAccountDetailsUseCase getAccountDetailsUseCase = (GetAccountDetailsUseCase) authentication.getPrincipal();
    String scope = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(" "));

    return JwtClaimsSet.builder()
        .issuer("self")
        .issuedAt(now)
        .expiresAt(now.plusSeconds(expiry))
        .subject(authentication.getName())
        .claim("scope", scope)
        .claim("id", getAccountDetailsUseCase.getId())
        .claim("firstName", getAccountDetailsUseCase.getFirstName())
        .claim("lastName", getAccountDetailsUseCase.getLastName()).build();
  }
}
