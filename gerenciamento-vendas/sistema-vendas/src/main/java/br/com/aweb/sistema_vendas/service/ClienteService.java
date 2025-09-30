package br.com.aweb.sistema_vendas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.aweb.sistema_vendas.model.Cliente;
import br.com.aweb.sistema_vendas.repository.RepositoryCliente;

@Service
public class ClienteService {

    @Autowired
    RepositoryCliente repositoryCliente;

    // Aqui você pode adicionar métodos para operações relacionadas ao Cliente

    // Por exemplo, um método para salvar um cliente
    public void salvarCliente(Cliente cliente) {

        if (repositoryCliente.existsById(cliente.getId())) {
            throw new IllegalArgumentException("Cliente com ID " + cliente.getId() + " já existe.");
        }
        repositoryCliente.save(cliente);

    }

    // buscar cliente por nome (semelhante ao buscar por id)
    public Cliente buscarClientePorNome(String nome) {
        if (!repositoryCliente.findByNomeContaining(nome).isEmpty()){
            throw new IllegalArgumentException("Cliente com o nome " + nome + " não existe.");
        }
        
        return repositoryCliente.findByNomeContaining(nome).stream().findFirst().orElse(null);
    
    }

    // método para deletar cliente por id
    public void deletarCliente(Long id) {
        if (!repositoryCliente.existsById(id)) {
            throw new IllegalArgumentException("Cliente com ID " + id + " não existe.");
        }
        repositoryCliente.deleteById(id);
    }

    // método para atualizar cliente
    public Cliente atualizarCliente(Cliente cliente) {
        if (!repositoryCliente.existsById(cliente.getId())) {
            throw new IllegalArgumentException("Cliente com ID " + cliente.getId() + " não existe.");
        }
        return repositoryCliente.save(cliente);
    }

    // método para listar todos os clientes
    public List<Cliente> listarTodosClientes() {
        return repositoryCliente.findAll();
    }

}
