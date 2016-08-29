package io.stricte.jogging.web.rest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRest {

    private final String email;

    private final String password;
}
