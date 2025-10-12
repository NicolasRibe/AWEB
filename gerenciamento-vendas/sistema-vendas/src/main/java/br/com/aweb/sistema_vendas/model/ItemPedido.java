package br.com.aweb.sistema_vendas.model;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity 
@Data 
@AllArgsConstructor
@NoArgsConstructor
public class ItemPedido {

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;  //Atributo de referencia da Jpa ;// mano que da hora entendeer isso seloko
    
}
