package br.com.aweb.sistema_vendas.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "O nome é obrigatorio!")
    private String name;

    @Column(nullable = false, length = 250)
    @NotBlank(message = "A descrição é obrigatoria!")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "O preço é obrigatorio!")
    @Positive(message = "O valor deve ser maior que zero!")
    private BigDecimal price;

    @Column(nullable = false)
    @NotNull(message = "A qunatidade é obrigatoria!")
    @PositiveOrZero(message = "A quantidade deve ser maior ou igual a zero!")
    private Integer amount;
}
