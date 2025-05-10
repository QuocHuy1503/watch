package com.example.watch.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long userId;

    @NotBlank @Size(max = 100)
    private String name;

    @NotBlank @Email @Size(max = 150)
    private String email;

    @Size(max = 20)
    private String phone;

    @NotBlank @Size(min = 6)
    private String password;

    @NotBlank
    private String role;

    @Pattern(regexp = "[MF]")
    private String gender;

    // getters and setters
}