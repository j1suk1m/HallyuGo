package com.hallyugo.hallyugo.proof.controller;

import com.hallyugo.hallyugo.auth.annotation.AuthUser;
import com.hallyugo.hallyugo.proof.domain.request.ProofShotRequestDto;
import com.hallyugo.hallyugo.proof.domain.response.ProofShotResponseDto;
import com.hallyugo.hallyugo.proof.domain.response.ProofShotUploadResponseDto;
import com.hallyugo.hallyugo.proof.service.ProofShotService;
import com.hallyugo.hallyugo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ProofController {

    private final ProofShotService proofShotService;

    // User
    @GetMapping("/user/proof-shot")
    public ResponseEntity<ProofShotResponseDto> getUserProofShot(
            @AuthUser User user,
            @RequestParam(name = "limit", defaultValue = "9") int limit
    ) {
        ProofShotResponseDto result = proofShotService.getProofShotsByUser(user, limit);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/user/proof-shot/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProofShotUploadResponseDto> uploadUserProofShot(
            @AuthUser User user,
            @RequestPart(value = "proof_shot")ProofShotRequestDto proofShot,
            @RequestPart(value = "image")MultipartFile image
    ) throws IOException {
        ProofShotUploadResponseDto result = proofShotService.saveProofShot(user, proofShot, image);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/user/proof-shot/delete/{id}")
    public ResponseEntity<?> deleteUserProofShot(
            @AuthUser User user,
            @PathVariable Long id
    ) {
        proofShotService.deleteProofShot(user, id);
        return ResponseEntity.ok(null);
    }

    // Common
    @GetMapping("/proof-shot/location")
    public ResponseEntity<ProofShotResponseDto> getLocationProofShot(
            @RequestParam(name = "location_id") Long locationId
    ) {
        ProofShotResponseDto result = proofShotService.getProofShot(locationId);
        return ResponseEntity.ok(result);
    }
}