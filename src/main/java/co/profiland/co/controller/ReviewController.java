package co.profiland.co.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.profiland.co.model.Review;
import co.profiland.co.service.ReviewService;

@RestController
@RequestMapping("/profiland/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/")
    public ResponseEntity<Review> saveReview(@RequestBody Review review) {
        try {
            Review savedReview = reviewService.saveReview(review);
            return ResponseEntity.ok(savedReview);
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<String> getAllReviews() {
        try {
            List<Review> reviews = reviewService.getAllReviews();
            return ResponseEntity.ok(reviewService.convertToJson(reviews));
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable String id) {
        try {
            Review review = reviewService.findReviewById(id);
            return review != null ? ResponseEntity.ok(review) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/owner/{ownerRef}")
    public ResponseEntity<String> getReviewsByOwner(@PathVariable String ownerRef) {
        try {
            List<Review> reviews = reviewService.findReviewsByOwner(ownerRef);
            return ResponseEntity.ok(reviewService.convertToJson(reviews));
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/author/{authorRef}")
    public ResponseEntity<String> getReviewsByAuthor(@PathVariable String authorRef) {
        try {
            List<Review> reviews = reviewService.findReviewsByAuthor(authorRef);
            return ResponseEntity.ok(reviewService.convertToJson(reviews));
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable String id, @RequestBody Review review) {
        try {
            Review updatedReview = reviewService.updateReview(id, review);
            return updatedReview != null ? ResponseEntity.ok(updatedReview) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

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