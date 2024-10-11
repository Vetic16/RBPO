package ru.mtuci.rbpo_2024_praktika.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "demon")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Demo {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "number")
    private int number;

    @OneToMany(mappedBy = "demo")
    @JsonIgnoreProperties("demo")
    private List<Details> details;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "demon_information",
            joinColumns = @JoinColumn(name = "demon_id"),
            inverseJoinColumns = @JoinColumn(name = "information_id")

    )
    private List<Info> infos;
}