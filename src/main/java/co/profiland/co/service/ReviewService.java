package co.profiland.co.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import co.profiland.co.components.ThreadPoolManager;
import co.profiland.co.model.Review;
import co.profiland.co.utils.Utilities;

@Service
public class ReviewService {
    
    private static final String XML_PATH = "C:/td/persistence/models/reviews/review.xml";
    private final Utilities persistence;
    private final ThreadPoolManager threadPool;

    public ReviewService(Utilities persistence) {
        this.persistence = persistence;
        this.threadPool = ThreadPoolManager.getInstance();
        persistence.initializeFile(XML_PATH, new ArrayList<Review>());
    }

    public CompletableFuture<Review> createReview(Review review) {
        return threadPool.submitTask(() -> {
            List<Review> reviews = getAllReviews().get();

            if (review.getId() == null || review.getId().isEmpty()) {
                review.setId(UUID.randomUUID().toString());
            }
            reviews.add(review);

            persistence.serializeObject(XML_PATH, reviews);
            persistence.writeIntoLogger("Review with ID " + review.getId() + " was created - Products UI Section", Level.FINE);
            return review;
        });
    }

    @SuppressWarnings("unchecked")
    public CompletableFuture<List<Review>> getAllReviews() {
        return threadPool.submitTask(() -> {
            Object deserializedData = persistence.deserializeObject(XML_PATH);
            if (deserializedData instanceof List<?>) {
                persistence.writeIntoLogger("Retrieved all reviews successfully", Level.FINE);
                return (List<Review>) deserializedData;
            }
            return new ArrayList<>();
        });
    }

    public CompletableFuture<Review> updateReview(String id, Review updatedReview) {
        return threadPool.submitTask(() -> {
            List<Review> reviews = getAllReviews().get();

            for (int i = 0; i < reviews.size(); i++) {
                if (reviews.get(i).getId().equals(id)) {
                    updatedReview.setId(id);
                    reviews.set(i, updatedReview);
                    persistence.serializeObject(XML_PATH, reviews);
                    persistence.writeIntoLogger("Review with ID " + id + " was updated - Profile UI Section", Level.FINE);
                    return updatedReview;
                }
            }
            persistence.writeIntoLogger("Review with ID " + id + " not found for update - Profile UI Section", Level.WARNING);
            return null;
        });
    }

    public CompletableFuture<Boolean> deleteReview(String id) {
        return threadPool.submitTask(() -> {
            List<Review> reviews = getAllReviews().get();
            boolean removed = reviews.removeIf(review -> review.getId().equals(id));

            if (removed) {
                persistence.serializeObject(XML_PATH, reviews);
                persistence.writeIntoLogger("Review with ID " + id + " was deleted - Profile UI Section", Level.FINE);
            } else {
                persistence.writeIntoLogger("Review with ID " + id + " not found for deletion - Profile UI Section", Level.WARNING);
            }

            return removed;
        });
    }

    public CompletableFuture<Review> findReviewById(String id) {
        return threadPool.submitTask(() -> {
            Review review = getAllReviews().get().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
                
            if (review != null) {
                persistence.writeIntoLogger("Review with ID " + id + " was found", Level.FINE);
            } else {
                persistence.writeIntoLogger("Review with ID " + id + " not found", Level.WARNING);
            }
            return review;
        });
    }

    public CompletableFuture<List<Review>> findReviewsByAuthor(String authorRef) {
        return threadPool.submitTask(() -> {
            List<Review> reviews = getAllReviews().get().stream()
                .filter(r -> r.getAuthorRef().equals(authorRef))
                .collect(Collectors.toList());
                
            persistence.writeIntoLogger("Found " + reviews.size() + " reviews for author " + authorRef, Level.FINE);
            return reviews;
        });
    }

    public CompletableFuture<List<Review>> findReviewsByOwner(String ownerRef) {
        return threadPool.submitTask(() -> {
            List<Review> reviews = getAllReviews().get().stream()
                .filter(r -> r.getOwnerRef().equals(ownerRef))
                .collect(Collectors.toList());
                
            persistence.writeIntoLogger("Found " + reviews.size() + " reviews for owner " + ownerRef, Level.FINE);
            return reviews;
        });
    }

    public CompletableFuture<Double> getAverageCalificationByOwner(String ownerRef) {
        return threadPool.submitTask(() -> {
            List<Review> ownerReviews = findReviewsByOwner(ownerRef).get();
            double average = ownerReviews.stream()
                .mapToInt(Review::getCalification)
                .average()
                .orElse(0.0);
                
            persistence.writeIntoLogger("Calculated average calification for owner " + ownerRef + ": " + average, Level.FINE);
            return average;
        });
    }

    public CompletableFuture<String> convertToJson(List<Review> reviews) {
        return threadPool.submitTask(() -> {
            String json = persistence.convertToJson(reviews);
            persistence.writeIntoLogger("Converted " + reviews.size() + " reviews to JSON", Level.FINE);
            return json;
        });
    }
}