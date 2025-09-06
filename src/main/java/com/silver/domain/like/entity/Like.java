package com.silver.domain.like.entity;

import com.silver.domain.snack.entity.Snack;
import com.silver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "likes")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Like {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Snack snack;
}
