package io.stricte.jogging.web.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRest {

    private String email;

    private String password;
}
