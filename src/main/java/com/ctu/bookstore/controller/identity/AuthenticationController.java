package com.ctu.bookstore.controller.identity;

import com.ctu.bookstore.dto.request.identity.AuthenticationRequest;
import com.ctu.bookstore.dto.request.identity.IntrospectRequest;
import com.ctu.bookstore.dto.request.identity.LogoutRequest;
import com.ctu.bookstore.dto.respone.ApiRespone;
import com.ctu.bookstore.dto.respone.identity.AuthenticationRespone;
import com.ctu.bookstore.dto.respone.IntrospectRespone;
import com.ctu.bookstore.service.identity.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;
    @PostMapping("/token")
    public ApiRespone<AuthenticationRespone>authenticate(@RequestBody AuthenticationRequest authenticationRequest){
        AuthenticationRespone result = authenticationService.authenticate(authenticationRequest);

        return ApiRespone.<AuthenticationRespone>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    public ApiRespone<IntrospectRespone>introspect(@RequestBody IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        IntrospectRespone result = authenticationService.instrospect(introspectRequest);

        return ApiRespone.<IntrospectRespone>builder()
                .result(result)
                .build();
    }
    @PostMapping("/logout")
    public ApiRespone<Void>logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);

        return ApiRespone.<Void>builder()
                .build();
    }
}
