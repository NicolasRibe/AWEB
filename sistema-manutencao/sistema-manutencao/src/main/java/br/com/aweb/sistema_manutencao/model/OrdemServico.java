package br.com.aweb.sistema_manutencao.model;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@EqualsAndHashCode(of = "id")
@ToString
@Data
public class OrdemServico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false,length = 50)
    private String tituloOrdem;

    @NotBlank(message = "O nome do solicitante da abertura da ordem é obrigatorio !")
    @Column(nullable = false, length = 50)
    private String solicitanteOrdem;

    @NotBlank(message = "Obrigatória uma descrição de abertura da ordem !")
    @Column(nullable = false, length = 50)
    private String descricaoOrdem;

    @Column(nullable = false)
    private LocalDateTime criacaoOrdem = LocalDateTime.now();

    @Column(nullable = true)
    private LocalDateTime finalizacaoOrdem;



    
}