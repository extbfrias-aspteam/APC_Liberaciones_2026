package com.mx.asp.pago.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mx.asp.pago.entity.CuentaEntity;

public interface IClabeRepository extends JpaRepository<CuentaEntity, String> {
	
	Optional<CuentaEntity> findByClabeAndActivo(String clabe, Boolean activo);

}
