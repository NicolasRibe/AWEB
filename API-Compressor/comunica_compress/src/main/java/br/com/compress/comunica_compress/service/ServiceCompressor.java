package br.com.compress.comunica_compress.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.compress.comunica_compress.model.Compressor;
import br.com.compress.comunica_compress.repository.RepositoryCompressor;
import jakarta.transaction.Transactional;

@Service
public class ServiceCompressor {

    @Autowired
    RepositoryCompressor repositoryCompressor;

    @Transactional
    public Compressor salvar(Compressor compressor) {

        return repositoryCompressor.save(compressor);
    }

    public Optional<Compressor> buscarPorId(Integer id) {
        return repositoryCompressor.findById(id);

    }

    public Compressor atualizaDados(Integer id, Compressor compressorAtualizado) {

        var optionalCompreess = buscarPorId(id);
        if (!optionalCompreess.isPresent())
            throw new IllegalArgumentException("O compressor não foi encontrado.");
        var compressExiste = optionalCompreess.get();

        
        compressExiste.setTempArCompr(compressorAtualizado.getTempArCompr());
        compressExiste.setTempOleo(compressorAtualizado.getTempOleo());
        compressExiste.setAlerta(compressorAtualizado.getAlerta());
        compressExiste.setDataHora(compressorAtualizado.getDataHora());
        compressExiste.setEstado(compressorAtualizado.getEstado());
        compressExiste.setHoraCarga(compressorAtualizado.getHoraCarga());
        compressExiste.setHoraTotal(compressorAtualizado.getHoraTotal());
        compressExiste.setPressCarga(compressorAtualizado.getPressCarga());
        compressExiste.setPressArCompr(compressorAtualizado.getPressArCompr());
        compressExiste.setTempAmbien(compressorAtualizado.getTempAmbien());
        
        var compressorSalvo = repositoryCompressor.save(compressExiste);
       
        return compressorSalvo;
    }


    public Optional<Compressor> buscarUltimaLeitura(Integer idCompressor){
        return repositoryCompressor.findTopByIdOrderByDataHoraDesc(idCompressor);

    }

    

}
