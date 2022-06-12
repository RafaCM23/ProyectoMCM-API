package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Persona {

	@Id
	private Long id;
	private String email;
	private String nombre;
	private String apellidos;
	@OneToMany(cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Cita> citas;
	private String tlfn;
	
	public Persona() {
		this.id=(long) this.hashCode();
		this.citas=new ArrayList<Cita>();
	}
	
	public void eliminaCita(Cita c) {
		this.citas.remove(this.citas.indexOf(c));
	}
	
	public void anadeCita(Cita c) {
		this.citas.add(c);
	}

}
