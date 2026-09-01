package com.mx.asp.pago.entity;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "udn")
@Getter
@Setter
public class UdnEntity {

    @Id
    @Column(name = "id_udn")
    private Integer idUdn;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "saldo", nullable = false)
    private BigDecimal saldo;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "usuario_creacion")
    private String usuarioCreacion;

    @Column(name = "pblu", nullable = false)
    private Integer pblu;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
    }

}
