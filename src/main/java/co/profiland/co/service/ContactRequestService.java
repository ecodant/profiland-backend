package co.profiland.co.service;

import java.io.IOException;

import org.springframework.stereotype.Service;


import java.util.*;

import co.profiland.co.model.ContactRequest;
import co.profiland.co.utils.Utilities;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ContactRequestService {

    private static final String XML_PATH = "src/main/resources/contactRequest/contactRequest.xml";

    private final Utilities persistence = Utilities.getInstance();

    public ContactRequestService() {
        persistence.initializeFile(XML_PATH, new ArrayList<ContactRequest>());
    }

    public ContactRequest createContactRequest(ContactRequest contactRequest) throws IOException {
        List<ContactRequest> contactRequests = getAllContactRequests();

        if (contactRequest.getId() == null || contactRequest.getId().isEmpty()) {
            contactRequest.setId(UUID.randomUUID().toString());
        }
        contactRequests.add(contactRequest);

        persistence.serializeObject(XML_PATH, contactRequests);
        log.info("Saved ContactRequest with ID: {}", contactRequest.getId());
        return contactRequest;
    }

    @SuppressWarnings("unchecked")
    public List<ContactRequest> getAllContactRequests() throws IOException {
        try {
            Object deserializedData = persistence.deserializeObject(XML_PATH);
            if (deserializedData instanceof List<?>) {
                return (List<ContactRequest>) deserializedData;
            }
        } catch (ClassNotFoundException e) {
            log.error("Failed to deserialize ContactRequests", e);
        }
        return new ArrayList<>();
    }

    public ContactRequest updateContactRequest(String id, ContactRequest updatedContactRequest) throws IOException {
        List<ContactRequest> contactRequests = getAllContactRequests();

        for (int i = 0; i < contactRequests.size(); i++) {
            if (contactRequests.get(i).getId().equals(id)) {
                updatedContactRequest.setId(id);
                contactRequests.set(i, updatedContactRequest);
                persistence.serializeObject(XML_PATH, contactRequests);
                log.info("Updated ContactRequest with ID: {}", id);
                return updatedContactRequest;
            }
        }
        log.warn("ContactRequest not found for update. ID: {}", id);
        return null;
    }

    public boolean deleteContactRequest(String id) throws IOException {
        List<ContactRequest> contactRequests = getAllContactRequests();
        boolean removed = contactRequests.removeIf(contactRequest -> contactRequest.getId().equals(id));

        if (removed) {
            persistence.serializeObject(XML_PATH, contactRequests);
            log.info("Deleted ContactRequest with ID: {}", id);
        } else {
            log.warn("ContactRequest not found for deletion. ID: {}", id);
        }

        return removed;
    }

    public ContactRequest findContactRequestBySellerId(String id) throws IOException {
        return getAllContactRequests().stream()
                .filter(contactRequest -> contactRequest.getIdSeller().equals(id))
                .findFirst()
                .orElse(null);
    }

    public String convertToJson(List<ContactRequest> contactRequests) throws IOException {
        return persistence.convertToJson(contactRequests);
    }
}