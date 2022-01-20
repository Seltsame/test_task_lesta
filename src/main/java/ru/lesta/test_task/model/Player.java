package ru.lesta.test_task.model;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    private String name;
    private Double skill;
    private Double latency;
}
