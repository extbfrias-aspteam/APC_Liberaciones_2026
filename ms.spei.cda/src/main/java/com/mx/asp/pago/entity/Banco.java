package com.mx.asp.pago.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "banco")
@Getter
@Setter
public class Banco {

	@Id
	private Integer idBanco;
	private String descripcion;
	private boolean activo;
}
