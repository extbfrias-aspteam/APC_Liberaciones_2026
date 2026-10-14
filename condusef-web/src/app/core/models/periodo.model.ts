import { Empresa } from "./empresa.model";

export interface Periodo {
    mes: number;
    anio: number;
    fecha_inicial: string;
    fecha_final: string;
    activo: boolean;
    fecha_cierre: Date;
    id_empresa: number;
}
