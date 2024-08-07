package com.dal.asdc.reconnect.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final ProfileServiceImpl profileService;

    private final String uploadImagesDirectory = "uploads/images";
    private final String uploadResumesDirectory = "uploads/resumes";

    /**
     * Uploads a resume file for a user and updates their profile with the file path.
     *
     * @param userId The ID of the user uploading the resume
     * @param file   The resume file to be uploaded
     * @throws IOException If there's an error in file operations
     */
    public void uploadResume(int userId, MultipartFile file) throws IOException {
        Path directory = Paths.get(uploadResumesDirectory);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
            log.info("Created directories for resumes at: {}", uploadResumesDirectory);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path fileNameAndPath = Paths.get(uploadResumesDirectory, fileName);
        Files.write(fileNameAndPath, file.getBytes());

        profileService.updateResumePath(userId, fileNameAndPath.toString());
        log.info("Resume uploaded successfully for userId: {}", userId);
    }

    /**
     * Uploads a profile picture for a user and updates their profile with the file path.
     *
     * @param userId The ID of the user uploading the profile picture
     * @param file   The profile picture file to be uploaded
     * @throws IOException If there's an error in file operations
     */
    public void uploadProfilePicture(int userId, MultipartFile file) throws IOException {
        Path directory = Paths.get(uploadImagesDirectory);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
            log.info("Created directories for images at: {}", uploadImagesDirectory);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path fileNameAndPath = Paths.get(uploadImagesDirectory, fileName);
        Files.write(fileNameAndPath, file.getBytes());

        profileService.updateProfilePicturePath(userId, fileNameAndPath.toString());
    }

    /**
     * Retrieves the resume file for a user.
     *
     * @param userId The ID of the user whose resume is to be retrieved
     * @return The resume file as a byte array
     * @throws IOException If there's an error in file operations
     */
    public byte[] getResume(int userId) throws IOException {
        String resumePath = profileService.getResumePath(userId);
        if (resumePath != null) {
            return Files.readAllBytes(Paths.get(resumePath));
        }
        return null;
    }

    /**
     * Retrieves the profile picture for a user.
     *
     * @param userId The ID of the user whose profile picture is to be retrieved
     * @return The profile picture as a byte array
     * @throws IOException If there's an error in file operations
     */
    public byte[] getProfilePicture(int userId) throws IOException {
        log.info("Retrieving profile picture for userId: {}", userId);
        String profilePicturePath = profileService.getProfilePicturePath(userId);
        if (profilePicturePath != null) {
            return Files.readAllBytes(Paths.get(profilePicturePath));
        }
        return null;
    }
}
