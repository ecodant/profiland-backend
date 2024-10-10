package co.profiland.co.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import co.profiland.co.model.Comment;
import co.profiland.co.utilities.Persistence;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommentService {

    private static final String XML_PATH = "src/main/resources/comments/comments.xml";
    private final Persistence persistence = Persistence.getInstance();

    public CommentService(){
        persistence.initializeXmlFile(XML_PATH, new ArrayList<Comment>());
    }

    public Comment saveComment(Comment comment) throws IOException, ClassNotFoundException {
        List<Comment> comments = getAllComments();

        if (comment.getId() == null || comment.getId().isEmpty()) {
            comment.setId(UUID.randomUUID().toString());
        }

        comments.add(comment);
        persistence.serializeObjectXML(XML_PATH, comments);

        return comment;
    }

    @SuppressWarnings("unchecked")
    public List<Comment> getAllComments() throws IOException, ClassNotFoundException {
        try {
            Object deserializedData = persistence.deserializeObjectXML(XML_PATH);
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
                persistence.serializeObjectXML(XML_PATH, comments);
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
            persistence.serializeObjectXML(XML_PATH, comments);
            log.info("Deleted comment with the ID: {}", id);
        } else {
            log.warn("Comment not found. its ID is: {}", id);
        }
        return removed;
    }

    public List<Comment> findCommentsByProductRef(String productRef) throws IOException, ClassNotFoundException {
        List<Comment> comments = getAllComments();
        return comments.stream()
                .filter(comment -> comment.getProductRef().equalsIgnoreCase(productRef))
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