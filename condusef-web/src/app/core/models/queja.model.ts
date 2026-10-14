export interface Queja {
    nombreCliente: string;
    correoCliente: string;
    telefonoCliente: string;
    sucursal: number;
    numCredito: string;
    despachoCobranza: string;
    numMes: number;     
    numQuejas: number;  
    numFolio: string;
    fechaRecepcion: string;
    idMedioRecepcion: number;
    idNivelAtencion: number;
    idProducto: string;
    producto: string;
    idCausa: string;
    causa: string;
    pori: string;
    estatus: number;
    idEntidadFed: number;
    idMunicipio: number;
    idLocalidad: number;
    idColonia: number;
    codPostal: number;
    idTipoPersona: number;
    personaSexo: string;
    personaEdad: number;
    fechaResolucion: string;
    fechaNotificacion: string;
    idRespuestaRes: number;
    numPenalizacion: number;
    idPenalizacion: number;
    reportado: boolean;
    idEmpresa: number;
}

export interface PagedResponse {
    total: number;
    items: any[];
}