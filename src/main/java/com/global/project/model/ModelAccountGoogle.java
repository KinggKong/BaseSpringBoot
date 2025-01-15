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
public class ModelAccountGoogle {
    String avatar;
    String username;
    String name;
    String email;
    String code;
    String password;
    LocalDate dob;
    int gender;
}
