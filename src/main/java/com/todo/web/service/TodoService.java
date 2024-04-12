package com.todo.web.service;

import com.todo.web.repository.model.TodoDTO;
import java.util.List;
import java.util.Optional;

public interface TodoService {

  TodoDTO addTodo(TodoDTO todoDTO);

  void deleteTodo(Long id);

  TodoDTO updateTodo(TodoDTO todoDTO);

  Optional<TodoDTO> getTodo(Long id);

  Optional<List<TodoDTO>> getAllTodos();
}
