package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Dia;
import com.example.demo.model.Mes;

public interface DiaRepo extends JpaRepository<Dia,Long>{

	//Método para obtener un dia por su numero
	public Optional<Dia> findByNumero(int numero);
}
