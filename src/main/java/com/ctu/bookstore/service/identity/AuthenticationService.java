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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    @NonFinal // để không bị đưa vào constructor (vì đã dùng @RequiredArg phía trên)
    @Value("${jwt.signerKey}")
    String SIGNER_KEY;

    public AuthenticationRespone authenticate(AuthenticationRequest authenticationRequest){
        User user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
        if(!authenticated){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String token = generateToken(user);

        return AuthenticationRespone.builder()
                .token(token)
                .authenticated(authenticated)
                .build();
    }

    private String generateToken(User user){
        // Using nimbus library to generate Token
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
        //khi nào hiểu được buildScope ròi thi quay lại đây tự hoi xem có cần truyền toàn bộ user vào hàm này để tạo token hay khong
        // hay chỉ cần mỗi username và role
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
        System.out.println("role của user trong buildScope " + user.getRoles());
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

    public IntrospectRespone introspect(IntrospectRequest request)
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
