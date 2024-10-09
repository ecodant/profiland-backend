package co.profiland.co.service;

import java.io.IOException;

import java.io.File;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import co.profiland.co.model.Review;
import co.profiland.co.utilities.Persistence;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService {
    
    private static final String XML_PATH = "src/main/resources/reviews/reviews.xml";
    private static final String DAT_PATH = "src/main/resources/reviews/reviews.dat";

    private final Persistence persistence = Persistence.getInstance();
    //private final ObjectMapper jsonMapper = new ObjectMapper(); 

    public Review saveReview(Review review, String format) throws IOException, ClassNotFoundException {
        List<Review> reviews;
        //This part is for the bug that both files needs to existe to save data otherwise didn't work
        String filePath = "xml".equalsIgnoreCase(format) ? XML_PATH : DAT_PATH;

        File file = new File(filePath);
        if (!file.exists()) {
            reviews = new ArrayList<>();
            if ("xml".equalsIgnoreCase(format)) {
                persistence.serializeObjectXML(XML_PATH, reviews);
            } else {
                persistence.serializeObject(DAT_PATH, reviews);
            }
        } else {
            // If the file exists, read the existing sellers
            reviews = getAllReviews(format);
        }

        reviews.add(review);

        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObjectXML(XML_PATH, reviews);
        } else {
            persistence.serializeObject(DAT_PATH, reviews);
        }

        return review;
    }

    // Retrieve all sellers by merging XML and DAT files 
    public List<Review> getAllReviewsMerged() throws IOException, ClassNotFoundException {
        List<Review> xmlReviews = getAllReviews("xml");
        List<Review> datReviews = getAllReviews("dat");

        // This Set eliminates duplicates based on the seller ID
        Set<String> seenIds = new HashSet<>();
        List<Review> mergedReviews = new ArrayList<>();

        for (Review review : xmlReviews) {
            if (seenIds.add(review.getId())) {
                mergedReviews.add(review);
            }
        }

        for (Review review : datReviews) {
            if (seenIds.add(review.getId())) {
                mergedReviews.add(review);
            }
        }

        return mergedReviews;
    }
    public Review updateReview(String id, Review updatedReview, String format) throws IOException, ClassNotFoundException {
        List<Review> reviews = getAllReviews(format);

        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getId().equals(id)) {
                // Preserve the same ID and update other fields
                updatedReview.setId(id);
                reviews.set(i, updatedReview);  // Replace existing Review with updated data
                serializeReviews(format, reviews);
                return updatedReview;
            }
        }

        return null;  
    }

    // 4. Delete: Remove a seller by ID
    public boolean deleteReview(String id) throws IOException, ClassNotFoundException {
        List<Review> reviews = getAllReviewsMerged();
        boolean removed = reviews.removeIf(Review -> Review.getId().equals(id));
        //This was cause an error because if a seller it was delete from one file but not from other
        // So you Edwin set up to save in both format, so check out in the future
        if (removed) {
            serializeReviews(id, reviews);
            serializeReviews("xml", reviews);
        }

        return removed;
    }
    // Hlper method for try out if the saving format is working okas
    @SuppressWarnings("unchecked")
    public List<Review> getAllReviews(String format) throws IOException, ClassNotFoundException {
        Object deserializedData;

        if ("xml".equalsIgnoreCase(format)) {
            deserializedData = persistence.deserializeObjectXML(XML_PATH);
        } else {
            deserializedData = persistence.deserializeObject(DAT_PATH);
        }

        if (deserializedData instanceof List<?>) {
            return (List<Review>) deserializedData;
        } else {
            return new ArrayList<>();
        }
    }
    @SuppressWarnings("unused")
    private void serializeReviews(String format, List<Review> reviews) throws IOException {
        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObjectXML(XML_PATH, reviews);
        } else {
            persistence.serializeObject(DAT_PATH, reviews);
        }
    }
    // Convert the Listica into JSON for handling the responses
    public String convertToJson(List<Review> reviews) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(reviews);
    }

    public Review findReviewById(String id) throws IOException, ClassNotFoundException {
        List<Review> reviews = getAllReviewsMerged();
        return reviews.stream()
                .filter(review -> review.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Review> findReviewByAuthor(String nameAuthor) throws IOException, ClassNotFoundException {
        List<Review> reviews = getAllReviewsMerged();
        return reviews.stream()
                .filter(review -> review.getAuthor().getName().equalsIgnoreCase(nameAuthor))
                .collect(Collectors.toList());
    }
}
