package com.example.demo.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Cita;


public interface CitaRepo extends JpaRepository<Cita,Long>{
	
	@Query(
			  value = "SELECT * FROM cita c WHERE \n"
			  		+ "(DATE_FORMAT(fecha, '%Y/%m/%d')<(DATE_FORMAT((DATE_ADD(CURDATE(), INTERVAL 2 WEEK)), '%Y/%m/%d')))\n"
			  		+ "AND c.prof_id=1\n"
			  		+ "AND c.id IN\n"
			  		+ "(SELECT citas_sin_confirmar_id FROM dia_citas_sin_confirmar)", 
			  nativeQuery = true)
			Collection<Cita> findNextNonVerified(@Param("id") Long id);
	//Esta query pregunta por las citas que esten en las proximas dos semanas despues del dia actual
	//Y no esten confirmadas
	
	
	@Query(
			  value = "SELECT * FROM cita c WHERE \n"
			  		+ "(DATE_FORMAT(fecha, '%Y/%m/%d')<(DATE_FORMAT((DATE_ADD(CURDATE(), INTERVAL 2 WEEK)), '%Y/%m/%d')))\n"
			  		+ "AND c.prof_id=1\n"
			  		+ "AND c.id IN\n"
			  		+ "(SELECT citas_confirmadas_id FROM dia_citas_confirmadas)", 
			  nativeQuery = true)
			Collection<Cita> findNextVerified(@Param("id") Long id);
	//Esta query pregunta por las citas que esten en las proximas dos semanas despues del dia actual
	//Y esten confirmadas
	
	
	//Método para obtener una Cita por Hash para cancelar
	public Optional<Cita> findByCancelar(int hash);
	
	
	
	
}
