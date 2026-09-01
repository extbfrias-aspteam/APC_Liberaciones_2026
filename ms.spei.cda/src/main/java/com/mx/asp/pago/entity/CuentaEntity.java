package com.mx.asp.pago.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cuenta")
@Getter
@Setter
public class CuentaEntity {

	@Id
	@Column(name = "clabe")
	private String clabe;

	@ManyToOne
	@JoinColumn(name = "udn")
	private UdnEntity udn;

	@Column(name = "estado")
	private String estado;

	@Column(name = "token")
	private String token;
	
	private Integer pblu;

	@Column(name = "activo")
	private Boolean activo;

	@Column(name = "fecha_creacion")
	private LocalDateTime fechaCreacion;

	@PrePersist
	public void prePersist() {
		this.fechaCreacion = LocalDateTime.now();
	}

}
