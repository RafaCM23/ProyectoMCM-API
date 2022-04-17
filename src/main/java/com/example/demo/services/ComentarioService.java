package com.example.demo.services;

import com.example.demo.model.Comentario;
import com.example.demo.repository.ComentarioRepo;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComentarioService {


	
	@Autowired ComentarioRepo comentRepo;
	
	
	//POST COMENTARIO
	public int postComentario(Comentario comentario) {
		Comentario existe=comentRepo.findByCodigo(comentario.hashCode()).orElse(null);
		if( existe == null) {
			comentario.setCodigo(comentario.hashCode());
			comentario.setFecha(new Date());
			comentario.setVerificado(false);
			comentRepo.save(comentario);
			return 1;
		}
		else {
			return -1;
		}
	}
}
