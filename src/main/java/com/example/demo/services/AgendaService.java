package com.example.demo.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Anio;
import com.example.demo.model.Cita;
import com.example.demo.model.Dia;
import com.example.demo.model.Mes;
import com.example.demo.repository.AnioRepo;
import com.example.demo.repository.CitaRepo;
import com.example.demo.repository.DiaRepo;
import com.example.demo.repository.MesRepo;

@Service
public class AgendaService {

	@Autowired AnioRepo anioRepo;
	@Autowired MesRepo mesRepo;
	@Autowired DiaRepo diaRepo;
	@Autowired CitaRepo citaRepo;
	
	/**
	 * Este metodo recibe una cita, comprueba que el anio, mes y dia existen, si no es asi lo crea.
	 * Si el dia existe, se inserta una cita y se guarda
	 * @param cita
	 */
	public void nuevaReserva(Cita cita){
		
		System.out.println(cita);
		
		Anio anio = buscaAnio(cita.getFecha());
		Mes mes = buscaMes(cita.getFecha());
		Dia dia=buscaDia(cita.getFecha());
		if(anio==null) {
			anio = new Anio(); anio.setNumero(cita.getFecha().getYear()+1900);
			
		}
		if(mes==null) {
			mes = new Mes(); mes.setNumero(cita.getFecha().getMonth());
			
		}
		if(dia== null) {
			dia = new Dia(); dia.setNumero(cita.getFecha().getDate());
			
		}
		citaRepo.save(cita);
		dia.addCitaSinConfirmar(cita);
		if(!mes.getDias().contains(dia)) {
			mes.addDia(dia);
		}	
		if(!anio.getMeses().contains(mes)) {
			anio.addMes(mes);
			mesRepo.save(mes);
		}
		anioRepo.save(anio);
		
		
		
	}
	
	public Anio buscaAnio(Date fecha){
		int anio = fecha.getYear()+1900; 	
		return anioRepo.findByNumero(anio).orElse(null);
	}
	
	public Mes buscaMes(Date fecha) {
		Anio anio=buscaAnio(fecha);
		if(anio==null) return null;
		List<Mes>  meses= anio.getMeses();
		
		for (Mes mes : meses) {
			if(mes.getNumero()==fecha.getMonth()) {
			return mes;}
		}
			return null;
	}
	
	public Dia buscaDia(Date fecha) {
		Mes mes = buscaMes(fecha);
		if(mes==null) return null;
		List<Dia> dias = mes.getDias();
		for (Dia dia : dias) {
			if(dia.getNumero()==fecha.getDate()) {
				return dia;
			}
		}
		return null;
	}
	
	
	public Mes getMes(int anio, int mes) {
		Anio year=anioRepo.findByNumero(2022).orElse(null);
		
		if(year!=null && mes>0 && mes<12) return year.getMes(mes);
		else return null;
		
	}
}
