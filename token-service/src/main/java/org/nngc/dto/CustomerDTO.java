package org.nngc.dto;

import jakarta.validation.constraints.Email;
import org.jetbrains.annotations.NotNull;

public record CustomerDTO(
        @NotNull Long id,
        @Email String email,
        @NotNull String firstName,
        @NotNull String lastName,
        boolean enabled
){
    public CustomerDTO(Long id, String email, String firstName, String lastName) {
        this(id, email, firstName, lastName, true);
    }
}
