package com.example.demo.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Categoria;

public interface CategoriaRepo extends JpaRepository<Categoria,Long>{
	
	//Método para obtener un Profesional por nombre
	public Optional<Categoria> findByNombre(String nombre);
	
	
	

}
