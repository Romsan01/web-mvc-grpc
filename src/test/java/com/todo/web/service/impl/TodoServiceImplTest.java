package com.todo.web.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.todo.web.TestUtil;
import com.todo.web.repository.TodoRepository;
import com.todo.web.repository.entity.Todo;
import com.todo.web.repository.mapper.TodoMapper;
import com.todo.web.repository.model.TodoDTO;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

  @Mock private TodoRepository todoRepository;

  @Mock private TodoMapper todoMapper;

  @InjectMocks private TodoServiceImpl todoService;

  @Test
  void addTodoTest() {
    Todo todo = TestUtil.createTodo(1L, "Test Description", false);
    TodoDTO todoDTO = TestUtil.createTodoDTO(1L, "Test Description", false);
    when(todoMapper.toEntity(any(TodoDTO.class))).thenReturn(todo);
    when(todoRepository.save(any(Todo.class))).thenReturn(todo);
    when(todoMapper.toDTO(any(Todo.class))).thenReturn(todoDTO);

    TodoDTO result = todoService.addTodo(todoDTO);

    assertThat(result).isNotNull();
    assertThat(result.getDescription()).isEqualTo(todoDTO.getDescription());
    verify(todoRepository).save(todo);
    verify(todoMapper).toDTO(todo);
  }

  @Test
  void deleteTodoTest() {
    Long id = 1L;

    todoService.deleteTodo(id);

    verify(todoRepository).deleteById(id);
  }

  @Test
  void updateTodoTest() {
    Todo todo = TestUtil.createTodo(1L, "Updated Description", true);
    TodoDTO todoDTO = TestUtil.createTodoDTO(1L, "Updated Description", true);
    when(todoMapper.toEntity(any(TodoDTO.class))).thenReturn(todo);
    when(todoRepository.save(any(Todo.class))).thenReturn(todo);
    when(todoMapper.toDTO(any(Todo.class))).thenReturn(todoDTO);

    TodoDTO result = todoService.updateTodo(todoDTO);

    assertThat(result).isNotNull();
    assertThat(result.getDescription()).isEqualTo("Updated Description");
    verify(todoRepository).save(todo);
    verify(todoMapper).toDTO(todo);
  }

  @Test
  void getTodoTest() {
    Long id = 1L;
    Todo todo = TestUtil.createTodo(id, "Test Description", false);
    TodoDTO todoDTO = TestUtil.createTodoDTO(id, "Test Description", false);
    when(todoRepository.findById(id)).thenReturn(Optional.of(todo));
    when(todoMapper.toDTO(any(Todo.class))).thenReturn(todoDTO);

    Optional<TodoDTO> result = todoService.getTodo(id);

    assertThat(result).isPresent();
    assertThat(result.get().getDescription()).isEqualTo("Test Description");
    verify(todoRepository).findById(id);
    verify(todoMapper).toDTO(todo);
  }

  @Test
  void getAllTodosTest() {
    List<Todo> todos = List.of(TestUtil.createTodo(1L, "Test Todo", true));
    List<TodoDTO> todoDTOs = List.of(TestUtil.createTodoDTO(1L, "Test Todo", true));
    when(todoRepository.findAll()).thenReturn(todos);
    when(todoMapper.toDtoList(todos)).thenReturn(todoDTOs);

    Optional<List<TodoDTO>> result = todoService.getAllTodos();

    assertThat(result).isNotEmpty();
    assertThat(result.get().size()).isEqualTo(1);
    assertThat(result.get().get(0).getDescription()).isEqualTo("Test Todo");
    verify(todoRepository).findAll();
    verify(todoMapper).toDtoList(todos);
  }
}
