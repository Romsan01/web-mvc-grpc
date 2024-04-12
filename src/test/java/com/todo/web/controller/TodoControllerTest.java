package com.todo.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.web.TestUtil;
import com.todo.web.repository.model.TodoDTO;
import com.todo.web.service.TodoService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private TodoService todoService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void testGetAllTodos_ReturnsTodosList_WhenTodosExist() throws Exception {
    List<TodoDTO> todos =
        Arrays.asList(
            TestUtil.createTodoDTO(1L, "Test 1", true), TestUtil.createTodoDTO(2L, "Test 2", true));
    when(todoService.getAllTodos()).thenReturn(Optional.of(todos));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/todos"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(todos.size()))
        .andExpect(jsonPath("$[0].id").value(todos.get(0).getId()))
        .andExpect(jsonPath("$[0].description").value("Test 1"))
        .andExpect(jsonPath("$[0].completionStatus").value(true));
  }

  @Test
  public void testGetAllTodos_ReturnsNotFound_WhenTodosNotExist() throws Exception {
    when(todoService.getAllTodos()).thenReturn(Optional.empty());
    mockMvc.perform(MockMvcRequestBuilders.get("/todos")).andExpect(status().isNotFound());
  }

  @Test
  public void testGetTodoById_ReturnsTodo_WhenTodoExists() throws Exception {
    TodoDTO todo = TestUtil.createTodoDTO(1L, "Important Task", false);
    when(todoService.getTodo(todo.getId())).thenReturn(Optional.of(todo));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/todos/{id}", 1).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(todo.getId()))
        .andExpect(jsonPath("$.description").value("Important Task"));
  }

  @Test
  public void testGetTodoById_ReturnsNotFound_WhenTodoNotExists() throws Exception {
    when(todoService.getTodo(1L)).thenReturn(Optional.empty());
    mockMvc.perform(MockMvcRequestBuilders.get("/todos/{id}", 1)).andExpect(status().isNotFound());
  }

  @Test
  public void testCreateTodo_ReturnsCreatedTodo() throws Exception {
    TodoDTO newTodo = TestUtil.createTodoDTO(null, "New Task", true);
    TodoDTO savedTodo = TestUtil.createTodoDTO(1L, "New Task", true);
    when(todoService.addTodo(newTodo)).thenReturn(savedTodo);

    String todoJson = objectMapper.writeValueAsString(newTodo);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(todoJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(savedTodo.getId()));
  }

  @Test
  public void testUpdateTodo_ReturnsUpdatedTodo() throws Exception {
    TodoDTO updatedTodo = TestUtil.createTodoDTO(1L, "Updated Task", true);
    when(todoService.getTodo(any())).thenReturn(Optional.of(updatedTodo));
    when(todoService.updateTodo(updatedTodo)).thenReturn(updatedTodo);
    HashMap<String, String> patch = new HashMap<>();
    patch.put("op", "replace");
    patch.put("path", "/description");
    patch.put("value", "Updated Task new");

    String todoJson = objectMapper.writeValueAsString(Arrays.asList(patch));
    System.out.println(todoJson);
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/todos/{id}", 1L)
                .contentType("application/json-patch+json")
                .content(todoJson))
        .andExpect(status().isOk());
  }

  @Test
  public void testDeleteTodoById_PerformsDeletion() throws Exception {
    doNothing().when(todoService).deleteTodo(1L);
    mockMvc.perform(MockMvcRequestBuilders.delete("/todos/{id}", 1L)).andExpect(status().isOk());
  }
}
