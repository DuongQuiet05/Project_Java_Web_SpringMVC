package ra.edu.utils;

import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HandlerFile {
    private final ServletContext servletContext;
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png","webp");
    private static final long MAX_FILE_SIZE_MB = 5;

    public String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }

    public String saveImage(MultipartFile file) {
        try {
            if (file.isEmpty()) throw new IllegalArgumentException("File is empty");

            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);

            if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                throw new IllegalArgumentException("Invalid file type: " + extension);
            }

            long sizeInMB = file.getSize() / (1024 * 1024);
            if (sizeInMB > MAX_FILE_SIZE_MB) {
                throw new IllegalArgumentException("File too large: " + sizeInMB + "MB (max is " + MAX_FILE_SIZE_MB + "MB)");
            }

            String filename = originalFilename.replaceAll("\\s+", "_");

            String deployUploadPath = servletContext.getRealPath("/uploads");
            new File(deployUploadPath).mkdirs();

            Path srcUploadPath = Paths.get("D:/Learn/JavaWeb/Project_InternShip/src/main/webapp/uploads");
            new File(srcUploadPath.toString()).mkdirs();

            byte[] bytes = file.getBytes();
            FileCopyUtils.copy(bytes, new File(deployUploadPath, filename));
            FileCopyUtils.copy(bytes, new File(srcUploadPath.toString(), filename));

            return filename;
        }
        catch (IOException e) {
            throw new IllegalArgumentException("ERROR saving image: " + e.getMessage());
        }
    }
    public String savePdf(MultipartFile file) {
        try {
            if (file.isEmpty()) throw new IllegalArgumentException("File is empty");

            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);

            if (!"pdf".equalsIgnoreCase(extension)) {
                throw new IllegalArgumentException("Invalid file type: " + extension + ". Only PDF files are allowed.");
            }

            long sizeInMB = file.getSize() / (1024 * 1024);
            long maxPdfSizeMB = 10;
            if (sizeInMB > maxPdfSizeMB) {
                throw new IllegalArgumentException("File too large: " + sizeInMB + "MB (max is " + maxPdfSizeMB + "MB)");
            }

            String filename = originalFilename.replaceAll("\\s+", "_");

            String deployUploadPath = servletContext.getRealPath("/uploads");
            new File(deployUploadPath).mkdirs();

            Path srcUploadPath = Paths.get("D:/Learn/JavaWeb/Project_InternShip/src/main/webapp/uploads");
            new File(srcUploadPath.toString()).mkdirs();

            byte[] bytes = file.getBytes();
            FileCopyUtils.copy(bytes, new File(deployUploadPath, filename));
            FileCopyUtils.copy(bytes, new File(srcUploadPath.toString(), filename));

            return filename;
        }
        catch (IOException e) {
            throw new IllegalArgumentException("ERROR saving pdf: " + e.getMessage());
        }
    }


}
