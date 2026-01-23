package com.capstone.dao;
import com.capstone.models.Comment;
import java.util.List;

public interface CommentDAOinterface {
    boolean save(Comment comment);
    Comment findById(String id);
    List<Comment> findByProject(String projectId);
    List<Comment> findReplies(String parentId);
    void delete(String id);
}