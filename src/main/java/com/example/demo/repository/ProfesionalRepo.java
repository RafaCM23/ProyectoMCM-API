package com.example.demo.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Profesional;



public interface ProfesionalRepo extends JpaRepository<Profesional,Long>{
	
	
	//Método para obtener un Profesional por correo
	public Optional<Profesional> findByEmail(String email);
		
	//Método para obtener un Profesional por nombre
	public Optional<Profesional> findByNombre(String nombre);
	
	
	@Query(
			  value = "SELECT * FROM profesional p WHERE p.email!='administrador'", 
			  nativeQuery = true)
			Collection<Profesional> findAllNonAdmin();
	
	@Query(
	  value = "SELECT * FROM profesional p WHERE p.verificado=0", 
	  nativeQuery = true)
	Collection<Profesional> findAllNonVerified();
	
	@Query(
			  value = "SELECT * FROM profesional p WHERE p.verificado=1 AND p.email!='administrador'", 
			  nativeQuery = true)
	Collection<Profesional> findAllVerified();

}
