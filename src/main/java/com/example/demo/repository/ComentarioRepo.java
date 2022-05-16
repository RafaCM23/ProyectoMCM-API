package com.example.demo.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Comentario;


public interface ComentarioRepo extends JpaRepository<Comentario, Long> {
	
	
		public Optional<Comentario> findByCodigo(int codigo);
		
		
		@Query(
				  value = "SELECT * FROM comentario c WHERE c.verificado=0", 
				  nativeQuery = true)
				Collection<Comentario> findAllNonVerified();
		
		@Query(
				  value = "SELECT * FROM comentario c WHERE c.verificado=1", 
				  nativeQuery = true)
				Collection<Comentario> findAllVerified();
}
