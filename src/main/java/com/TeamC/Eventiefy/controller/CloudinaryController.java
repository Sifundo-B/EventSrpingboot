package com.TeamC.Eventiefy.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

// Controller for handling image uploads to Cloudinary.
@RestController
@RequestMapping("/api/v1/cloudinary")
@RequiredArgsConstructor
public class CloudinaryController {

    private final Cloudinary cloudinary;

    /**
     * Endpoint for uploading an image to Cloudinary.
     * @param file the image file to upload
     * @return a ResponseEntity containing the upload result
     * @throws IOException if an error occurs during file upload
     */
    @PostMapping("/upload")
    public ResponseEntity<Map> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return ResponseEntity.ok(uploadResult);
    }
}
