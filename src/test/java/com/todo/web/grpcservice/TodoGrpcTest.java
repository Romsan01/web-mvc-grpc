package com.todo.web.grpcservice;

import static org.mockito.Mockito.*;

import com.todo.web.repository.TodoRepository;
import com.todo.web.repository.entity.Todo;
import com.todo.web.repository.mapper.TodoMapper;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import todo.Todos;

class TodoGrpcTest {

  @Mock private TodoRepository todoRepository;

  @Mock private TodoMapper todoMapper;

  @Mock private StreamObserver<Todos.TodoListResponse> todoListResponseObserver;

  @Mock private StreamObserver<Todos.TodoDTO> todoDtoResponseObserver;

  @Mock private StreamObserver<Todos.DeleteTodoResponse> deleteTodoResponseObserver;

  @InjectMocks private TodoGrpc todoGrpc;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void getAllTodosTest() {
    List<Todo> todos = List.of(new Todo(1L, "Sample Todo", false));
    when(todoRepository.findAll()).thenReturn(todos);
    when(todoMapper.toProtoList(todos))
        .thenReturn(
            List.of(
                Todos.TodoDTO.newBuilder()
                    .setDescription("Sample Todo")
                    .setCompletionStatus(false)
                    .build()));

    todoGrpc.getAllTodos(Todos.EmptyRequest.newBuilder().build(), todoListResponseObserver);

    verify(todoRepository).findAll();
    verify(todoMapper).toProtoList(todos);
    verify(todoListResponseObserver).onNext(any(Todos.TodoListResponse.class));
    verify(todoListResponseObserver).onCompleted();
  }

  @Test
  void getTodoNotFoundTest() {
    when(todoRepository.findById(any())).thenReturn(Optional.empty());

    todoGrpc.getTodo(Todos.TodoIdRequest.newBuilder().setId(1).build(), todoDtoResponseObserver);

    verify(todoRepository).findById(1L);
    verify(todoDtoResponseObserver).onError(any(RuntimeException.class));
  }

  @Test
  void getTodoFoundTest() {
    Todo todo = new Todo(1L, "Sample Todo", true);
    when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

    todoGrpc.getTodo(Todos.TodoIdRequest.newBuilder().setId(1).build(), todoDtoResponseObserver);

    verify(todoRepository).findById(1L);
    verify(todoDtoResponseObserver).onNext(any(Todos.TodoDTO.class));
    verify(todoDtoResponseObserver).onCompleted();
  }

  @Test
  void addTodoTest() {
    Todo todo = new Todo(1L, "Sample Todo", true);
    when(todoRepository.save(any(Todo.class))).thenReturn(todo);

    todoGrpc.addTodo(
        Todos.TodoDTO.newBuilder().setDescription("Sample Todo").setCompletionStatus(true).build(),
        todoDtoResponseObserver);

    verify(todoRepository).save(any(Todo.class));
    verify(todoDtoResponseObserver).onNext(any(Todos.TodoDTO.class));
    verify(todoDtoResponseObserver).onCompleted();
  }

  @Test
  void updateTodoTest() {
    Todo todo = new Todo(1L, "Updated Todo", true);
    when(todoRepository.save(any(Todo.class))).thenReturn(todo);

    todoGrpc.updateTodo(
        Todos.TodoDTO.newBuilder()
            .setId(1)
            .setDescription("Updated Todo")
            .setCompletionStatus(true)
            .build(),
        todoDtoResponseObserver);

    verify(todoRepository).save(todo);
    verify(todoDtoResponseObserver).onNext(any(Todos.TodoDTO.class));
    verify(todoDtoResponseObserver).onCompleted();
  }

  @Test
  void deleteTodoTest() {
    todoGrpc.deleteTodo(
        Todos.TodoIdRequest.newBuilder().setId(1).build(), deleteTodoResponseObserver);

    verify(todoRepository).deleteById(1L);
    verify(deleteTodoResponseObserver).onNext(any(Todos.DeleteTodoResponse.class));
    verify(deleteTodoResponseObserver).onCompleted();
  }
}
