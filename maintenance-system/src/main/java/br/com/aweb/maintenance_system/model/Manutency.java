package br.com.aweb.maintenance_system.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")

public class Manutency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 5, max = 100)
    @NotBlank
    @Column(nullable = false, length = 100)
    private String descricaoServico;

    @Column(nullable = false)
    private LocalDateTime createdOs = LocalDateTime.now();

    @NotNull
    // @FutureOrPresent
    @DateTimeFormat(iso = ISO.DATE)
    @Column(nullable = false)
    private LocalDate deadlineOs;

    @Column(nullable = true)
    private LocalDate finishedOs;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    @NotNull
    private String prioridade;

    @NotBlank
    private String nomeSolicitante;

    @NotBlank
    @Column(nullable = false)
    private String local;







    



}
