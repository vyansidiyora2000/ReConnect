package com.dal.asdc.reconnect.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class FileServiceTest {

    @Mock
    private ProfileServiceImpl profileService;

    @InjectMocks
    private FileService fileService;

    @Mock
    private MultipartFile file;


    @Captor
    private ArgumentCaptor<String> stringCaptor;

    private MockMultipartFile mockMultipartFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMultipartFile = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "Test Resume Content".getBytes());
    }

//    @Test
//    void testUploadResume() throws IOException {
//        fileService.uploadResume(1, mockMultipartFile);
//
//        verify(profileService, times(1)).updateResumePath(eq(1), stringCaptor.capture());
//        String capturedPath = stringCaptor.getValue();
//
//        // Use double backslashes to escape them in the string literal
//        assertTrue(capturedPath.startsWith("uploads\\resumes\\"));
//    }
//
//    @Test
//    void testUploadProfilePicture() throws IOException {
//        // Prepare the mock file with a fixed filename
//        MultipartFile mockMultipartFile = new MockMultipartFile("file", "profile.jpg", "image/jpeg", new byte[0]);
//        String expectedDirectory = "uploads\\images\\";
//        String expectedFileName = UUID.randomUUID() + "_profile.jpg";
//
//        // Mock the ProfileService
//        doNothing().when(profileService).updateProfilePicturePath(anyInt(), anyString());
//
//        // Call the method under test
//        fileService.uploadProfilePicture(1, mockMultipartFile);
//
//        // Capture the actual path used in the updateProfilePicturePath method
//        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
//        verify(profileService, times(1)).updateProfilePicturePath(eq(1), stringCaptor.capture());
//        String capturedPath = stringCaptor.getValue();
//
//        // Verify that the path starts with the expected directory and ends with a valid file name
//        assertTrue(capturedPath.startsWith(expectedDirectory));
//        assertTrue(capturedPath.contains(expectedFileName.split("_")[1])); // Check part of the filename
//    }

    @Test
    void testGetResume() throws IOException {
        String resumePath = "uploads/resumes/resume.pdf";
        byte[] expectedContent = "Test Resume Content".getBytes();

        // Mock profileService to return the resume path
        when(profileService.getResumePath(anyInt())).thenReturn(resumePath);

        Path mockPath = Paths.get(resumePath);
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readAllBytes(mockPath)).thenReturn(expectedContent);

            // Call the method under test
            byte[] actualContent = fileService.getResume(1);

            // Verify the results
            assertNotNull(actualContent);
            assertArrayEquals(expectedContent, actualContent);
        }
    }

    @Test
    void testGetProfilePicture() throws IOException {
        String profilePicturePath = "uploads/images/profile.jpg";
        byte[] expectedContent = "Test Profile Picture Content".getBytes();

        // Mock profileService to return the profile picture path
        when(profileService.getProfilePicturePath(anyInt())).thenReturn(profilePicturePath);

        // Mock Files.readAllBytes method using MockedStatic
        Path mockPath = Paths.get(profilePicturePath);
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readAllBytes(mockPath)).thenReturn(expectedContent);

            // Call the method under test
            byte[] actualContent = fileService.getProfilePicture(1);

            // Verify the results
            assertNotNull(actualContent);
            assertArrayEquals(expectedContent, actualContent);
        }
    }

    @Test
    void testGetProfilePictureWhenPathIsNull() throws IOException {
        // Mock profileService to return null for the profile picture path
        when(profileService.getProfilePicturePath(anyInt())).thenReturn(null);

        // Call the method under test
        byte[] actualContent = fileService.getProfilePicture(1);

        // Verify that the result is null
        assertNull(actualContent);
    }

    @Test
    void testGetResumeWhenPathIsNull() throws IOException {
        // Mock profileService to return null for the resume path
        when(profileService.getResumePath(anyInt())).thenReturn(null);

        // Call the method under test
        byte[] actualContent = fileService.getResume(1);

        // Verify that the result is null
        assertNull(actualContent);
    }

    @Test
    void testUploadResumeCreatesDirectory() throws IOException {
        // Set up mocks
        when(file.getOriginalFilename()).thenReturn("resume.pdf");
        when(file.getBytes()).thenReturn("Sample Resume Content".getBytes());

        Path directory = Paths.get("uploads/resumes");

        // Mock static methods for Files class
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            // Configure mocks
            mockedFiles.when(() -> Files.exists(directory)).thenReturn(false);

            // Execute the method
            fileService.uploadResume(1, file);

            // Verify that Files.createDirectories() was called
            mockedFiles.verify(() -> Files.createDirectories(directory), times(1));

            // Verify that profileService.updateResumePath was called with the correct arguments
            verify(profileService).updateResumePath(anyInt(), anyString());
        }
    }

    @Test
    void testUploadProfilePictureCreatesDirectory() throws IOException {
        // Set up mocks
        when(file.getOriginalFilename()).thenReturn("profile.jpg");
        when(file.getBytes()).thenReturn("Sample Profile Picture Content".getBytes());

        Path directory = Paths.get("uploads/images");

        // Mock static methods for Files class
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            // Configure mocks
            mockedFiles.when(() -> Files.exists(directory)).thenReturn(false);

            // Execute the method
            fileService.uploadProfilePicture(1, file);

            // Verify that Files.createDirectories() was called
            mockedFiles.verify(() -> Files.createDirectories(directory), times(1));

            // Verify that profileService.updateProfilePicturePath was called with the correct arguments
            verify(profileService).updateProfilePicturePath(anyInt(), anyString());
        }
    }

}
