package co.profiland.co.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.profiland.co.model.Comment;
import co.profiland.co.service.CommentService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/profiland/Comments") 
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // Create: Save a new Comment
    @PostMapping("/save")
    public ResponseEntity<Comment> saveComment(@RequestBody Comment comment,
                                             @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            Comment savedComment = commentService.saveComment(comment, format);
            return ResponseEntity.ok(savedComment);
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Read: Get all Comments
    @GetMapping("/")
    public ResponseEntity<String> getAllComments() {
        try {
            List<Comment> comments = commentService.getAllCommentsMerged();
            return ResponseEntity.ok(commentService.convertToJson(comments));
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/find-by-id")
    public ResponseEntity<Comment> getCommentById(@RequestParam("id") String id) {
        try {
            Comment comment = commentService.findCommentById(id);
            return comment != null ? ResponseEntity.ok(comment) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable String id,
                                               @RequestBody Comment comment,
                                               @RequestParam(name = "format", required = false, defaultValue = "dat") String format) {
        try {
            Comment updatedComment = commentService.updateComment(id, comment, format);
            return updatedComment != null ? ResponseEntity.ok(updatedComment) : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Delete: Delete a Comment by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable String id) {
        try {
            boolean isDeleted = commentService.deleteComment(id);
            return isDeleted ? ResponseEntity.ok("Comment deleted successfully ;D") : ResponseEntity.notFound().build();
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
