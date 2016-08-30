package io.stricte.jogging.web;

import io.stricte.jogging.domain.Run;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collection;
import java.util.Collections;

import static io.stricte.jogging.config.security.Role.ROLE_USER;

@Controller
@RequestMapping("/run")
@Secured(ROLE_USER)
public class RunController {

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Collection<Run>> runs() {
        return ResponseEntity.ok(Collections.emptyList());
    }
}
