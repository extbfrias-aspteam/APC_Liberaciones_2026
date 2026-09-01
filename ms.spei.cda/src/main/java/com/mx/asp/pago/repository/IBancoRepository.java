package com.mx.asp.pago.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mx.asp.pago.entity.Banco;

public interface IBancoRepository extends JpaRepository<Banco, Integer>{

}
