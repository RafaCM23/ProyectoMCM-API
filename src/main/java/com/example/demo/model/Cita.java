package com.example.demo.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Cita {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(cascade = CascadeType.MERGE)
	private Persona persona;
	private String motivo;
	private Date fecha;
	private Boolean presencial;
	private int hora;
	private Long profId;
	@JsonIgnore
	private int cancelar;
	
	@Override
	public int hashCode() {
		return Objects.hash(cancelar, fecha, hora, motivo, persona, presencial, profId,new Date());
	}
	@Override
	public boolean equals(Object obj) {
		
		Cita other = (Cita) obj;
		return cancelar == other.cancelar && Objects.equals(fecha, other.fecha) && hora == other.hora
				&& Objects.equals(motivo, other.motivo)
				&& Objects.equals(presencial, other.presencial) && Objects.equals(profId, other.profId);
	}
	
	
	
	
}

