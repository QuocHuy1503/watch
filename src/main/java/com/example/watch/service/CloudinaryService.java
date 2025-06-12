package com.example.watch.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    public String upload(MultipartFile file, String folder) throws IOException {
        Map<?,?> options = ObjectUtils.asMap("folder", folder);
        Map<?,?> result = cloudinary.uploader().upload(file.getBytes(), options);
        return result.get("secure_url").toString();
    }
}
