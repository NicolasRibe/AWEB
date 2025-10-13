package br.com.aweb.sistema_vendas.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Data 
@AllArgsConstructor
@NoArgsConstructor
public class ItemPedido {

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;  //Atributo de referencia da Jpa ;// mano que da hora entendeer isso seloko
    
    @OneToMany
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @NotNull
    private Integer quantidade;
    
    @NotNull
    private BigDecimal precoUnitario;
}
