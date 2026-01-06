package com.capstone.repository;

import com.capstone.models.*;

import java.util.*;

public class InMemoryRepo {
    public final Map<String, User> users = new HashMap<>();

    public final Map<String, Project> projects = new HashMap<>();

    public final Map<String, Proposal> proposals = new HashMap<>();

    public final Map<String, Milestone> milestones = new HashMap<>();

    public final Map<String, Comment> comments = new HashMap<>();
}
