package com.mx.asp.pago.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table(name = "estado_pago")
@Entity
@Getter
@Setter
@ToString
public class EstadoPago {

	@Id
	private Integer idEstadoPago;
	private String descripcion;

}
