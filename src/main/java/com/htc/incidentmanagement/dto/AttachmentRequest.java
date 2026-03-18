package com.htc.incidentmanagement.dto;

import jakarta.validation.constraints.NotBlank;

public class AttachmentRequest {

    @NotBlank(message = "Filename is required")
    private String fileName;

    @NotBlank(message = "filePath is required")
    private String filePath;

    @NotBlank(message = "filePath is required")
    private String fileType;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    // getters & setters
}
