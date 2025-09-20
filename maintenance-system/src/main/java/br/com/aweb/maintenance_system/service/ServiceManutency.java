package br.com.aweb.maintenance_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.aweb.maintenance_system.model.Manutency;
import br.com.aweb.maintenance_system.repository.RepositoryManutency;
//Istancio a classe como serviço , aonde ficará toda a logica de negocio 
@Service
public class ServiceManutency {

    //Ele faz a injeção de repository dentro de service 
    //Da acesso a todos os metodos do repositorio.
    @Autowired
    RepositoryManutency repositoryManutency;

    //listar todas as ordens de serviço 
    public List<Manutency> listAll(){
        return repositoryManutency.findAll();

    }

    //Buscar por Local 
    public List<Manutency> buscarLocal(String local){
        
        if(local.isEmpty()){
            throw new RuntimeException("Erro ao consultar Local da Ordem de Serviço");
        }
        return repositoryManutency.findByLocalContaining(local);
    }

    // remover uma ordem 
    public void deleteOrdem(Long id){
        if(!repositoryManutency.existsById(id)){
            throw new RuntimeException("Erro ao excluir ordem de serviço !");
        }
        repositoryManutency.deleteById(id);
    }
    
    //adicionar uma ordem de serviço 
    public void createOs(Manutency ordemServ){
        repositoryManutency.save(ordemServ);
    }

    //edit 
    public void editOrdem(Manutency ordemServ){
        repositoryManutency.save(ordemServ);
        // var ordem = repositoryManutency.findById(id);
        // if (ordem.isPresent()){
        //     ordem.get().setDeadlineOs(ordemServ.getDeadlineOs());
        //     ordem.get().setDescricaoServico(ordemServ.getDescricaoServico());
        //     ordem.get().setLocal(ordemServ.getLocal());
        //     ordem.get().setNomeSolicitante(ordemServ.getNomeSolicitante());
        //     ordem.get().setPrioridade(ordemServ.getPrioridade());
        //     ordem.get().setStatus(ordemServ.getStatus());
        // }
        // throw new RuntimeException("O ID não existe !!");

    }
    

    
}
