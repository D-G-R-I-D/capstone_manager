package com.capstone.services;
import com.capstone.dao.CommentDAO;
import com.capstone.models.Comment;
import com.capstone.utils.IdGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CommentService {
    private final CommentDAO commentDAO = new CommentDAO();

    public void add(String  projectId, String authorId, String message, String parentId) {
        try {
            Comment c = new Comment(
                    IdGenerator.generateId(),       //UUID.randomUUID().toString(),
                    projectId,
                    authorId,
                    message,
                    parentId,
                    LocalDateTime.now()
            );
            commentDAO.save(c);
        } catch (Exception e) {
            System.out.println("could not add / Debug: comment service");
            throw new RuntimeException(e);
        }
    }

    public List<Comment> getByProject(String projectId) {
        return commentDAO.findByProject(projectId);
    }

    public void addComment(Comment comment) {
        commentDAO.save(comment); // Matches your DAO's save method
    }
}