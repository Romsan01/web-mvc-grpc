package com.todo.web.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoDTO {

  private Long id;
  private String description;
  private boolean completionStatus;

  public TodoDTO(String description, boolean completionStatus) {
    this.description = description;
    this.completionStatus = completionStatus;
  }
}
