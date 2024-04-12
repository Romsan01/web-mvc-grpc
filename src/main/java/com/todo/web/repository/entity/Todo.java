package com.todo.web.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "todo")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Todo {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "completionStatus", nullable = false)
  private boolean completionStatus;
}
