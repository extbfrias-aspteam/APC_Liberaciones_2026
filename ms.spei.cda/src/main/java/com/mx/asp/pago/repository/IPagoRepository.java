package com.mx.asp.pago.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mx.asp.pago.entity.PagoEntity;

public interface IPagoRepository extends JpaRepository<PagoEntity,String>{
	
	Optional<PagoEntity> findByUuid(String uuid);
}
