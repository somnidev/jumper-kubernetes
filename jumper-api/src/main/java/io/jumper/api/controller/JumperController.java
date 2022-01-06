package io.jumper.api.controller;

import io.jumper.api.dto.UrlDto;
import io.jumper.api.service.UrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController()
@Slf4j
public class JumperController {

    @Autowired
    private UrlService urlService;

    @GetMapping("/{shortUrl:[a-zA-Z0-9]{6}}")
    public ResponseEntity<Void> redirect(@PathVariable("shortUrl") String shortUrlPath){
        log.info("JumperController 'GET /shortUrl/???... with path " + shortUrlPath);
        var originalUrl = urlService.getUrl(shortUrlPath);
        log.info("JumperController 'GET /shortUrl/'" + shortUrlPath + "' -> originalPath: " + originalUrl);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
    }

    // curl localhost:8080
    @GetMapping("/shorturl/{shortUrl:[a-zA-Z0-9]{6}}")
    public ResponseEntity<UrlDto> get(@PathVariable("shortUrl") String shortUrlPath) {
        var originalUrl = urlService.getUrl(shortUrlPath);
        log.info("JumperController: GET " + shortUrlPath + " -> " + originalUrl);
        var urlDto = UrlDto.builder()
                .shortUrl(shortUrlPath)
                .url(originalUrl)
                .build();
        return new ResponseEntity<UrlDto>(urlDto, HttpStatus.OK);
    }

    // curl -v -H'Content-Type: application/json' -d'{"url": "http://www.swr3.de"}' http://localhost:8080/
    @CrossOrigin
    @PostMapping("/shorturl")
    public ResponseEntity<UrlDto> add(@RequestBody UrlDto urlDto) {
        var originalUrl = urlDto.getUrl();
        var savedUrl = urlService.createUrl(originalUrl);
        log.info("JumperController: POST " + originalUrl + " -> " + savedUrl);

        var body = UrlDto.builder()
                .shortUrl(savedUrl)
                .url(originalUrl)
                .build();
        return new ResponseEntity<UrlDto>(body, HttpStatus.OK);
    }

    @GetMapping("/ping")
    public String ping() {
        log.info("Endpoint /ping");
        return "Hello Ping";
    }

}
