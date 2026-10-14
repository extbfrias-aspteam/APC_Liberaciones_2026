export interface ReclamacionREUNE {
  ID?: number;
  RecDenominacion?: string;
  RecSector?: string;
  RecTrimestre: number;
  RecNumero: number;
  RecFolioAtencion: string;
  RecEstadoConPend: number;
  RecFechaReclamacion: string;
  RecFechaAtencion?: string;
  RecMedioRecepcionCanal: number;
  RecProductoServicio: string;
  RecCausaMotivo: string;
  RecFechaResolucion?: string;
  RecFechaNotifiUsuario?: string;
  RecEntidadFederativa: number;
  RecCodigoPostal?: number;
  RecMunicipioAlcaldia: number;
  RecLocalidad?: number;
  RecColonia?: number;
  RecMonetario: string;
  RecMontoReclamado?: number;
  RecImporteAbonado?: number;
  RecFechaAbonoImporte?: string;
  RecPori: string;
  RecTipoPersona: number;
  RecSexo?: string;
  RecEdad?: number;
  RecSentidoResolucion?: number;
  RecNivelAtencion?: number;
  RecFolioCondusef?: string;
  RecReversa?: number;
  Sucursal?: number;
  Estatus?: number;
  Reportado?: boolean;
  RecOperacionExtranjero?: string;
}
export interface ReclamacionREUNEError {
  field: keyof ReclamacionREUNE | null;
  message: string;
}
