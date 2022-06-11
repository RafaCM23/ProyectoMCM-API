package com.example.demo.services;

import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.demo.model.Cita;
import com.example.demo.model.Profesional;
import com.example.demo.repository.ProfesionalRepo;

@Service
public class CorreoService {

	//-  Este Servicio se encarga de los envios de correos   -//
	
	@Autowired private JavaMailSender mailSender;
	@Autowired private TemplateEngine templateEng;
	
	@Autowired ProfesionalRepo profRepo;
	
	private static final String FROM = "mcm.nutricion.noreply@gmail.com";
	
	/**
	 * Este metodo es el raiz de todos, segun el tipo de correo, llama al metodo que envia el correo
	 * de solicitud, de confirmacion o el de cancelacion. El unico con motivo es el de cancelacion
	 * @param tipo
	 * @param Cita
	 * @param motivo
	 * @return ResponseEntity<?>
	 */
	public ResponseEntity<?> sendMail(int tipo, Cita c,int motivo){
		try {
			MimeMessage correo = null;
			switch (tipo) {
			case 1:
				correo=creaCorreoPeticion(c);
				break;
			case 2:
				correo=creaCorreoConfirmacion(c);
				break;
			case 3:
				correo=creaCorreoCancelacion(c,motivo);				
				break;

			default:
				correo=null;
				break;
			}
			if(correo==null && (tipo<1 || tipo>3)) {return ResponseEntity.badRequest().body("Falta Tipo Correo");}
			else if(correo!=null) {
				mailSender.send(correo);
				return ResponseEntity.ok().build();}
			else {return ResponseEntity.badRequest().body("Error al procesar el correo");}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error al procesar el correo");
		}		
	}
	
	/**
	 * Este metodo crea el correo que envia el metodo principal. En este caso es el de peticion, por lo que 
	 * usa la plantilla guardada en resource y da valores a las variables del corre segun el input recibido.
	 * Una vez creado lo devuelve para que SendMail() lo envie
 	 * @param Cita
	 * @return MimeMessage
	 * @throws MessagingException
	 */
	public MimeMessage creaCorreoPeticion(Cita c) throws MessagingException {
		
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
		
		Context context = new Context();
		String fecha= formatFecha(c.getFecha(),c.getHora());
		
		context.setVariable("fecha", fecha);
		Profesional prof = profRepo.findById(c.getProfId()).orElse(null);	if(prof==null) {return null;}
		context.setVariable("profesional", (prof.getNombre()+" "+prof.getApellidos()));
		context.setVariable("idCancelar", c.getCancelar());
		
		String html= templateEng.process("plantillaCorreoPeticion",context);
		helper.setText(html, true); 
		helper.setFrom(FROM);
		
		String who = c.getPersona().getEmail();
		helper.setTo(who);
		helper.setSubject("MCM Nutrición - Cita Solicitada");
		return mimeMessage;
	}
	
	/**
	 * Este metodo crea el correo que envia el metodo principal. En este caso es el de confirmacion, por lo que 
	 * usa la plantilla guardada en resource y da valores a las variables del corre segun el input recibido.
	 * Una vez creado lo devuelve para que SendMail() lo envie
	 * @param Cita
	 * @return MimeMessage
	 * @throws MessagingException
	 */
	public MimeMessage creaCorreoConfirmacion(Cita c) throws MessagingException {
		
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
		
		Context context = new Context();
		String fecha= formatFecha(c.getFecha(),c.getHora());
		context.setVariable("fecha", fecha);
		Profesional prof = profRepo.findById(c.getProfId()).orElse(null);	if(prof==null) {return null;}
		context.setVariable("profesional", (prof.getNombre()+" "+prof.getApellidos()));
		context.setVariable("idCancelar", c.getCancelar());
		String html= templateEng.process("plantillaCorreoAceptada",context);
		helper.setText(html, true); 
		helper.setFrom(FROM);
		
		String who = c.getPersona().getEmail();
		helper.setTo(who);
		helper.setSubject("MCM Nutrición - Cita Confirmada");
		return mimeMessage;
	}
	
	/**
	 * Este metodo crea el correo que envia el metodo principal. En este caso es el de cancelacion, por lo que 
	 * usa la plantilla guardada en resource, da valores a las variables del corre segun el input recibido.
	 * Una vez creado lo devuelve para que SendMail() lo envie.
	 * @param Cita
	 * @param motivo
	 * @return MimeMessage
	 * @throws MessagingException
	 */
	public MimeMessage creaCorreoCancelacion(Cita c, int motivo) throws MessagingException {
		if(motivo<1 || motivo>4) {return null;}
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
		
		Context context = new Context();
		
		String fecha= formatFecha(c.getFecha(),c.getHora());
		context.setVariable("fecha", fecha);
		
		Profesional prof = profRepo.findById(c.getProfId()).orElse(null);
		if(prof==null) {return null;}
		String mot="";
		if(motivo==1) {mot="Dia Ocupado";}
		else if(motivo==3){mot="Vacaciones";}
		else if(motivo==4) {mot="Cancelada por el Paciente";}
		else{mot="Motivos Personales";}
		
		context.setVariable("profesional", (prof.getNombre()+" "+prof.getApellidos()));
		context.setVariable("motivo", mot);
		String html= templateEng.process("plantillaCorreoCancelada",context);
		helper.setText(html, true); 
		helper.setFrom(FROM);
		
		String who = c.getPersona().getEmail();
		helper.setTo(who);
		helper.setSubject("MCM Nutrición - Cita Cancelada");
		return mimeMessage;
	}
	
		//  -- Utilidades  --  //
		
		public String formatFecha(Date d,int hora) {
			String fecha= ((d.getDate()+1)+"-"+(d.getMonth()+1)+"-"+(1900+d.getYear()));
			fecha+=" a las ";
			switch (hora) {
			case 4:
				fecha+="16:00";
				break;
			case 5:
				fecha+="17:00";
				break;
			case 6:
				fecha+="18:00";
				break;
			case 7:
				fecha+="19:00";
				break;

			default:
				fecha+="¿#?";
				break;
			}
			return fecha;
		}
	
}
