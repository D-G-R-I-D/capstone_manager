package com.capstone.dao;
import com.capstone.models.Project;
import java.util.List;

public interface ProjectDAOInterface {
    void save(Project project);
    Project findById(String id);
    List<Project> findByStudent(String studentId);
    List<Project> findAll();
    boolean update(Project project);
    List<Project> findBySupervisor(String supervisorId);
}