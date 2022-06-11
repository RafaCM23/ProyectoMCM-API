package com.example.demo.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.model.Agenda;
import com.example.demo.model.Anio;
import com.example.demo.model.Cita;
import com.example.demo.model.Dia;
import com.example.demo.model.Mes;
import com.example.demo.model.Persona;
import com.example.demo.model.Profesional;
import com.example.demo.repository.AgendaRepo;
import com.example.demo.repository.AnioRepo;
import com.example.demo.repository.CitaRepo;
import com.example.demo.repository.DiaRepo;
import com.example.demo.repository.MesRepo;
import com.example.demo.repository.PersonaRepo;
import com.example.demo.repository.ProfesionalRepo;

@Service
public class AgendaService {

	@Autowired AnioRepo anioRepo;
	@Autowired MesRepo mesRepo;
	@Autowired DiaRepo diaRepo;
	@Autowired CitaRepo citaRepo;
	@Autowired AgendaRepo agendaRepo;
	@Autowired ProfesionalRepo profRepo;
	@Autowired PersonaRepo personaRepo;
	
	@Autowired private CorreoService correoService;
	
	@Autowired ProfesionalService profService;
	/**
	 * Este metodo recibe una cita, comprueba que el anio, mes y dia existen, si no es asi lo crea.
	 * Si el dia existe, se inserta una cita y se guarda
	 * @param cita
	 */
	public String nuevaReserva(int idProf,int anio, int mes,Cita cita){
		
		if(cita==null || cita.getPersona()==null || cita.getHora()<4 || cita.getHora()>7 ||cita.getMotivo()==null) {
			return "Faltan datos";
		}
		Profesional prof = profRepo.findById(Long.valueOf(idProf)).orElse(null);
		if(prof==null) {return "Profesional no encontrado";}	Agenda ag=prof.getAgenda();
		if(ag==null) {return "Agenda no encontrada";}			Anio year = ag.getAnio(anio);
		if(year==null) {ag.creaAnio(anio);}						Mes month = year.getMes(mes);
		if(month==null) {month=new Mes(mes);}					Dia day =month.getDia(cita.getFecha().getDate());
		if(day==null) {day= new Dia(cita.getFecha().getDate());}
		
		cita.setProfId(prof.getId());
		cita.setCancelar(cita.hashCode());

		day.addCitaSinConfirmar(cita);
		
		Persona nueva = cita.getPersona();
		Persona buscada = personaRepo.findByEmail(nueva.getEmail()).orElse(null);
		
		if(buscada!=null) {
			buscada.anadeCita(cita);
			cita.setPersona(buscada);
			citaRepo.save(cita);
			personaRepo.save(buscada);	
		}
		else {
			nueva.anadeCita(cita);
			cita.setPersona(nueva);
			personaRepo.save(nueva);
			citaRepo.save(cita);
		}

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
		correoService.sendMail(1, cita, 0);
		return "Success";
	}
	
	public ResponseEntity<?> ocupaDia(Long id, int anio, int mes, int dia,Profesional p){
		Profesional prof=profRepo.findById(id).orElse(null);
		if(p==null || (!p.getEmail().equals("administrador") && prof.getId()!=p.getId())|| prof==null ) {
			return ResponseEntity.badRequest().body("Datos Erroneos");
		}
		else {
			Mes month=prof.getAgenda().getAnio(anio).getMes(mes);
			Dia day= month.getDia(dia);
			if(day==null) {
				Dia nuevo = new Dia(dia);
				nuevo.setOcupado(true);
				month.addDia(nuevo);
				diaRepo.save(nuevo);
				mesRepo.save(month);
				}
			else {
				day.setOcupado(true);
				diaRepo.save(day);
				mesRepo.save(month);
				List<Cita> conf=day.getCitasConfirmadas();
				List<Cita> noConf=day.getCitasSinConfirmar();
				for (Cita c: conf) {
					correoService.sendMail(3, c, 1);
					day.rechazaCita(c);
					citaRepo.delete(c);
				}

				for (Cita c : noConf) {
					correoService.sendMail(3, c, 1);
					day.rechazaCita(c);
					citaRepo.delete(c);
				}
			}
			return ResponseEntity.ok().build();
		}
	}
	
	public ResponseEntity<?> vacacionesDia(Long id, int anio, int mes, int dia,Profesional p){
		Profesional prof=profRepo.findById(id).orElse(null);
		if(p==null || (!p.getEmail().equals("administrador") && prof.getId()!=p.getId())|| prof==null ) {
			return ResponseEntity.badRequest().body("Datos Erroneos");
		}
		else {
			Mes month=prof.getAgenda().getAnio(anio).getMes(mes);
			Dia day= month.getDia(dia);
			if(day==null) {
				Dia nuevo = new Dia(dia);
				nuevo.setVacaciones(true);
				month.addDia(nuevo);
				diaRepo.save(nuevo);
				mesRepo.save(month);
				}
			else {
				day.setVacaciones(true);
				diaRepo.save(day);
				mesRepo.save(month);
				List<Cita> conf=day.getCitasConfirmadas();
				List<Cita> noConf=day.getCitasSinConfirmar();
				for (Cita c: conf) {
					correoService.sendMail(3, c, 3);
					day.rechazaCita(c);
					citaRepo.delete(c);
				}

				for (Cita c : noConf) {
					correoService.sendMail(3, c, 3);
					day.rechazaCita(c);
					citaRepo.delete(c);
				}
			}
			
			return ResponseEntity.ok().build();
		}
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
	
	public ResponseEntity<?> getCitasProximasSinVerificar(String email){
		Profesional p=profRepo.findByEmail(email).orElse(null);
		if(p==null) {return ResponseEntity.badRequest().body("No es un profesional");}
		else {
			List<Cita> todas = (List<Cita>) citaRepo.findNextNonVerified(p.getId());
			return ResponseEntity.ok(todas);
		}
		
	}
	
	public ResponseEntity<?> getCitasProximasVerificadas(String email){
		Profesional p=profRepo.findByEmail(email).orElse(null);
		if(p==null) {return ResponseEntity.badRequest().body("No es un profesional");}
		else {
			List<Cita> todas = (List<Cita>) citaRepo.findNextVerified(p.getId());
			return ResponseEntity.ok(todas);
		}
	}
	
	public ResponseEntity<?> aceptaCita(Long id, String email){
			
		if(id==null) {return ResponseEntity.badRequest().body("Faltan datos");}
		Cita cita=citaRepo.findById(id).orElse(null);
		Profesional prof= profRepo.findByEmail(email).orElse(null);
		if(prof==null || (prof.getId()!= cita.getProfId() && !prof.getEmail().equals("administrador")) ) {return ResponseEntity.badRequest().body("Faltan permisos");}
		else {

			Dia day=buscaDia(cita.getProfId(),cita);
			day.confirmarCita(cita);
			correoService.sendMail(2, cita, 0);
			diaRepo.save(day);
			return ResponseEntity.ok().build();
		}
		
	}
	
	public ResponseEntity<?> rechazaCita(Long id,String email,int motivo){
		if(id==null) {return ResponseEntity.badRequest().body("Faltan datos");}
		Cita cita=citaRepo.findById(id).orElse(null);
		Profesional prof= profRepo.findByEmail(email).orElse(null);
		if(prof==null || (prof.getId()!= cita.getProfId() && !prof.getEmail().equals("administrador"))) {return ResponseEntity.badRequest().body("Faltan permisos");}
		else {

			Dia day=buscaDia(cita.getProfId(),cita);
			day.rechazaCita(cita);
			diaRepo.save(day);
			citaRepo.delete(cita);
			correoService.sendMail(3, cita, motivo);
			return ResponseEntity.ok().build();
		}
	}
	
	public ResponseEntity<?> cancelarCita(int hash){
		Cita cita = citaRepo.findByCancelar(hash).orElse(null);
		if(cita==null) {return ResponseEntity.notFound().build();}
		else {
			
			Dia day=buscaDia(cita.getProfId(),cita);
			day.rechazaCita(cita);
			diaRepo.save(day);
			correoService.sendMail(3, cita, 4);
			citaRepo.delete(cita);
			return ResponseEntity.noContent().build();
		}
	}
	
	
	public Dia buscaDia(Long idProf,Cita c) {
		String y=c.getFecha().toString().substring(0,4);
		String m=c.getFecha().toString().substring(5,7);
		String d=c.getFecha().toString().substring(8,10);
		Profesional p = profRepo.findById(idProf).orElse(null); if(p==null) {return null;}
		Anio year= p.getAgenda().getAnio(Integer.parseInt(y));	
		Mes mes= year.getMes(Integer.parseInt(m)-1);
		Dia day = mes.getDia(Integer.parseInt(d));
		return day;
	}
	

	
	
}
