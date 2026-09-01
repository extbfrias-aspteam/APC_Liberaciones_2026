package com.mx.asp.pago.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mx.asp.pago.entity.ClaveRastreoEntity;

public interface IClaveRastreoRepository extends JpaRepository<ClaveRastreoEntity, Integer> {

	@Query(value = "SELECT COALESCE(MAX(dato_incremental),0)\r\n" + "FROM public.claves_rastreo\r\n"
			+ "where fecha = ?1 and id_participante = ?2", nativeQuery = true)
	Integer obtenerDatoIncremental(String fecha, String idParticipante);

}
