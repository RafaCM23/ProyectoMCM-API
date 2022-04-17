package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Mes;

public interface MesRepo extends JpaRepository<Mes,Long>{
	
			//Método para obtener un Mes por su numero
			public Optional<Mes> findByNumero(int numero);

}
