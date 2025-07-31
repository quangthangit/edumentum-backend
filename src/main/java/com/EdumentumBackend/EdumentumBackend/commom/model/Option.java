package com.EdumentumBackend.EdumentumBackend.commom.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Option {
    private String text;
    private boolean isCorrect;
}
