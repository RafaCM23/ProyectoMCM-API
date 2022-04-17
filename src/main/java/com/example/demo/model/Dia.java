package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@ToString

public class Dia {
	
	public Dia() {
		this.citasConfirmadas=new ArrayList<Cita>();
		this.citasSinConfirmar=new ArrayList<Cita>();
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private int numero;
	
	@OneToMany(fetch=FetchType.LAZY)
	private List<Cita> citasSinConfirmar;
	@OneToMany(fetch=FetchType.LAZY)
	private List<Cita> citasConfirmadas;
	
	private Boolean vacaciones;

	
	public void addCitaSinConfirmar(Cita cita) {
		this.citasSinConfirmar.add(cita);
	}
	
	public void confirmarCita(Cita cita) {
		if(this.citasSinConfirmar.contains(cita)){
			this.citasSinConfirmar.remove(cita);
			this.citasConfirmadas.add(cita);
		}
	}
	
	
}