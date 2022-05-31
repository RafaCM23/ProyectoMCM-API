package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Persona {
	
	@Id
	private int id;
	private String email;
	private String nombre;
	private String apellidos;

	private String tlfn;
	
	public Persona() {
		this.id=this.hashCode();
	}
}
