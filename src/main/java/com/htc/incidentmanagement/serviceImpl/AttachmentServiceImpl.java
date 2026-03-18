package com.htc.incidentmanagement.serviceImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.htc.incidentmanagement.dto.AttachmentRequest;
import com.htc.incidentmanagement.dto.AttachmentResponse;
import com.htc.incidentmanagement.model.Attachment;
import com.htc.incidentmanagement.model.Employee;
import com.htc.incidentmanagement.model.IncidentTicket;
import com.htc.incidentmanagement.notification.MailService;
import com.htc.incidentmanagement.repository.AttachmentRepository;
import com.htc.incidentmanagement.repository.EmployeeRepository;
import com.htc.incidentmanagement.repository.IncidentTicketRepository;
import com.htc.incidentmanagement.service.AttachmentService;

import jakarta.mail.MessagingException;

@Service
@Transactional
public class AttachmentServiceImpl implements AttachmentService {

        private static final Logger logger = LoggerFactory.getLogger(AttachmentServiceImpl.class);

        private final AttachmentRepository attachmentRepository;
        private final IncidentTicketRepository ticketRepository;
        private final EmployeeRepository employeeRepository;
        private final MailService mailService;

        public AttachmentServiceImpl(
                        AttachmentRepository attachmentRepository,
                        IncidentTicketRepository ticketRepository,
                        EmployeeRepository employeeRepository, MailService mailService) {
                this.attachmentRepository = attachmentRepository;
                this.ticketRepository = ticketRepository;
                this.employeeRepository = employeeRepository;
                this.mailService = mailService;
                logger.info("AttachmentService initialized.");
        }

        @Override
        public List<AttachmentResponse> getAttachmentsByTicket(Long ticketId) {

                IncidentTicket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new NoSuchElementException("Ticket not found with ID: " + ticketId));

                return attachmentRepository.findByTicket(ticket)
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        @Override
        public AttachmentResponse getAttachmentById(Long id) {

                Attachment attachment = attachmentRepository.findById(id)
                                .orElseThrow(() -> new NoSuchElementException("Attachment not found with ID: " + id));

                return mapToResponse(attachment);
        }

        @Override
        public AttachmentResponse createAttachment(
                        AttachmentRequest request,
                        Long ticketId,
                        Long uploadedByEmployeeId) {

                IncidentTicket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new NoSuchElementException("Ticket not found with ID: " + ticketId));

                Employee employee = employeeRepository.findById(uploadedByEmployeeId)
                                .orElseThrow(() -> new NoSuchElementException(
                                                "Employee not found with ID: " + uploadedByEmployeeId));

                Attachment attachment = new Attachment();
                attachment.setFileName(request.getFileName());
                attachment.setFilePath(request.getFilePath());
                attachment.setFileType(request.getFileType());
                attachment.setTicket(ticket);
                attachment.setUploadedBy(employee);
                attachment.setUploadedAt(LocalDateTime.now());

                Attachment saved = attachmentRepository.save(attachment);
                return mapToResponse(saved);
        }

        @Override
        public void deleteAttachment(Long attachmentId) {

                Attachment attachment = attachmentRepository.findById(attachmentId)
                                .orElseThrow(() -> new NoSuchElementException(
                                                "Attachment not found with ID: " + attachmentId));

                attachmentRepository.delete(attachment);
        }

        @Override
        public AttachmentResponse uploadAndSaveFile(
                        MultipartFile file, Long ticketId, Long uploadedByEmployeeId) throws IOException {

                logger.info("UploadAndSaveFile ticketId {} to uploadedByEmployeeId {}", ticketId, uploadedByEmployeeId);

                IncidentTicket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new NoSuchElementException("Ticket not found"));

                Employee employee = employeeRepository.findById(uploadedByEmployeeId)
                                .orElseThrow(() -> new NoSuchElementException("Employee not found"));

                String uploadDir = "uploads/tickets/" + ticketId;
                Files.createDirectories(Paths.get(uploadDir));

                String filePath = uploadDir + "/" + file.getOriginalFilename();
                file.transferTo(Paths.get(filePath));

                Attachment attachment = new Attachment();
                attachment.setFileName(file.getOriginalFilename());
                attachment.setFilePath(filePath);
                attachment.setFileType(file.getContentType());
                attachment.setTicket(ticket);
                attachment.setUploadedBy(employee);
                attachment.setUploadedAt(LocalDateTime.now());

                notification(ticketId);

                Attachment saved = attachmentRepository.save(attachment);
                logger.info("Attachment Uploaded successfully... ", ticketId, employee.getEmail());
                return mapToResponse(saved);
        }

        // -------------------- INTERNAL DTO MAPPING --------------------

        public void notification(long ticketId) {

                String uploadBasePath = "uploads/tickets/";
                File mailFile = Paths.get(uploadBasePath, "3", "approval.pdf").toFile();

                try {
                        // Validate attachment
                        if (!mailFile.exists() || !mailFile.isFile() || !mailFile.canRead()) {
                                throw new FileNotFoundException(
                                                "Attachment not found or not readable: " + mailFile.getAbsolutePath());
                        }

                        mailService.sendMailWithAttachment(
                                        "trng_testuser5@htcindia.in",
                                        "Ticket Attachment",
                                        "Please find the attached file for Ticket ID: " + ticketId,
                                        mailFile);

                } catch (FileNotFoundException e) {
                        // File/path related issues
                        logger.error("Attachment missing for ticketId={}", ticketId, e);
                        throw new RuntimeException("Unable to send email: attachment missing", e);

                } catch (Exception e) {
                        // Catch-all for unexpected failures
                        logger.error("Unexpected error while sending notification for ticketId={}", ticketId, e);
                        throw new RuntimeException("Unexpected error while sending ticket notification", e);
                }
        }

        private AttachmentResponse mapToResponse(Attachment attachment) {

                AttachmentResponse response = new AttachmentResponse();
                response.setAttachmentId(attachment.getAttachmentId());
                response.setFileName(attachment.getFileName());
                response.setFilePath(attachment.getFilePath());
                response.setFileType(attachment.getFileType());
                response.setUploadedAt(attachment.getUploadedAt());
                response.setTicketId(attachment.getTicket().getTicketId());
                response.setUploadedByEmployeeId(
                                attachment.getUploadedBy() != null
                                                ? attachment.getUploadedBy().getEmployeeId()
                                                : null);

                return response;
        }

}
