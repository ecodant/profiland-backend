package co.profiland.co.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.model.Review;
import co.profiland.co.service.ReviewService;


import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/profiland/Reviews") 
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Create: Save a new Review
    @PostMapping("/save")
    public ResponseEntity<Review> saveReview(@RequestBody Review review,
                                             @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            Review savedReview = reviewService.saveReview(review, format);
            return ResponseEntity.ok(savedReview);
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Read: Get all Reviews
    @GetMapping("/")
    public ResponseEntity<String> getAllReviews() {
        try {
            List<Review> reviews = reviewService.getAllReviewsMerged();
            return ResponseEntity.ok(reviewService.convertToJson(reviews));
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/find-by-id")
    public ResponseEntity<Review> getReviewById(@RequestParam("id") String id) {
        try {
            Review Review = reviewService.findReviewById(id);
            return Review != null ? ResponseEntity.ok(Review) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable String id,
                                               @RequestBody Review review,
                                               @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            Review updatedReview = reviewService.updateReview(id, review, format);
            return updatedReview != null ? ResponseEntity.ok(updatedReview) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Delete: Delete a Review by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable String id) {
        try {
            boolean isDeleted = reviewService.deleteReview(id);
            return isDeleted ? ResponseEntity.ok("Review deleted successfully ;D") : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
