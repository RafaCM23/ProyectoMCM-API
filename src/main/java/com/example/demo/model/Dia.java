package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@ToString

public class Dia {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private int numero;
	
	@OneToMany(fetch=FetchType.LAZY)
	private List<Cita> citasSinConfirmar;
	@OneToMany(fetch=FetchType.LAZY)
	private List<Cita> citasConfirmadas;
	
	private Boolean vacaciones;
	private Boolean ocupado;

	
	public Dia() {
		this.citasConfirmadas=new ArrayList<Cita>();
		this.citasSinConfirmar=new ArrayList<Cita>();
		this.vacaciones=false;
		this.ocupado=false;
	}
	public Dia(int numero) {
		this.citasConfirmadas=new ArrayList<Cita>();
		this.citasSinConfirmar=new ArrayList<Cita>();
		this.numero=numero;
		this.vacaciones=false;
		this.ocupado=false;
	}
	
	public void addCitaSinConfirmar(Cita cita) throws Exception {
		for (Cita c : citasConfirmadas) {
			if(c.getHora()==cita.getHora()) {
				throw new Exception("Hora Ocupada");
			}
		}
		for (Cita c : citasSinConfirmar) {
			if(c.getHora()==cita.getHora()) {
				throw new Exception("Hora Ocupada");
			}
		}
		this.citasSinConfirmar.add(cita);
	}
	
	public void confirmarCita(Cita cita) {
		if(this.citasSinConfirmar.contains(cita)){
			this.citasSinConfirmar.remove(cita);
			this.citasConfirmadas.add(cita);
		}
	}
	
	public void rechazaCita(Cita cita) {
		if(this.citasSinConfirmar.contains(cita)){
			this.citasSinConfirmar.remove(cita);
		}else if(this.citasConfirmadas.contains(cita)) {
			this.citasConfirmadas.remove(cita);
		}
	}
	
	
	
	
	
	
}
