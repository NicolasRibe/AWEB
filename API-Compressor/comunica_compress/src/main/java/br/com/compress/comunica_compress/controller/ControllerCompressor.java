package br.com.compress.comunica_compress.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.compress.comunica_compress.model.Compressor;
import br.com.compress.comunica_compress.service.ServiceCompressor;

@RestController
@RequestMapping("/api/compressor")
public class ControllerCompressor {

    @Autowired
    ServiceCompressor serviceCompressor;

    @PostMapping
    public ResponseEntity<Compressor> receberDados(@RequestBody Compressor compressor) {
        Integer id = compressor.getId(); 
        Compressor salvo = serviceCompressor.atualizaDados(id, compressor);
        return ResponseEntity.ok(salvo);
    }
    
}
