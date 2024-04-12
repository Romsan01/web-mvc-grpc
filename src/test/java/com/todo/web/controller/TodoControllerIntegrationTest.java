package com.todo.web.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.todo.web.TestUtil;
import com.todo.web.repository.TodoRepository;
import com.todo.web.repository.model.TodoDTO;
import com.todo.web.service.impl.TodoServiceImpl;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
      "grpc.server.inProcessName=test", // Enable inProcess server
      "grpc.server.port=-1", // Disable external server
      "grpc.client.inProcess.address=in-process:test" // Configure the client to connect to the
      // inProcess server
    })
@DirtiesContext // Ensures that the grpc-server is properly shutdown after each test
// Avoids "port already in use" during tests
public class TodoControllerIntegrationTest {

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private TodoServiceImpl todoService;

  @Autowired private TodoRepository todoRepository;

  @Test
  public void testGetAllTodos() {

    todoService.addTodo(new TodoDTO(null, "Test Task 1", false));
    todoService.addTodo(new TodoDTO(null, "Test Task 2", false));
    todoService.addTodo(new TodoDTO(null, "Test Task 3", false));
    todoService.addTodo(new TodoDTO(null, "Test Task 4", false));

    ResponseEntity<List<TodoDTO>> response =
        restTemplate.exchange(
            "http://localhost:" + port + "/todos",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<TodoDTO>>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertFalse(response.getBody().isEmpty(), "The todo list should not be empty");
    assertEquals(4, response.getBody().size(), "Expected 4 todos in the list");
  }

  @Test
  public void testGetTodoById() {
    TodoDTO todoDTO = todoService.addTodo(new TodoDTO(null, "Test Task 5", false));
    ResponseEntity<TodoDTO> response =
        restTemplate.exchange(
            "http://localhost:" + port + "/todos/" + todoDTO.getId(),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<TodoDTO>() {});

    TodoDTO todo = response.getBody();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(todo);
    assertEquals(todoDTO.getId(), todo.getId());
  }

  @Test
  public void testCreateTodo() {
    TodoDTO todoDTO = TestUtil.createTodoDTO(null, "Test Task 6", false);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<TodoDTO> request = new HttpEntity<>(todoDTO, headers);

    ResponseEntity<TodoDTO> response =
        restTemplate.postForEntity("http://localhost:" + port + "/todos", request, TodoDTO.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getId()); // Check if ID is generated
    assertEquals(todoDTO.getDescription(), response.getBody().getDescription());
    assertFalse(response.getBody().isCompletionStatus());
  }

  @Test
  public void testUpdateTodo() {
    TodoDTO todoDTO = todoService.addTodo(new TodoDTO(null, "Update Test", false));

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json-patch+json");
    String updateDescription = "Updated Task new";

    HashMap<String, String> patch = new HashMap<>();
    patch.put("op", "replace");
    patch.put("path", "/description");
    patch.put("value", updateDescription);

    HttpEntity request = new HttpEntity<>(Arrays.asList(patch), headers);

    ResponseEntity<TodoDTO> response =
        restTemplate.exchange(
            "http://localhost:" + port + "/todos/" + todoDTO.getId(),
            HttpMethod.PATCH,
            request,
            TodoDTO.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(todoDTO.getId(), response.getBody().getId());
    assertEquals(updateDescription, response.getBody().getDescription());
    assertEquals(todoDTO.isCompletionStatus(), response.getBody().isCompletionStatus());
  }

  @Test
  public void testAddAndDeleteTodo() {
    TodoDTO newTodo = new TodoDTO(null, "Test Task", false);
    ResponseEntity<TodoDTO> createdResponse =
        restTemplate.postForEntity("http://localhost:" + port + "/todos", newTodo, TodoDTO.class);
    assertEquals(HttpStatus.OK, createdResponse.getStatusCode());
    assertNotNull(createdResponse.getBody().getId());

    restTemplate.delete("http://localhost:" + port + "/todos/" + createdResponse.getBody().getId());
    ResponseEntity<TodoDTO> deletedResponse =
        restTemplate.getForEntity(
            "http://localhost:" + port + "/todos/" + createdResponse.getBody().getId(),
            TodoDTO.class);
    assertEquals(HttpStatus.NOT_FOUND, deletedResponse.getStatusCode());
  }
}
