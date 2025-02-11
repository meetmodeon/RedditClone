package com.radish.repository;

import com.radish.model.Comment;
import com.radish.model.Post;
import com.radish.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllByUser(User user);

    List<Comment> findByPost(Post post);
}
