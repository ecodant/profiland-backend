package co.profiland.co.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.model.ContactRequest;
import co.profiland.co.model.StateRequest;
import co.profiland.co.service.ContactRequestService;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/profiland/contact-requests")
public class ContactRequestController {

    private final ContactRequestService contactRequestService;

    public ContactRequestController(ContactRequestService contactRequestService) {
        this.contactRequestService = contactRequestService;
    }

    @PostMapping("/")
    public CompletableFuture<ResponseEntity<ContactRequest>> createContactRequest(@RequestBody ContactRequest request) {
        return contactRequestService.saveContactRequest(request)
            .thenApply(ResponseEntity::ok)
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/")
    public CompletableFuture<ResponseEntity<List<ContactRequest>>> getAllRequests() {
        return contactRequestService.getAllRequests()
            .thenApply(ResponseEntity::ok)
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<? extends Object>> getRequestById(@PathVariable String id) {
        return contactRequestService.findRequestById(id)
            .thenApply(request -> {
                if (request != null) {
                    return ResponseEntity.ok(request);
                }
                return ResponseEntity.notFound().build();
            })
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<? extends Object>> updateRequest(
            @PathVariable String id,
            @RequestBody ContactRequest request) {
        return contactRequestService.updateRequest(id, request)
            .thenApply(updatedRequest -> {
                if (updatedRequest != null) {
                    return ResponseEntity.ok(updatedRequest);
                }
                return ResponseEntity.notFound().build();
            })
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<Object>> deleteRequest(@PathVariable String id) {
        return contactRequestService.deleteRequest(id)
            .thenApply(deleted -> {
                if (deleted) {
                    return ResponseEntity.ok().build();
                }
                return ResponseEntity.notFound().build();
            })
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/emisor/{emisorId}")
    public CompletableFuture<ResponseEntity<List<ContactRequest>>> getRequestsByEmisorId(
            @PathVariable String emisorId) {
        return contactRequestService.findRequestsByEmisorId(emisorId)
            .thenApply(ResponseEntity::ok)
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/receiver/{receiverId}")
    public CompletableFuture<ResponseEntity<List<ContactRequest>>> getRequestsByReceiverId(
            @PathVariable String receiverId) {
        return contactRequestService.findRequestsByReceiverId(receiverId)
            .thenApply(ResponseEntity::ok)
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PutMapping("/{id}/accept")
    public CompletableFuture<ResponseEntity<? extends Object>> acceptRequest(@PathVariable String id) {
        return contactRequestService.findRequestById(id)
            .thenCompose(request -> {
                if (request != null) {
                    request.setState(StateRequest.ACCEPTED.toString());
                    return contactRequestService.updateRequest(id, request);
                }
                return CompletableFuture.completedFuture(null);
            })
            .thenApply(request -> {
                if (request != null) {
                    return ResponseEntity.ok(request);
                }
                return ResponseEntity.notFound().build();
            })
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PutMapping("/{id}/reject")
    public CompletableFuture<ResponseEntity<? extends Object>> rejectRequest(@PathVariable String id) {
        return contactRequestService.findRequestById(id)
            .thenCompose(request -> {
                if (request != null) {
                    request.setState(StateRequest.REJECTED.toString());
                    return contactRequestService.updateRequest(id, request);
                }
                return CompletableFuture.completedFuture(null);
            })
            .thenApply(request -> {
                if (request != null) {
                    return ResponseEntity.ok(request);
                }
                return ResponseEntity.notFound().build();
            })
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}