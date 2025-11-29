package br.com.aweb.sistema_vendas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.aweb.sistema_vendas.model.Order;
import br.com.aweb.sistema_vendas.model.Order.StatusPedido;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(StatusPedido Status);
}
