package io.stricte.jogging.web.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private int id;

    @Email
    @Size(max = 100)
    private String email;

    private String password;

    public UserDto(String email) {
        this.email = email;
    }

    public UserDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
