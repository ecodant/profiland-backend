package co.profiland.co.service;


import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

import co.profiland.co.components.ThreadPoolManager;
import co.profiland.co.exception.BackupException;
import co.profiland.co.exception.PersistenceException;
import co.profiland.co.exception.RequestNotFoundException;
import co.profiland.co.model.ContactRequest;
import co.profiland.co.model.StateRequest;
import co.profiland.co.utils.Utilities;

@Service
public class ContactRequestService {

    private static final String XML_PATH = "C:/td/persistence/models/contact-requests";
    private final String ON_HOLD_PATH = XML_PATH + "/on_hold_requests.xml";
    private final String ACCEPTED_PATH = XML_PATH + "/accepted_requests.xml";
    private final String REJECTED_PATH = XML_PATH + "/rejected_requests.xml";
    private final String LOG_PATH = "C:/td/persistence/log/Profiland_Log.log";

    private final ThreadPoolManager threadPool = ThreadPoolManager.getInstance();
    private final Utilities persistence = Utilities.getInstance();

    public ContactRequestService() {
        try {
            persistence.initializeFile(ON_HOLD_PATH, new ArrayList<ContactRequest>());
            persistence.initializeFile(ACCEPTED_PATH, new ArrayList<ContactRequest>());
            persistence.initializeFile(REJECTED_PATH, new ArrayList<ContactRequest>());
        } catch (BackupException | PersistenceException e) {
            e.printStackTrace();
        }
        Utilities.setupLogger(LOG_PATH);
    }

    // Save a contact request and log the operation
    public CompletableFuture<ContactRequest> saveContactRequest(ContactRequest request) {
        return threadPool.submitTask(() -> {
            List<ContactRequest> requests = getRequestsList(ON_HOLD_PATH);

            if (request.getId() == null || request.getId().isEmpty()) {
                request.setId(UUID.randomUUID().toString());
            }
            
            if (request.getState() == null) {
                request.setState(StateRequest.ON_HOLD.toString());
            }

            requests.add(request);
            persistence.serializeObject(ON_HOLD_PATH, requests);
            persistence.writeIntoLogger(
                String.format("Contact request from '%s' to '%s' was saved", 
                    request.getIdEmisor(), request.getIdReciver()),
                Level.INFO
            );

            return request;
        }).exceptionally(ex -> {
            persistence.writeIntoLogger("Error saving contact request", Level.SEVERE);
            throw new RuntimeException("Failed to save contact request", ex);
        });
    }

    // Fetch all contact requests
    public CompletableFuture<List<ContactRequest>> getAllRequests() {
        return threadPool.submitTask(() -> {
            List<ContactRequest> mergedList = new ArrayList<>();
            mergedList.addAll(getRequestsList(ON_HOLD_PATH));
            mergedList.addAll(getRequestsList(ACCEPTED_PATH));
            mergedList.addAll(getRequestsList(REJECTED_PATH));

            return mergedList;
        }).exceptionally(ex -> {
            persistence.writeIntoLogger("Error retrieving contact requests", Level.WARNING);
            return new ArrayList<>();
        });
    }

    // Find a contact request by ID
    public CompletableFuture<ContactRequest> findRequestById(String id) {
        return getAllRequests().thenApply(requests -> {
            try {
                return requests.stream()
                        .filter(request -> request.getId().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new RequestNotFoundException("Contact request not found: " + id));
            } catch (RequestNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }).exceptionally(ex -> {
            persistence.writeIntoLogger("Error finding contact request by ID: " + id, Level.WARNING);
            return null;
        });
    }

    // Update contact request and handle state transition
    public CompletableFuture<ContactRequest> updateRequest(String id, ContactRequest updatedRequest) {
        return getAllRequests().thenApply(requests -> {
            for (ContactRequest request : requests) {
                if (request.getId().equals(id)) {
                    updatedRequest.setId(id);
                    moveRequestBetweenLists(updatedRequest);
                    return updatedRequest;
                }
            }
            try {
                throw new RequestNotFoundException("Contact request with ID " + id + " not found");
            } catch (RequestNotFoundException e) {
                e.printStackTrace();
            }
            return updatedRequest;
        }).exceptionally(ex -> {
            persistence.writeIntoLogger("Error updating contact request", Level.SEVERE);
            throw new RuntimeException("Failed to update contact request", ex);
        });
    }

    // Move requests between lists based on StateRequest enum
    private void moveRequestBetweenLists(ContactRequest request) {
        StateRequest state = StateRequest.valueOf(request.getState());

        switch (state) {
            case ACCEPTED:
                moveRequest(ON_HOLD_PATH, ACCEPTED_PATH, request);
                break;
            case REJECTED:
                moveRequest(ON_HOLD_PATH, REJECTED_PATH, request);
                break;
            case ON_HOLD:
                // No need to move for on hold state
                break;
            default:
                persistence.writeIntoLogger("Invalid request state: " + state, Level.WARNING);
                break;
        }
    }

    // Move request between two lists and serialize them
    private void moveRequest(String fromPath, String toPath, ContactRequest request) {
        List<ContactRequest> fromList = getRequestsList(fromPath);
        List<ContactRequest> toList = getRequestsList(toPath);

        if (fromList.removeIf(r -> r.getId().equals(request.getId()))) {
            persistence.serializeObject(fromPath, fromList);
            toList.add(request);
            persistence.serializeObject(toPath, toList);
        } else {
            persistence.writeIntoLogger("Contact request not found in source list: " + request.getId(), Level.WARNING);
        }
    }

    // Delete a contact request by ID
    public CompletableFuture<Boolean> deleteRequest(String id) {
        return findRequestById(id).thenApply(request -> {
            boolean deleted;
            StateRequest state = StateRequest.valueOf(request.getState());

            switch (state) {
                case ACCEPTED:
                    deleted = deleteFromList(ACCEPTED_PATH, id);
                    break;
                case REJECTED:
                    deleted = deleteFromList(REJECTED_PATH, id);
                    break;
                default:
                    deleted = deleteFromList(ON_HOLD_PATH, id);
                    break;
            }

            if (deleted) {
                persistence.writeIntoLogger("Contact request with ID '" + id + "' deleted", Level.INFO);
            }
            return deleted;
        }).exceptionally(ex -> {
            persistence.writeIntoLogger("Error deleting contact request", Level.SEVERE);
            return false;
        });
    }

    // Helper to delete from a specific list
    private boolean deleteFromList(String path, String id) {
        List<ContactRequest> requests = getRequestsList(path);
        boolean removed = requests.removeIf(r -> r.getId().equals(id));

        if (removed) {
            persistence.serializeObject(path, requests);
        }
        return removed;
    }

    // Fetch requests from a given path
    @SuppressWarnings("unchecked")
    private List<ContactRequest> getRequestsList(String path) {
        Object data = persistence.deserializeObject(path);
        return (data instanceof List<?>) ? (List<ContactRequest>) data : new ArrayList<>();
    }

    // Find requests by emisor ID
    public CompletableFuture<List<ContactRequest>> findRequestsByEmisorId(String emisorId) {
        return getAllRequests().thenApply(requests -> 
            requests.stream()
                    .filter(request -> request.getIdEmisor().equals(emisorId))
                    .collect(Collectors.toList())
        ).exceptionally(ex -> {
            persistence.writeIntoLogger("Error searching for requests by emisor ID: " + emisorId, Level.WARNING);
            return new ArrayList<>();
        });
    }

    // Find requests by receiver ID
    public CompletableFuture<List<ContactRequest>> findRequestsByReceiverId(String receiverId) {
        return getAllRequests().thenApply(requests -> 
            requests.stream()
                    .filter(request -> request.getIdReciver().equals(receiverId))
                    .collect(Collectors.toList())
        ).exceptionally(ex -> {
            persistence.writeIntoLogger("Error searching for requests by receiver ID: " + receiverId, Level.WARNING);
            return new ArrayList<>();
        });
    }
}