package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Persona;


public interface PersonaRepo extends JpaRepository<Persona, Long>{
	 
	//Método para obtener una persona por su email
	public Optional<Persona> findByEmail(String email);
}
