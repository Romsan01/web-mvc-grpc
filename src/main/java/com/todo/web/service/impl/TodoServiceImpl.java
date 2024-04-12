package com.todo.web.service.impl;

import com.todo.web.repository.TodoRepository;
import com.todo.web.repository.mapper.TodoMapper;
import com.todo.web.repository.model.TodoDTO;
import com.todo.web.service.TodoService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

  private final TodoRepository todoRepository;
  private final TodoMapper todoMapper;

  @Override
  @Transactional
  public TodoDTO addTodo(TodoDTO todoDTO) {
    return todoMapper.toDTO(todoRepository.save(todoMapper.toEntity(todoDTO)));
  }

  @Override
  @Transactional
  public void deleteTodo(Long id) {
    todoRepository.deleteById(id);
  }

  @Override
  @Transactional
  public TodoDTO updateTodo(TodoDTO todoDTO) {
    return todoMapper.toDTO(todoRepository.save(todoMapper.toEntity(todoDTO)));
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<TodoDTO> getTodo(Long id) {
    var todo = todoRepository.findById(id);
    if (todo.isPresent()) {
      return Optional.of(todoMapper.toDTO(todo.get()));
    }
    return Optional.empty();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<List<TodoDTO>> getAllTodos() {
    var todoList = todoRepository.findAll();
    if (todoList != null && !todoList.isEmpty()) {
      return Optional.of(todoMapper.toDtoList(todoList));
    }
    return Optional.empty();
  }
}
