package com.mx.asp.pago.record;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificadoFiles {

	File cer;
	File key;
	File txt;
}