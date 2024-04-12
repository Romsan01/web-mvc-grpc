package com.todo.web;

import com.todo.web.repository.entity.Todo;
import com.todo.web.repository.model.TodoDTO;

public class TestUtil {
  public static TodoDTO createTodoDTO(Long id, String name, boolean completed) {
    TodoDTO todoDTO = new TodoDTO();
    todoDTO.setId(id);
    todoDTO.setDescription(name);
    todoDTO.setCompletionStatus(completed);

    return todoDTO;
  }

  public static Todo createTodo(Long id, String name, boolean completed) {
    Todo todo = new Todo();
    todo.setId(id);
    todo.setDescription(name);
    todo.setCompletionStatus(completed);

    return todo;
  }
}
