package com.mx.asp.pago.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table(name="pago")
@Entity
@Getter
@Setter
@ToString
public class PagoEntity {

	@Id
	private String cveRastreo;
	@ManyToOne
	@JoinColumn(name = "id_estado_pago")
	private EstadoPago idEstadoPago;
	private String refNum;
	private LocalDateTime fechaCreacion;
	private String uuid;
	
}

	 