package co.profiland.co.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import co.profiland.co.exception.BackupException;
import co.profiland.co.exception.PersistenceException;
import co.profiland.co.model.Review;
import co.profiland.co.utils.Utilities;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ReviewService {

    private static final String XML_PATH = "src/main/resources/reviews/reviews.xml";
    private final Utilities persistence = Utilities.getInstance();

    public ReviewService() {
        try {
            persistence.initializeFile(XML_PATH, new ArrayList<Review>());
        } catch (BackupException | PersistenceException e) {
            e.printStackTrace();
        }
    }

    public Review saveReview(Review review) throws IOException, ClassNotFoundException {
        List<Review> reviews = getAllReviews();
    
        if (review.getId() == null || review.getId().isEmpty()) {
            review.setId(UUID.randomUUID().toString());
        }

        reviews.add(review);
        persistence.serializeObject(XML_PATH, reviews);

        log.info("Saved Review with ID: {}", review.getId());
        return review;
    }

    @SuppressWarnings("unchecked")
    public List<Review> getAllReviews() throws IOException, ClassNotFoundException {
        Object deserializedData = persistence.deserializeObject(XML_PATH);
        if (deserializedData instanceof List<?>) {
            return (List<Review>) deserializedData;
        }
        return new ArrayList<>();
    }

    public Review updateReview(String id, Review updatedReview) throws IOException, ClassNotFoundException {
        List<Review> reviews = getAllReviews();

        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getId().equals(id)) {
                updatedReview.setId(id);
                reviews.set(i, updatedReview);
                persistence.serializeObject(XML_PATH, reviews);
                log.info("Updated review with ID: {}", id);
                return updatedReview;
            }
        }
        log.warn("Review not found for bro. ID: {}", id);
        return null;
    }

    public boolean deleteReview(String id) throws IOException, ClassNotFoundException {
        List<Review> reviews = getAllReviews();
        boolean removed = reviews.removeIf(review -> review.getId().equals(id));
        if (removed) {
            persistence.serializeObject(XML_PATH, reviews);
            log.info("Deleted review with ID: {}", id);
        } else {
            log.warn("Review not found for deletion. ID: {}", id);
        }
        return removed;
    }

    public List<Review> findReviewsByOwner(String ownerRef) throws IOException, ClassNotFoundException {
        List<Review> reviews = getAllReviews();
        return reviews.stream()
                .filter(review -> review.getOwnerRef().equalsIgnoreCase(ownerRef))
                .collect(Collectors.toList());
    }

    public List<Review> findReviewsByAuthor(String authorRef) throws IOException, ClassNotFoundException {
        List<Review> reviews = getAllReviews();
        return reviews.stream()
                .filter(review -> review.getAuthorRef().equalsIgnoreCase(authorRef))
                .collect(Collectors.toList());
    }

    public Review findReviewById(String id) throws IOException, ClassNotFoundException {
        List<Review> reviews = getAllReviews();
        return reviews.stream()
                .filter(review -> review.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public String convertToJson(List<Review> reviews) throws IOException {
        return persistence.convertToJson(reviews);
    }
}