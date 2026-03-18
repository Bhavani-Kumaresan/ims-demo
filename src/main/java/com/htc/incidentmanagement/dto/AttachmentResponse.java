package com.htc.incidentmanagement.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Attachment response DTO")
public class AttachmentResponse {

    private Long attachmentId;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long ticketId;
    private Long uploadedByEmployeeId;
    private LocalDateTime uploadedAt;

    public AttachmentResponse(
            Long attachmentId,
            String fileName,
            String filePath,
            String fileType,
            Long ticketId,
            Long uploadedByEmployeeId,
            LocalDateTime uploadedAt) {

        this.attachmentId = attachmentId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.ticketId = ticketId;
        this.uploadedByEmployeeId = uploadedByEmployeeId;
        this.uploadedAt = uploadedAt;
    }

    public AttachmentResponse() {
    }



    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public void setUploadedByEmployeeId(Long uploadedByEmployeeId) {
        this.uploadedByEmployeeId = uploadedByEmployeeId;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public Long getUploadedByEmployeeId() {
        return uploadedByEmployeeId;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}
