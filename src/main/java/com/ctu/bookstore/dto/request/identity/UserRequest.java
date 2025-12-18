package com.ctu.bookstore.dto.request.identity;

import com.ctu.bookstore.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    @Size(min=4, message = "USERNAME_INVALID")
    String username;
    @Size(min=6, message = "PASSWORD_INVALID")
    String password;
    @NotBlank(message = "FIRSTNAME_REQUIRED")
    String firstname;
    @NotBlank(message = "LASTNAME_REQUIRED")
    String lastname;
    @NotNull(message = "GENDER_REQUIRED")
    Gender gender;
    @Past(message = "DOB_INVALID")
    LocalDate dob;
    String avatar;
    @Email(message = "EMAIL_INVALID")
    String email;
    String phoneNumber;
    String adress;
}
