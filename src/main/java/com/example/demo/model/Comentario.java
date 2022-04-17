package com.example.demo.model;

import java.util.Date;
import java.util.Objects;

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
@NoArgsConstructor
public class Comentario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String autor;
	
	private String contenido;
	
	private Date fecha;
	
	private Boolean verificado;
	
	private int codigo;

	@Override
	public int hashCode() {
		return Objects.hash(autor, contenido, fecha);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Comentario other = (Comentario) obj;
		return Objects.equals(autor, other.autor) && Objects.equals(contenido, other.contenido)
				&& Objects.equals(fecha, other.fecha);
	}
	
	
}
