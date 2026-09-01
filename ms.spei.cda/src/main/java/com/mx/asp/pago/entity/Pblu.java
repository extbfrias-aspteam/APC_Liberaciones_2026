package com.mx.asp.pago.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pblu")
@Getter
@Setter
public class Pblu {

	@Id
	private Integer idPblu;

	private String clabePblu;
}
