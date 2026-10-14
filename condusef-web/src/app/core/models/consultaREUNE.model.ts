export interface ConsultaREUNE {
    Id?: number;
    InstitucionClave?: string;
    Sector?: string;
    ConsultasTrim: number;
    NumConsultas: number;
    ConsultasFolio: string;
    ConsultasEstatusCon: number;
    ConsultasFecAten?: string;
    EstadosId: number;
    ConsultasFecRecepcion: string;
    MediosId: number;
    Producto: string;
    CausaId: string;
    ConsultasCP?: number;
    ConsultasMpioId: number;
    ConsultasLocId?: number;
    ConsultasColId?: number;
    ConsultascatnivelatenId?: number;
    ConsultasPori: string;
    Sucursal?: number;
    Estatus?: number;
    Reportado?: boolean;
  }

  export interface ConsultaREUNEError {
    field: keyof ConsultaREUNE | null;
    message: string;
  }