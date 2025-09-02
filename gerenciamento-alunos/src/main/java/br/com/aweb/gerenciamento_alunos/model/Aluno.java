package br.com.aweb.gerenciamento_alunos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Aluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "O campo deve ser preenchido!")
    private String nome;
    @Min(value = 10, message = "O aluno precisa ser maior ou igual a 10 anos;")
    @Max(value = 18, message = "O aluno precisa ser maior ou igual a 18 anos;")
    private Integer idade;
    @NotBlank(message = "O campo deve ser preenchido!")
    private String curso;

    // contrutores
    public Aluno() {
    }

    public Aluno(Long id, @NotBlank(message = "O campo deve ser preenchido!") String nome,
            @Min(value = 10, message = "O aluno precisa ser maior ou igual a 10 anos;") @Max(value = 18, message = "O aluno precisa ser maior ou igual a 18 anos;") Integer idade,
            @NotBlank(message = "O campo deve ser preenchido!") String curso) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.curso = curso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

}
