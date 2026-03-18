package com.htc.incidentmanagement.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.htc.incidentmanagement.dto.AttachmentRequest;
import com.htc.incidentmanagement.dto.AttachmentResponse;

public interface AttachmentService {

    List<AttachmentResponse> getAttachmentsByTicket(Long ticketId);

    AttachmentResponse getAttachmentById(Long id);

    AttachmentResponse createAttachment(
            AttachmentRequest request,
            Long ticketId,
            Long uploadedByEmployeeId);

    void deleteAttachment(Long attachmentId);

    AttachmentResponse uploadAndSaveFile(
            MultipartFile file, Long ticketId, Long uploadedByEmployeeId) throws IOException;
}
