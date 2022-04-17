package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Comentario;


public interface ComentarioRepo extends JpaRepository<Comentario, Long> {
	
	
		public Optional<Comentario> findByCodigo(int codigo);
}
