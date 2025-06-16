package com.falcon.falcon.controllers;

import com.falcon.falcon.services.impl.OpenVPNServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vpn")
public class OpenVPNController {

    private final OpenVPNServiceImpl openVPNService;

    public OpenVPNController(OpenVPNServiceImpl openVPNService) {
        this.openVPNService = openVPNService;
    }

    @GetMapping("/users/{username}/config")
    public ResponseEntity<byte[]> getUserConfig(@PathVariable String username) {
        try {
            byte[] configFile = openVPNService.generateUserConfig(username);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", username + ".ovpn");

            return new ResponseEntity<>(configFile, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
