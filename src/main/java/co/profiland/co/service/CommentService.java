package co.profiland.co.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import co.profiland.co.exception.BackupException;
import co.profiland.co.exception.PersistenceException;
import co.profiland.co.model.Comment;
import co.profiland.co.utils.Utilities;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommentService {

    private static final String XML_PATH = "src/main/resources/comments/comments.xml";
    private final Utilities persistence = Utilities.getInstance();

    public CommentService(){
        try {
            persistence.initializeFile(XML_PATH, new ArrayList<Comment>());
        } catch (BackupException | PersistenceException e) {
            
            e.printStackTrace();
        }
    }

    public Comment saveComment(Comment comment) throws IOException, ClassNotFoundException {
        List<Comment> comments = getAllComments();

        if (comment.getId() == null || comment.getId().isEmpty()) {
            comment.setId(UUID.randomUUID().toString());
        }

        comments.add(comment);
        persistence.serializeObject(XML_PATH, comments);

        return comment;
    }

    @SuppressWarnings("unchecked")
    public List<Comment> getAllComments() throws IOException, ClassNotFoundException {
        try {
            Object deserializedData = persistence.deserializeObject(XML_PATH);
            if (deserializedData instanceof List<?>) {
                return (List<Comment>) deserializedData;
            }
        } catch (Exception e) {
            log.error("Failed to deserialize comments", e);
        }
        return new ArrayList<>();
       
    }

    public Comment updateComment(String id, Comment updatedComment) throws IOException, ClassNotFoundException {
        List<Comment> comments = getAllComments();

        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).getId().equals(id)) {
                updatedComment.setId(id);
                comments.set(i, updatedComment);
                persistence.serializeObject(XML_PATH, comments);
                return updatedComment;
            }
        }
        log.warn("Review not found. with the ID: {}", id);
        return null;
    }

    public boolean deleteComment(String id) throws IOException, ClassNotFoundException {
        List<Comment> comments = getAllComments();
        boolean removed = comments.removeIf(comment -> comment.getId().equals(id));
        if (removed) {
            persistence.serializeObject(XML_PATH, comments);
            log.info("Deleted comment with the ID: {}", id);
        } else {
            log.warn("Comment not found. its ID is: {}", id);
        }
        return removed;
    }

    public List<Comment> findCommentsByAuthor(String author) throws IOException, ClassNotFoundException {
        List<Comment> comments = getAllComments();
        return comments.stream()
                .filter(comment -> comment.getAuthor().equalsIgnoreCase(author))
                .collect(Collectors.toList());
    }

    public Comment findCommentById(String id) throws IOException, ClassNotFoundException {
        List<Comment> comments = getAllComments();
        return comments.stream()
                .filter(comment -> comment.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public String convertToJson(List<Comment> comments) throws IOException {
        return persistence.convertToJson(comments);
    }
}