package ru.practicum.explore.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long id;
    @NotNull
    @NotBlank
    @Length(min = 6, max = 254)
    private String email;
    @NotNull
    @NotBlank
    @Size(min = 2)
    @Size(max = 250)
    private String name;
}
