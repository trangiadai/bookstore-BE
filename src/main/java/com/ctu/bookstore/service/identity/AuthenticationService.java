package com.ctu.bookstore.service.identity;

import com.ctu.bookstore.dto.request.identity.AuthenticationRequest;
import com.ctu.bookstore.dto.request.identity.IntrospectRequest;
import com.ctu.bookstore.dto.request.identity.LogoutRequest;
import com.ctu.bookstore.dto.respone.identity.AuthenticationRespone;
import com.ctu.bookstore.dto.respone.IntrospectRespone;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;


    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    public IntrospectRespone instrospect(IntrospectRequest request)
            throws JOSEException, ParseException {
       var token = request.getToken();
        SignedJWT jwt = null; //để lưu sessionId trong socketHandler

       boolean isValid = true;
       try {
           jwt = verifyToken(token);
       }catch (Exception e){
           isValid = false;
       }


       return IntrospectRespone.builder()
               .userName(
                       Objects.nonNull(jwt)
                               ? jwt.getJWTClaimsSet().getSubject()
                                : null
                       )
               .valid(isValid)
               .build();
    }
    public AuthenticationRespone authenticate(AuthenticationRequest authenticationRequest){
        var user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));


        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean result = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());

        String token = generateToken(user);

        return AuthenticationRespone.builder()
                .token(token)
                .authenticated(result)
                .build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());

        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
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
                .claim("scope",buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header,payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();

    }
    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        System.out.println("role của user trong buildScope "+user.getRoles());
        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions()
                            .forEach(permission -> stringJoiner.add(permission.getName()));
            });
        System.out.println("stringJoiner của buildScope trong AuthenticationService "+stringJoiner);
        return stringJoiner.toString();
    }
//private String buildScope(User user){
//    StringJoiner stringJoiner = new StringJoiner(" ");
//    if (!CollectionUtils.isEmpty(user.getRoles()))
//        user.getRoles().forEach(stringJoiner::add);
//
//    return stringJoiner.toString();
//}

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository
                .existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }
}
