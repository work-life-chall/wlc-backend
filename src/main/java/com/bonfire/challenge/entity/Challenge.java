package com.bonfire.challenge.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "category_id", nullable = false)
    private int category_id;
    @Column(name = "company_id", nullable = false)
    private int company_id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "frequency", nullable = false)
    private String frequency;
    @Column(name = "term", nullable = false)
    private String term;
    @Column(name = "reward", nullable = false)
    private int reward;
    @Column(name = "entry", nullable = false)
    private int entry;
    @Column(name = "img_file1")
    private String img_file1;
    @Column(name = "img_file2")
    private String img_file2;
    @Column(name = "recommend", nullable = false)
    private boolean recommend;
    @Column(name = "note_id", nullable = false)
    private String note_id;
    @Column(name = "effect", nullable = false)
    private String effect;

}