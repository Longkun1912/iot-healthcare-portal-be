package com.example.iothealth.security.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    // User avatar management
    public String getUserAvatar(String email) {
        return cloudinary.url().generate("iot-web-portal/users/" + email);
    }

    public String uploadUserAvatar(String email, MultipartFile avatar) throws IOException {
        Map<String, String> uploadResult = cloudinary.uploader().upload(
                avatar.getBytes(),
                ObjectUtils.asMap("folder", "iot-web-portal/users", "public_id", email)
        );
        return uploadResult.get("url");
    }

    public void deleteUserAvatar(String email) throws IOException {
        cloudinary.uploader().destroy("iot-web-portal/users/" + email, ObjectUtils.emptyMap());
    }

    // Health objective picture management
    public String uploadHealthObjectivePicture(String title, MultipartFile picture) throws IOException {
        Map<String, String> uploadResult = cloudinary.uploader().upload(
                picture.getBytes(),
                ObjectUtils.asMap("folder", "iot-web-portal/health-objectives", "public_id", title)
        );
        return uploadResult.get("url");
    }

    public void deleteHealthObjectivePicture(String title) throws IOException {
        cloudinary.uploader().destroy("iot-web-portal/health-objectives/" + title, ObjectUtils.emptyMap());
    }
}
