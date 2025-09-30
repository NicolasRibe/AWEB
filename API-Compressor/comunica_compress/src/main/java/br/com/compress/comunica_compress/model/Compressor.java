package br.com.compress.comunica_compress.model;

import java.time.LocalDateTime;


import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Getter
@Setter
public class Compressor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @DateTimeFormat
    private LocalDateTime dataHora;

    @NotBlank
    private Byte estado;
    @NotBlank
    private Float tempArCompr;
    @NotBlank
    private Float tempAmbien;
    @NotNull
    private Float tempOleo;
    @NotNull
    private Float pressArCompr;
    @NotNull
    private Float pressCarga;
    @NotNull
    private Float horaCarga;
    @NotNull
    private Float horaTotal;
    @NotNull
    private String alerta;

}
