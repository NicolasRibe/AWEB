package br.com.aweb.sistema_vendas.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.aweb.sistema_vendas.model.Client;
import br.com.aweb.sistema_vendas.model.Order;
import br.com.aweb.sistema_vendas.model.Order.StatusPedido;
import br.com.aweb.sistema_vendas.model.OrderItem;
import br.com.aweb.sistema_vendas.model.Product;
import br.com.aweb.sistema_vendas.repository.OrderRepository;
import br.com.aweb.sistema_vendas.repository.ProductRepository;
import jakarta.transaction.Transactional;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductRepository productRepository;

    @Transactional
    public Order createOrder(Client client) {
        return orderRepository.save(new Order(client));
    }

    private void calculateTotalValue(Order order) {
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : order.getItems()) {
            BigDecimal itemPrice = item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getAmount()));
            total = total.add(itemPrice);
        }
        order.setTotalPrice(total);
    }

    @SuppressWarnings("null")
    @Transactional
    public void addItem(Long orderId, Long productId, Integer amount) {
        Optional<Order> order = orderRepository.findById(orderId);
        Optional<Product> product = productRepository.findById(productId);

        if (!order.isPresent()) {
            throw new IllegalArgumentException("Pedido não encontrado!");
        }

        if (!product.isPresent()) {
            throw new IllegalArgumentException("Produto não encontrado!");
        }

        if (order.get().getStatus() != StatusPedido.ATIVO) {
            throw new IllegalArgumentException("Não é possivel alterar um pedido cancelado!");
        }

        if (product.get().getAmount() < amount) {
            throw new IllegalArgumentException("Quantidade de produto insuficiente!");
        }
        var item = new OrderItem(product.get(), amount);

        order.get().getItems().add(item);

        product.get().setAmount(product.get().getAmount() - amount);

        calculateTotalValue(order.get());

        orderRepository.save(order.get());
        productRepository.save(product.get());
    }

    @SuppressWarnings("null")
    @Transactional
    public void removeItem(Long orderId, Long itemId) {
        Optional<Order> order = orderRepository.findById(orderId);

        if (!order.isPresent()) {
            throw new IllegalArgumentException("Pedido não encontrado!");
        }

        if (order.get().getStatus() != StatusPedido.ATIVO) {
            throw new IllegalArgumentException("Não é possivel alterar um pedido cancelado!");
        }

        for (OrderItem item : order.get().getItems()) {
            if (item.getId().equals(itemId)) {

                order.get().getItems().remove(item);

                Optional<Product> product = productRepository.findById(item.getId());

                product.get().setAmount(product.get().getAmount() + item.getAmount());

                productRepository.save(product.get());

                break;
            } else {
                throw new IllegalArgumentException("Produto nao encontrado!");
            }
        }
        calculateTotalValue(order.get());

        orderRepository.save(order.get());
    }
}
