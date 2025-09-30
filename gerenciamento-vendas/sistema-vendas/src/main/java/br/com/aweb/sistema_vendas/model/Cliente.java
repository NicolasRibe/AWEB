package br.com.aweb.sistema_vendas.model;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.aweb.sistema_vendas.repository.RepositoryCliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString

@Table(name = "clientes")
public class Cliente {

    @Autowired
    RepositoryCliente repositoryCliente;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotBlank(message = "O nome é obrigatório")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @NotNull(message = "O telefone é obrigatório")
    @Column(nullable = false, length = 15)
    private String telefone;

}
