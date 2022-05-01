package com.example.demo.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Profesional {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nombre;
	private String apellidos;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String contrasenia;
	private String email;
	private String tlfn;
	@OneToOne(fetch=FetchType.LAZY)
	@JsonIgnore
	private Agenda agenda;
	
	private Boolean verificado;
	
	private String img;
	private String especialidad;
	private String descripcion;
	
	public Profesional(String nombre, String apellidos, String email, String tlfn) {
		super();
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.email = email;
		this.tlfn = tlfn;
		this.verificado=true;
		this.agenda = new Agenda();
	}

	public Profesional(String nombre, String apellidos, String contrasenia, String email, String tlfn,
			String especialidad, String descripcion) {
		super();
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.contrasenia = contrasenia;
		this.email = email;
		this.tlfn = tlfn;
		this.especialidad = especialidad;
		this.descripcion = descripcion;
		this.verificado=false;
		this.agenda = new Agenda();
	}
	
	
	
	
}

