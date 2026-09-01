package com.mx.asp.pago.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "claves_rastreo")
@Getter
@Setter
public class ClaveRastreoEntity {

	@Id
	private Integer id;

	private String fecha;

	@Column(name = "spei_emisor")
	private String speiEmisor;

	private String hora;

	@Column(name = "tipo_aplicacion")
	private String tipoAplicacion;

	@Column(name = "id_participante")
	private String idParticipante;

	@Column(name = "dato_incremental")
	private Integer datoIncremental;

	@Column(name = "dato_verificador")
	private String datoVerificador;
}
