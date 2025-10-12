package br.com.aweb.sistema_vendas.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data 
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of ="id")
@Table(name="pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Cliente é Obrigadorio !")
    @ManyToOne//Define que a classe se comporta com relacionamento muitos pra um (muitas vendas para um cliente )
    //Essa anotação define que o atributo recebe chave estrageira de Cliente,
    // e tem como refencia o name/ do banco de dados ;
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente pedidoClientes;

    @NotNull
    private LocalDateTime dataPedido= LocalDateTime.now();

    @NotNull
    private BigDecimal valorTotal;

    // Esse campo adiciona um enumerate com strings dentro do status .;
    @Enumerated(EnumType.STRING)
    @Column(nullable =false, length = 10)
    private StatusPedido status = StatusPedido.ATIVO;

    // A Anotação OneTO Many , se trata de um relacionamento de um para muitos !"Um pedido pode ter varios itens "
    // Ou seja um Pedido contem diversos Items 
    // Bem a Anotação seguida de MappedBy se trata de uma chave estrageira no banco que é o outro lado da  relação 
    // Indica que , na classe ItemPedido existe um atributo associado a essa classe Pedido

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    //O version implementa controle de concorrencia otimista;

    @Version 
    private Long version;


    
}
