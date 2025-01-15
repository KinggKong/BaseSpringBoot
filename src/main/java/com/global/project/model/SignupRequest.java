package com.global.project.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignupRequest {
    String lastName;
    String firstName;
    String email;
    String password;
    LocalDate dob;
    int gender;
}
