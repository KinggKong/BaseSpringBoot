package com.global.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Table(name = "black_tokens")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlackToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String token;
}
