export interface AclaracionREUNE {
    Id?: number;
    AclaracionDenominacion?: string;
    AclaracionSector?: string;
    AclaracionTrimestre: number;
    AclaracionNumero: number;
    AclaracionFolioAtencion: string;
    AclaracionEstadoConPend: number;
    AclaracionFechaAclaracion: string;
    AclaracionFechaAtencion?: string;
    AclaracionMedioRecepcionCanal: number;
    AclaracionProductoServicio: string;
    AclaracionCausaMotivo: string;
    AclaracionFechaResolucion?: string;
    AclaracionFechaNotifiUsuario?: string;
    AclaracionEntidadFederativa: number;
    AclaracionCodigoPostal?: number;
    AclaracionMunicipioAlcaldia: number;
    AclaracionLocalidad?: number;
    AclaracionColonia?: number;
    AclaracionMonetario: string;
    AclaracionMontoReclamado?: number;
    AclaracionPori: string;
    AclaracionTipoPersona: number;
    AclaracionSexo?: string;
    AclaracionEdad?: number;
    AclaracionNivelAtencion?: number;
    AclaracionFolioCondusef?: string;
    AclaracionReversa?: number;
    AclaracionOperacionExtranjero: string;
    Sucursal?: number;
    Estatus?: number;
    Reportado?: boolean;
  }

  export interface AclaracionREUNEError {
    field: keyof AclaracionREUNE | null;
    message: string;
  }