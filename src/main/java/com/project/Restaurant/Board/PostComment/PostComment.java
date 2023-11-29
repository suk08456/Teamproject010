package com.project.Restaurant.Board.PostComment;


import com.project.Restaurant.Board.Post.Post;
import com.project.Restaurant.Member.consumer.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
public class PostComment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String content;

  private LocalDateTime localDateTime;

  @ManyToOne
  private Customer customer;

  @ManyToOne
  private Post post;



  private LocalDateTime modifyDate;

  @ManyToMany
  Set<Customer> likes;

  @ColumnDefault("FALSE")
  @Column(nullable = false)
  private Boolean isDeleted;

  @ColumnDefault("0")
  private Long parent;



}
