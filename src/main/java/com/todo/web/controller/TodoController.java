package com.todo.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.todo.web.repository.model.TodoDTO;
import com.todo.web.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("todos")
public class TodoController {

  private final TodoService todoService;
  private final ObjectMapper objectMapper;

  @GetMapping
  public ResponseEntity<List<TodoDTO>> getAllTodos() {
    Optional<List<TodoDTO>> todoList = todoService.getAllTodos();
    if (todoList.isPresent()) {
      return ResponseEntity.ok(todoList.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<TodoDTO> getTodoById(@PathVariable Long id) {
    Optional<TodoDTO> todo = todoService.getTodo(id);
    if (todo.isPresent()) {
      return ResponseEntity.ok(todo.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping(consumes = "application/json")
  public ResponseEntity<TodoDTO> createTodo(@RequestBody TodoDTO dto) {
    return ResponseEntity.ok(todoService.addTodo(dto));
  }

  @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
  public ResponseEntity<TodoDTO> updateTodo(@PathVariable Long id, @RequestBody JsonPatch patch) {
    Optional<TodoDTO> todo = todoService.getTodo(id);

    if (!todo.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    try {
      JsonNode patched = patch.apply(objectMapper.convertValue(todo, JsonNode.class));
      TodoDTO updatedTodo = objectMapper.treeToValue(patched, TodoDTO.class);
      return ResponseEntity.ok(todoService.updateTodo(updatedTodo));

    } catch (JsonPatchException | JsonProcessingException e) {
      return ResponseEntity.unprocessableEntity().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity deleteTodoById(@PathVariable Long id) {
    todoService.deleteTodo(id);
    return ResponseEntity.ok().build();
  }
}
