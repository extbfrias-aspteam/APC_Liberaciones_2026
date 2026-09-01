package com.mx.asp.pago.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginRequest implements Serializable{
	/**
	 * Clase para inciar sesion
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
}
