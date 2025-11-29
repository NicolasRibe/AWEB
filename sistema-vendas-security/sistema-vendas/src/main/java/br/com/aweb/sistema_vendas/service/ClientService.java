package br.com.aweb.sistema_vendas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.aweb.sistema_vendas.model.Client;
import br.com.aweb.sistema_vendas.repository.ClientRepository;
import jakarta.transaction.Transactional;

@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @SuppressWarnings("null")
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    @SuppressWarnings("null")
    @Transactional
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    @Transactional
    public Client updateClient(Long id, Client updatedClient) {
        Optional<Client> optionalClient = findById(id);
        if (!optionalClient.isPresent()) {
            throw new RuntimeException("Cliente não Econtrado!");
        }

        var existingClient = optionalClient.get();

        existingClient.setFullName(updatedClient.getFullName());
        existingClient.setEmail(updatedClient.getEmail());
        existingClient.setCpf(updatedClient.getCpf());
        existingClient.setTelephone(updatedClient.getTelephone());
        existingClient.setStreet(updatedClient.getStreet());
        existingClient.setNumber(updatedClient.getNumber());
        existingClient.setComplement(updatedClient.getComplement());
        existingClient.setNeighboorhood(updatedClient.getNeighboorhood());
        existingClient.setCity(updatedClient.getCity());
        existingClient.setUf(updatedClient.getUf());
        existingClient.setZipCode(updatedClient.getZipCode());

        return clientRepository.save(existingClient);
    }

    @SuppressWarnings("null")
    @Transactional
    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado!");
        }
        clientRepository.deleteById(id);

    }
}
