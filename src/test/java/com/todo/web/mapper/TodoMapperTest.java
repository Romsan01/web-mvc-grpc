package com.todo.web.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.todo.web.TestUtil;
import com.todo.web.repository.entity.Todo;
import com.todo.web.repository.mapper.TodoMapper;
import com.todo.web.repository.model.TodoDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import todo.Todos;

@SpringBootTest
class TodoMapperTest {

  private TodoMapper todoMapper;

  @BeforeEach
  void setUp() {
    todoMapper = Mappers.getMapper(TodoMapper.class);
  }

  @Test
  void toDTOTest() {
    Todo todo = TestUtil.createTodo(1L, "Sample Todo", true);

    TodoDTO todoDTO = todoMapper.toDTO(todo);

    assertNotNull(todoDTO);
    assertEquals(todo.getId(), todoDTO.getId());
    assertEquals(todo.getDescription(), todoDTO.getDescription());
    assertEquals(todo.isCompletionStatus(), todoDTO.isCompletionStatus());
  }

  @Test
  void toEntityTest() {
    TodoDTO todoDTO = TestUtil.createTodoDTO(1L, "Sample Todo", true);

    Todo todo = todoMapper.toEntity(todoDTO);

    assertNotNull(todo);
    assertEquals(todoDTO.getId(), todo.getId());
    assertEquals(todoDTO.getDescription(), todo.getDescription());
    assertEquals(todoDTO.isCompletionStatus(), todo.isCompletionStatus());
  }

  @Test
  void toEntityListTest() {
    List<TodoDTO> dtoList = List.of(TestUtil.createTodoDTO(1L, "Sample Todo DTO", true));
    List<Todo> entityList = todoMapper.toEntityList(dtoList);

    assertNotNull(entityList);
    assertEquals(dtoList.size(), entityList.size());
    assertEquals(dtoList.get(0).getId(), entityList.get(0).getId());
    assertEquals(dtoList.get(0).getDescription(), entityList.get(0).getDescription());
    assertEquals(dtoList.get(0).isCompletionStatus(), entityList.get(0).isCompletionStatus());
  }

  @Test
  void toDtoListTest() {
    List<Todo> entityList = List.of(TestUtil.createTodo(1L, "Sample Todo", true));
    List<TodoDTO> dtoList = todoMapper.toDtoList(entityList);

    assertNotNull(dtoList);
    assertEquals(entityList.size(), dtoList.size());
    assertEquals(entityList.get(0).getId(), dtoList.get(0).getId());
    assertEquals(entityList.get(0).getDescription(), dtoList.get(0).getDescription());
    assertEquals(entityList.get(0).isCompletionStatus(), dtoList.get(0).isCompletionStatus());
  }

  @Test
  void toProtoListTest() {
    List<Todo> todos = List.of(TestUtil.createTodo(1L, "Sample Todo", true));
    Iterable<Todos.TodoDTO> protoList = todoMapper.toProtoList(todos);

    assertNotNull(protoList);
    protoList.forEach(
        proto -> {
          assertEquals(todos.get(0).getId(), proto.getId());
          assertEquals(todos.get(0).getDescription(), proto.getDescription());
          assertEquals(todos.get(0).isCompletionStatus(), proto.getCompletionStatus());
        });
  }
}
