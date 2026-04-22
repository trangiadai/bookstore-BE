package com.ctu.bookstore.controller.identity;

import com.ctu.bookstore.dto.request.identity.AuthenticationRequestDTO;
import com.ctu.bookstore.dto.request.identity.IntrospectRequestDTO;
import com.ctu.bookstore.dto.request.identity.LogoutRequestDTO;
import com.ctu.bookstore.dto.request.identity.RefreshRequestDTO;
import com.ctu.bookstore.dto.response.ApiResponseDTO;
import com.ctu.bookstore.dto.response.identity.AuthenticationResponseDTO;
import com.ctu.bookstore.dto.response.IntrospectResponseDTO;
import com.ctu.bookstore.service.identity.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/auth")
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    public ApiResponseDTO<AuthenticationResponseDTO> authenticate(@RequestBody AuthenticationRequestDTO authenticationRequestDTO){
        AuthenticationResponseDTO result = authenticationService.authenticate(authenticationRequestDTO);

        return ApiResponseDTO.<AuthenticationResponseDTO>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponseDTO<IntrospectResponseDTO> introspect(@RequestBody IntrospectRequestDTO introspectRequestDTO) throws ParseException, JOSEException {
        IntrospectResponseDTO result = authenticationService.introspect(introspectRequestDTO);

        return ApiResponseDTO.<IntrospectResponseDTO>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    public ApiResponseDTO<Void> logout(@RequestBody LogoutRequestDTO request) throws ParseException, JOSEException {
        authenticationService.logout(request);

        return ApiResponseDTO.<Void>builder()
                .build();
    }

    @PostMapping("/refresh")
    ApiResponseDTO<AuthenticationResponseDTO> authenticate(@RequestBody RefreshRequestDTO request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);

        return ApiResponseDTO.<AuthenticationResponseDTO>builder().result(result).build();
    }
}
