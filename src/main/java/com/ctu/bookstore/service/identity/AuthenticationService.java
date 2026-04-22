package com.ctu.bookstore.service.identity;

import com.ctu.bookstore.dto.request.identity.AuthenticationRequestDTO;
import com.ctu.bookstore.dto.request.identity.IntrospectRequestDTO;
import com.ctu.bookstore.dto.request.identity.LogoutRequestDTO;
import com.ctu.bookstore.dto.response.identity.AuthenticationResponeDTO;
import com.ctu.bookstore.dto.response.IntrospectResponseDTO;
import com.ctu.bookstore.entity.identity.InvalidatedToken;
import com.ctu.bookstore.entity.identity.User;
import com.ctu.bookstore.exception.AppException;
import com.ctu.bookstore.exception.ErrorCode;
import com.ctu.bookstore.repository.identity.InvalidatedTokenRepository;
import com.ctu.bookstore.repository.identity.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    PasswordEncoder passwordEncoder;

    @NonFinal // để không bị đưa vào constructor (vì đã dùng @RequiredArg phía trên)
    @Value("${jwt.signerKey}")
    String SIGNER_KEY;

    public AuthenticationResponeDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO){
        User user = userRepository.findByUsername(authenticationRequestDTO.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        boolean authenticated = passwordEncoder.matches(authenticationRequestDTO.getPassword(), user.getPassword());
        if(!authenticated){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String token = generateToken(user);

        return AuthenticationResponeDTO.builder()
                .token(token)
                .authenticated(authenticated)
                .build();
    }

    private String generateToken(User user){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .issuer("BookStore_BE")
                .subject(user.getUsername())
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header,payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions()
                            .forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }

    public IntrospectResponseDTO introspect(IntrospectRequestDTO request)
            throws JOSEException, ParseException {

        var token = request.getToken();
        SignedJWT jwt = null; //để lưu sessionId trong socketHandler
        boolean isValid = true;
        try {
            jwt = verifyToken(token);
        }catch (Exception e){
            isValid = false;
        }

        return IntrospectResponseDTO.builder()
                .userName(Objects.nonNull(jwt) ? jwt.getJWTClaimsSet().getSubject() : null)
                .valid(isValid)
                .build();
    }

    public void logout(LogoutRequestDTO request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());
        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }



}
