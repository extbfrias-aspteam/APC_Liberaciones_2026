export interface QuejaREDECO {
    QuejasSector: string,
    QuejasDenominacion: string,
    QuejasNoMes: number;
    QuejasNum: number;  
    QuejasFolio: string;
    QuejasFecRecepcion: string;
    QuejasMedio: number;
    QuejasNivelAT: number;
    QuejasProducto: string;
    QuejasCausa: string;
    QuejasPORI: string;
    QuejasEstatus: number;
    QuejasEstados: number;
    QuejasMunId: number;
    QuejasLocId: number;
    QuejasColId: number;
    QuejasCP: number;
    QuejasTipoPersona: number;
    QuejasSexo: string;
    QuejasEdad: number;
    QuejasFecResolucion: string;
    QuejasFecNotificacion: string;
    QuejasRespuesta: number;
    QuejasNumPenal: number;
    QuejasPenalizacion: number;
}

export interface QuejaEnvioResponse {
    "Número total de envios": number;
    "Quejas enviadas": string[];
    message: string;
}

export interface QuejaEnvioResponseError {
    errors: any;
    message: string;
}

export interface QuejaError {
    numFolio: string;
    errors: string[];
}

export interface QuejaReportadaResponse {
    quejas: QuejaReportadaItem[]
}

export interface QuejaReportadaItem {
    institucionClave: number;
    folio: string;
    year: number;
    month: string;
}