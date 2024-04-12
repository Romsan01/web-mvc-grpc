package com.todo.web.grpcservice;

import com.todo.web.repository.TodoRepository;
import com.todo.web.repository.entity.Todo;
import com.todo.web.repository.mapper.TodoMapper;
import com.todo.web.service.TodoService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import todo.TodoServiceGrpc;
import todo.Todos;

@GrpcService
@RequiredArgsConstructor
public class TodoGrpc extends TodoServiceGrpc.TodoServiceImplBase {

  private final TodoRepository todoRepository;
  private final TodoService todoService;
  private final TodoMapper todoMapper;

  @Override
  public void getAllTodos(
      Todos.EmptyRequest request, StreamObserver<Todos.TodoListResponse> responseObserver) {
    final var response =
        Todos.TodoListResponse.newBuilder()
            .addAllTodos(todoMapper.toProtoList(todoRepository.findAll()))
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getTodo(Todos.TodoIdRequest request, StreamObserver<Todos.TodoDTO> responseObserver) {
    var todo = todoRepository.findById(request.getId());

    if (todo.isEmpty()) {
      responseObserver.onError(new RuntimeException("Todo not found"));
      return;
    }
    var todoResult = todo.get();
    buildResponse(responseObserver, todoResult);
  }

  private static void buildResponse(
      StreamObserver<Todos.TodoDTO> responseObserver, Todo todoResult) {
    final var response =
        Todos.TodoDTO.newBuilder()
            .setId(todoResult.getId())
            .setDescription(todoResult.getDescription())
            .setCompletionStatus(todoResult.isCompletionStatus())
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void addTodo(Todos.TodoDTO request, StreamObserver<Todos.TodoDTO> responseObserver) {
    var todo =
        Todo.builder()
            .description(request.getDescription())
            .completionStatus(request.getCompletionStatus())
            .build();

    var todoResult = todoRepository.save(todo);

    buildResponse(responseObserver, todoResult);
  }

  @Override
  public void updateTodo(Todos.TodoDTO request, StreamObserver<Todos.TodoDTO> responseObserver) {
    var todo =
        Todo.builder()
            .id(request.getId())
            .description(request.getDescription())
            .completionStatus(request.getCompletionStatus())
            .build();

    var todoResult = todoRepository.save(todo);

    buildResponse(responseObserver, todoResult);
  }

  @Override
  public void deleteTodo(
      Todos.TodoIdRequest request, StreamObserver<Todos.DeleteTodoResponse> responseObserver) {
    todoRepository.deleteById(request.getId());
    responseObserver.onNext(Todos.DeleteTodoResponse.newBuilder().build());
    responseObserver.onCompleted();
  }
}
