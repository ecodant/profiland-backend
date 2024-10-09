package co.profiland.co.service;

import java.io.IOException;

import java.io.File;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import co.profiland.co.model.Comment;
import co.profiland.co.utilities.Persistence;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentService {
    
    private static final String XML_PATH = "src/main/resources/comments/comments.xml";
    private static final String DAT_PATH = "src/main/resources/comments/comments.dat";

    private final Persistence persistence = Persistence.getInstance();
    //private final ObjectMapper jsonMapper = new ObjectMapper(); 

    public Comment saveComment(Comment comment, String format) throws IOException, ClassNotFoundException {
        List<Comment> comments;
        //This part is for the bug that both files needs to existe to save data otherwise didn't work
        String filePath = "xml".equalsIgnoreCase(format) ? XML_PATH : DAT_PATH;

        File file = new File(filePath);
        if (!file.exists()) {
            comments = new ArrayList<>();
            if ("xml".equalsIgnoreCase(format)) {
                persistence.serializeObjectXML(XML_PATH, comments);
            } else {
                persistence.serializeObject(DAT_PATH, comments);
            }
        } else {
            // If the file exists, read the existing sellers
            comments = getAllComments(format);
        }

        comments.add(comment);

        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObjectXML(XML_PATH, comments);
        } else {
            persistence.serializeObject(DAT_PATH, comments);
        }

        return comment;
    }

    // Retrieve all comments by merging XML and DAT files 
    public List<Comment> getAllCommentsMerged() throws IOException, ClassNotFoundException {
        List<Comment> xmlComments = getAllComments("xml");
        List<Comment> datComments = getAllComments("dat");

        // This Set eliminates duplicates based on the seller ID
        Set<String> seenIds = new HashSet<>();
        List<Comment> mergedComments = new ArrayList<>();

        for (Comment comment : xmlComments) {
            if (seenIds.add(comment.getId())) {
                mergedComments.add(comment);
            }
        }

        for (Comment comment : datComments) {
            if (seenIds.add(comment.getId())) {
                mergedComments.add(comment);
            }
        }

        return mergedComments;
    }
    public Comment updateComment(String id, Comment updatedComment, String format) throws IOException, ClassNotFoundException {
        List<Comment> comments = getAllComments(format);

        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).getId().equals(id)) {
                // Preserve the same ID and update other fields
                updatedComment.setId(id);
                comments.set(i, updatedComment);  // Replace existing Comment with updated data
                serializeComments(format, comments);
                return updatedComment;
            }
        }

        return null;  
    }

    // 4. Delete: Remove a seller by ID
    public boolean deleteComment(String id) throws IOException, ClassNotFoundException {
        List<Comment> comments = getAllCommentsMerged();
        boolean removed = comments.removeIf(comment -> comment.getId().equals(id));
        //This was cause an error because if a seller it was delete from one file but not from other
        // So you Edwin set up to save in both format, so check out in the future
        if (removed) {
            serializeComments(id, comments);
            serializeComments("xml", comments);
        }

        return removed;
    }
    // Hlper method for try out if the saving format is working okas
    @SuppressWarnings("unchecked")
    public List<Comment> getAllComments(String format) throws IOException, ClassNotFoundException {
        Object deserializedData;

        if ("xml".equalsIgnoreCase(format)) {
            deserializedData = persistence.deserializeObjectXML(XML_PATH);
        } else {
            deserializedData = persistence.deserializeObject(DAT_PATH);
        }

        if (deserializedData instanceof List<?>) {
            return (List<Comment>) deserializedData;
        } else {
            return new ArrayList<>();
        }
    }
    @SuppressWarnings("unused")
    private void serializeComments(String format, List<Comment> comments) throws IOException {
        if ("xml".equalsIgnoreCase(format)) {
            persistence.serializeObjectXML(XML_PATH, comments);
        } else {
            persistence.serializeObject(DAT_PATH, comments);
        }
    }
    // Convert the Listica into JSON for handling the responses
    public String convertToJson(List<Comment> comments) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(comments);
    }

    public Comment findCommentById(String id) throws IOException, ClassNotFoundException {
        List<Comment> comments = getAllCommentsMerged();
        return comments.stream()
                .filter(comment -> comment.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Comment> findCommentByAuthor(String nameAuthor) throws IOException, ClassNotFoundException {
        List<Comment> comments = getAllCommentsMerged();
        return comments.stream()
                .filter(comment -> comment.getAuthor().getName().equalsIgnoreCase(nameAuthor))
                .collect(Collectors.toList());
    }
}
