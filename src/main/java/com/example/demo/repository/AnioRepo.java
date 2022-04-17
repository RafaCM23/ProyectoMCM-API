package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Anio;
import com.example.demo.model.User;


public interface AnioRepo extends JpaRepository<Anio,Long> {

		//Método para obtener un anio por su numero
		public Optional<Anio> findByNumero(int numero);
}
