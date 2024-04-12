package com.todo.web.repository.mapper;

import com.todo.web.repository.entity.Todo;
import com.todo.web.repository.model.TodoDTO;
import java.util.List;
import org.mapstruct.Mapper;
import todo.Todos;

@Mapper(componentModel = "spring")
public interface TodoMapper {
  TodoDTO toDTO(Todo todo);

  Todo toEntity(TodoDTO todoDTO);

  List<Todo> toEntityList(List<TodoDTO> dtoList);

  List<TodoDTO> toDtoList(List<Todo> entityList);

  Iterable<Todos.TodoDTO> toProtoList(List<Todo> all);
}
