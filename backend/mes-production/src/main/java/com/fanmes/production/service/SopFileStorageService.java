package com.fanmes.production.service;

import com.fanmes.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class SopFileStorageService {
    private static final Set<String> DEFAULT_ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp", "mp4", "webm", "pdf", "txt", "csv", "xlsx", "glb"
    );

    private final Path storageRoot;
    private final long maxFileSize;

    public SopFileStorageService(
            @Value("${fanmes.sop.storage-root:${user.dir}/sop-files}") String storageRoot,
            @Value("${fanmes.sop.max-file-size:104857600}") long maxFileSize
    ) {
        this.storageRoot = Path.of(storageRoot).toAbsolutePath().normalize();
        this.maxFileSize = maxFileSize;
    }

    public StoredFile store(MultipartFile file, String folder, Set<String> allowedExtensions) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("file is required");
        }
        if (file.getSize() > maxFileSize) {
            throw new BadRequestException("file size exceeds SOP upload limit");
        }

        String originalName = StringUtils.hasText(file.getOriginalFilename())
                ? Path.of(file.getOriginalFilename()).getFileName().toString()
                : "upload.bin";
        String extension = extensionOf(originalName);
        Set<String> allowed = allowedExtensions == null || allowedExtensions.isEmpty()
                ? DEFAULT_ALLOWED_EXTENSIONS
                : allowedExtensions;
        if (!allowed.contains(extension)) {
            throw new BadRequestException("unsupported SOP file extension: " + extension);
        }

        String hash = sha256(file);
        String objectKey = safeFolder(folder) + "/" + UUID.randomUUID() + "." + extension;
        Path target = storageRoot.resolve(objectKey).normalize();
        if (!target.startsWith(storageRoot)) {
            throw new BadRequestException("invalid storage object key");
        }

        try {
            Files.createDirectories(target.getParent());
            file.transferTo(target);
        } catch (IOException e) {
            throw new BadRequestException("failed to store SOP file: " + e.getMessage());
        }

        return new StoredFile(
                originalName,
                file.getContentType(),
                file.getSize(),
                objectKey,
                hash
        );
    }

    public Resource load(String objectKey) {
        if (!StringUtils.hasText(objectKey)) {
            throw new BadRequestException("object key is required");
        }
        Path target = storageRoot.resolve(objectKey).normalize();
        if (!target.startsWith(storageRoot)) {
            throw new BadRequestException("invalid storage object key");
        }
        try {
            Resource resource = new UrlResource(target.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new BadRequestException("SOP file does not exist: " + objectKey);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new BadRequestException("invalid SOP file path");
        }
    }

    private String extensionOf(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private String safeFolder(String folder) {
        if (!StringUtils.hasText(folder)) {
            return "sop";
        }
        return folder.trim().replaceAll("[^a-zA-Z0-9/_-]", "");
    }

    private String sha256(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream input = file.getInputStream(); DigestInputStream digestInput = new DigestInputStream(input, digest)) {
                byte[] buffer = new byte[8192];
                while (digestInput.read(buffer) != -1) {
                    // DigestInputStream updates the MessageDigest as bytes are read.
                }
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new BadRequestException("failed to calculate file hash: " + e.getMessage());
        }
    }

    public record StoredFile(
            String fileName,
            String contentType,
            long fileSize,
            String objectKey,
            String sha256
    ) {
    }
}
