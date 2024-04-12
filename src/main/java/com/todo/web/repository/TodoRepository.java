package com.todo.web.repository;

import com.todo.web.repository.entity.Todo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
  Optional<Todo> findByDescription(String description);
}
