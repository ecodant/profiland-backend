package mainClasses;

import java.time.LocalDateTime;

public class Comment {
    private Seller author;
    private String text;
    private LocalDateTime dateComment;

    public Comment(){

    }

    public Seller getAuthor() {
        return author;
    }

    public void setAuthor(Seller author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDateComment() {
        return dateComment;
    }

    public void setDateComment(LocalDateTime dateComment) {
        this.dateComment = dateComment;
    }
}
