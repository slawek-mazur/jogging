package io.stricte.jogging.web.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class UserDto {

    @Email
    @Size(max = 100)
    private String email;

    @NotNull
    @Size(min = 10, max = 60)
    private String password;
}
