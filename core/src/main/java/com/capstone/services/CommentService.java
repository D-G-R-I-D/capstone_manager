package com.capstone.services;
import com.capstone.dao.CommentDAO;
import com.capstone.models.Comment;
import com.capstone.utils.IdGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentService {
    private final CommentDAO dao = new CommentDAO();

    public void add(String projectId, String authorId, String message, String parentId) {
        Comment c = new Comment(
                IdGenerator.id(),       //UUID.randomUUID().toString(),
                projectId,
                authorId,
                message,
                parentId,
                LocalDateTime.now()
        );
        dao.save(c);
    }
}
