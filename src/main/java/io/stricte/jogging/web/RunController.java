package io.stricte.jogging.web;

import io.stricte.jogging.domain.Run;
import io.stricte.jogging.service.RunService;
import io.stricte.jogging.web.rest.PaginationUtil;
import io.stricte.jogging.web.rest.model.RunDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static io.stricte.jogging.config.JacksonConfiguration.ISO_FIXED_FORMAT;
import static io.stricte.jogging.config.security.Role.USER;

@Secured(USER)
@Controller
@RequestMapping("/runs")
class RunController {

    private final RunService runService;

    @Autowired
    public RunController(RunService runService) {
        this.runService = runService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Collection<Run>> getRuns(Pageable pageable,
        @RequestParam Optional<String> from, @RequestParam Optional<String> to)
        throws URISyntaxException {

        final LocalDateTime fromDay = from.map(date -> LocalDateTime.parse(date, ISO_FIXED_FORMAT)).orElse(null);
        final LocalDateTime toDay = to.map(date -> LocalDateTime.parse(date, ISO_FIXED_FORMAT)).orElse(null);

        Page<Run> page = runService.all(pageable, fromDay, toDay);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/runs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getRun(@PathVariable int id) {
        final Run run = runService.one(id);
        return run != null ? ResponseEntity.ok(run) : ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> createRun(@Valid @RequestBody RunDto runDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Run saved = runService.create(runDto);

            return ResponseEntity.created(new URI("/runs/" + saved.getId()))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> updateRun(@Valid @RequestBody RunDto runDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            runService.update(runDto);

            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> updateRun(@PathVariable int id) {

        try {
            runService.delete(id);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
