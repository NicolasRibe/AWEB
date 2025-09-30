package br.com.compress.comunica_compress.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Falhas {

    @Id
    private Integer id;
    
    @NotBlank
    private String nome;

    
}
