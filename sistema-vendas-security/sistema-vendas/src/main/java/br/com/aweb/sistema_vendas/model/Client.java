package br.com.aweb.sistema_vendas.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Clients")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Length(min = 3, max = 100)
    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(unique = true, nullable = false)
    @Email
    private String email;

    @Column(unique = true, nullable = false, length = 11)
    @Size(min = 11, max = 11, message = "Cpf deve ter 11 digitos!")
    @CPF
    private String cpf;

    @Nullable
    @Column(nullable = true)
    private String Telephone;

    @NotBlank(message = "Rua é obrigatorio!")
    @Column(nullable = false)
    private String street;

    @Nullable
    @Column(nullable = true)
    private Integer number;

    @Nullable
    @Column(nullable = true)
    private String complement;

    @NotBlank(message = "Bairro é obrigatorio!")
    @Column(nullable = false)
    private String neighboorhood;

    @NotBlank(message = "Cidade é obrigatorio!")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "UF é obrigatorio!")
    @Size(min = 2, max = 2, message = "Digite uma UF valida!")
    @Column(nullable = false, length = 2)
    private String uf;

    @NotBlank(message = "CEP é obrigatorio!")
    @Column(nullable = false)
    private String zipCode;

    @OneToMany(mappedBy = "client")
    private List<Order> order = new ArrayList<>();
}
