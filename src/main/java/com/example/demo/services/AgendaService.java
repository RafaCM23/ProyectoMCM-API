package com.example.demo.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Agenda;
import com.example.demo.model.Anio;
import com.example.demo.model.Cita;
import com.example.demo.model.Dia;
import com.example.demo.model.Mes;
import com.example.demo.model.Profesional;
import com.example.demo.repository.AgendaRepo;
import com.example.demo.repository.AnioRepo;
import com.example.demo.repository.CitaRepo;
import com.example.demo.repository.DiaRepo;
import com.example.demo.repository.MesRepo;
import com.example.demo.repository.ProfesionalRepo;

@Service
public class AgendaService {

	@Autowired AnioRepo anioRepo;
	@Autowired MesRepo mesRepo;
	@Autowired DiaRepo diaRepo;
	@Autowired CitaRepo citaRepo;
	@Autowired AgendaRepo agendaRepo;
	@Autowired ProfesionalRepo profRepo;
	
	@Autowired ProfesionalService profService;
	/**
	 * Este metodo recibe una cita, comprueba que el anio, mes y dia existen, si no es asi lo crea.
	 * Si el dia existe, se inserta una cita y se guarda
	 * @param cita
	 */
	public String nuevaReserva(int idProf,int anio, int mes,Cita cita){
		
		if(cita==null || /*cita.getPersona()==null || cita.getHora()<4 || cita.getHora()>7 ||*/cita.getMotivo()==null) {
			System.out.println(cita);
			System.out.println(cita.getPersona());
			System.out.println(cita.getMotivo());
			return "Faltan datos";
		}
		Profesional prof = profRepo.findById(Long.valueOf(idProf)).orElse(null);
		
		if(prof==null) {return "Profesional no encontrado";}	Agenda ag=prof.getAgenda();
		if(ag==null) {return "Agenda no encontrada";}			Anio year = ag.getAnio(anio);
		if(year==null) {ag.creaAnio(anio);}						Mes month = year.getMes(mes);
		if(month==null) {month=new Mes(mes);}					Dia day =month.getDia(cita.getFecha().getDate());
		if(day==null) {day= new Dia(cita.getFecha().getDate());}
		
		day.addCitaSinConfirmar(cita);
		citaRepo.save(cita);
		diaRepo.save(day);
		if(!month.getDias().contains(day)) {
			month.addDia(day);
		}	
		if(!year.getMeses().contains(month)) {
			year.addMes(month);
			mesRepo.save(month);
		}
		anioRepo.save(year);
		
		ag.addAnio(year);
	
		return "Success";
		
		
		
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
	
	public Mes getAgenda(int idProf,int anio, int mes) {
		List<Profesional> profs = profService.getProfesionales();
		if(profs==null) {profService.initProfesionales();}
		Profesional p = profRepo.findById(Long.valueOf(idProf)).orElse(null);
		
		if(p==null || p.getAgenda()==null ){return null;}
		Anio year=p.getAgenda().getAnio(anio);
		if(year==null) {p.getAgenda().creaAnio(anio);}
	
			year=p.getAgenda().getAnio(anio);
			Mes month = year.getMes(mes);
		if(month!=null) {return month;}
		else {
				month=new Mes(mes);
				anioRepo.save(year);
				mesRepo.save(month);
				
		}
			year.addMes(month);
			anioRepo.save(year);
			agendaRepo.save(p.getAgenda());
			profRepo.save(p);
			return month;
		
		
		
		
	}
	
	public void creaMarta() {
		Profesional marta= new Profesional("Marta","Cuberos Mesa","correo@correo.com","123123123");
		marta.getAgenda().creaAnio(2022);
		Mes month = new Mes(); month.setNumero(2);
		Anio year=marta.getAgenda().getAnio(2022);
		mesRepo.save(month);
		year.addMes(month);
		anioRepo.save(year);
		agendaRepo.save(marta.getAgenda());
		profRepo.save(marta);
	}
}
