package io.stricte.jogging.web;

import io.stricte.jogging.domain.Run;
import io.stricte.jogging.service.RunServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.Collection;

import static io.stricte.jogging.config.security.Role.ROLE_USER;

@Secured(ROLE_USER)
@Controller
@RequestMapping("/runs")
public class RunController {

    private final RunServiceImpl runService;

    @Autowired
    public RunController(RunServiceImpl runService) {
        this.runService = runService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Collection<Run>> list(Principal principal) {

        Collection<Run> runs = runService.getRunsForPrincipal(principal);

        return ResponseEntity.ok(runs);
    }
}
