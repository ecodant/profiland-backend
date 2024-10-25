package co.profiland.co.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.model.ContactRequest;
import co.profiland.co.service.ContactRequestService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@RestController
@RequestMapping("/profiland/contact-requests")
@Slf4j
public class ContactRequestController {
    private final ContactRequestService contactRequestService;

    public ContactRequestController(ContactRequestService contactRequestService) {
        this.contactRequestService = contactRequestService;
    }

    @PostMapping("/")
    public ResponseEntity<ContactRequest> createContactRequest(@RequestBody ContactRequest contactRequest) {
        try {
            ContactRequest savedContactRequest = contactRequestService.createContactRequest(contactRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedContactRequest);
        } catch (Exception e) {
            log.error("Error creating ContactRequest", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<ContactRequest>> getAllContactRequests() {
        try {
            List<ContactRequest> contactRequests = contactRequestService.getAllContactRequests();
            return ResponseEntity.ok(contactRequests);
        } catch (Exception e) {
            log.error("Error retrieving all ContactRequests", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactRequest> getContactRequestById(@PathVariable String id) {
        try {
            ContactRequest contactRequest = contactRequestService.findContactRequestBySellerId(id);
            return contactRequest != null ? ResponseEntity.ok(contactRequest) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving ContactRequest by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactRequest> updateContactRequest(@PathVariable String id, @RequestBody ContactRequest contactRequest) {
        try {
            ContactRequest updatedContactRequest = contactRequestService.updateContactRequest(id, contactRequest);
            return updatedContactRequest != null ? ResponseEntity.ok(updatedContactRequest) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating ContactRequest with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContactRequest(@PathVariable String id) {
        try {
            boolean deleted = contactRequestService.deleteContactRequest(id);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting ContactRequest with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}