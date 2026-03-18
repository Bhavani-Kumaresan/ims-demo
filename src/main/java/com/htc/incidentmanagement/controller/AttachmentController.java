package com.htc.incidentmanagement.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.htc.incidentmanagement.dto.AttachmentRequest;
import com.htc.incidentmanagement.dto.AttachmentResponse;
import com.htc.incidentmanagement.service.AttachmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/attachments")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Attachments", description = "File attachment management for tickets")
public class AttachmentController {

        private final AttachmentService attachmentService;

        public AttachmentController(AttachmentService attachmentService) {
                this.attachmentService = attachmentService;
        }

        // -------------------- GET BY TICKET --------------------
        @Operation(summary = "List attachments for a ticket", description = "Retrieves all attachments associated with a ticket.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Attachments retrieved", content = @Content(schema = @Schema(implementation = AttachmentResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Ticket not found")
        })
        @GetMapping("/ticket/{ticketId}")
        public ResponseEntity<List<AttachmentResponse>> getByTicket(@PathVariable Long ticketId) {
                return ResponseEntity.ok(
                                attachmentService.getAttachmentsByTicket(ticketId));
        }

        // -------------------- GET BY ID --------------------
        @Operation(summary = "Get attachment by ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Attachment found", content = @Content(schema = @Schema(implementation = AttachmentResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Attachment not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<AttachmentResponse> getById(@PathVariable Long id) {
                return ResponseEntity.ok(
                                attachmentService.getAttachmentById(id));
        }

        // -------------------- CREATE --------------------
        @Operation(summary = "Create new attachment")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Attachment created", content = @Content(schema = @Schema(implementation = AttachmentResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                        @ApiResponse(responseCode = "404", description = "Ticket or employee not found")
        })
        @PostMapping("/create")
        public ResponseEntity<AttachmentResponse> create(
                        @Parameter(description = "Ticket ID") @RequestParam Long ticketId,
                        @Parameter(description = "Uploader employee ID") @RequestParam Long uploadedByEmployeeId,
                        @RequestBody AttachmentRequest request) {

                AttachmentResponse response = attachmentService.createAttachment(request, ticketId,
                                uploadedByEmployeeId);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        // -------------------- DELETE --------------------
        @Operation(summary = "Delete attachment")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Attachment deleted"),
                        @ApiResponse(responseCode = "404", description = "Attachment not found")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(@PathVariable Long id) {
                attachmentService.deleteAttachment(id);
                return ResponseEntity.noContent().build();
        }

        // -------------------- File Upload ---------------

        @Operation(summary = "Upload attachment file for a ticket", description = "Uploads a file and links it to a ticket. Files are stored locally and metadata is stored in the database.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "File uploaded successfully", content = @Content(schema = @Schema(implementation = AttachmentResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                        @ApiResponse(responseCode = "404", description = "Ticket or employee not found")
        })
        @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<AttachmentResponse> uploadFile(
                        @RequestParam Long ticketId,
                        @RequestParam Long uploadedByEmployeeId,
                        @RequestPart MultipartFile file) throws IOException {

                AttachmentResponse response = attachmentService.uploadAndSaveFile(file, ticketId, uploadedByEmployeeId);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @Operation(summary = "Download attachment file")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
                        @ApiResponse(responseCode = "404", description = "Attachment not found")
        })
        @GetMapping("/download/{attachmentId}")
        public ResponseEntity<Resource> downloadFile(@PathVariable Long attachmentId) throws IOException {

                AttachmentResponse attachment = attachmentService.getAttachmentById(attachmentId);
                Path filePath = Paths.get(attachment.getFilePath());

                Resource resource = new UrlResource(filePath.toUri());

                if (!resource.exists()) {
                        return ResponseEntity.notFound().build();
                }

                return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=\"" + attachment.getFileName() + "\"")
                                .body(resource);
        }

}
