package com.example.demo.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Categoria;

public interface CategoriaRepo extends JpaRepository<Categoria,Long>{
	
	//Método para obtener una Categoria por nombre
	public Optional<Categoria> findByNombre(String nombre);
	
	
	

}
