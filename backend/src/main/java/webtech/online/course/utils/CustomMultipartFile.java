package webtech.online.course.utils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.file.Files;

public class CustomMultipartFile implements MultipartFile {

    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final File fileContent;
    private final byte[] byteContent;

    public CustomMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.byteContent = content;
        this.fileContent = null;
    }

    public CustomMultipartFile(String name, String originalFilename, String contentType, File file) {
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.fileContent = file;
        this.byteContent = null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return getSize() == 0;
    }

    @Override
    public long getSize() {
        if (fileContent != null)
            return fileContent.length();
        return byteContent != null ? byteContent.length : 0;
    }

    @Override
    public byte[] getBytes() throws IOException {
        if (fileContent != null)
            return Files.readAllBytes(fileContent.toPath());
        return byteContent;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (fileContent != null)
            return new FileInputStream(fileContent);
        return new ByteArrayInputStream(byteContent);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        if (fileContent != null) {
            FileCopyUtils.copy(fileContent, dest);
        } else {
            FileCopyUtils.copy(byteContent, dest);
        }
    }
}
