package io.stricte.jogging.web.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private int id;

    @Email
    @Size(max = 100)
    private String email;

    private String password;

    private Collection<String> authorities = Collections.emptySet();

    public UserDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
