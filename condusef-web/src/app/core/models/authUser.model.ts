import { Empresa } from "./empresa.model";

export interface AuthUser {
    id: string;
    num_empleado: string;
    nombre: string;
    empresa: Empresa
  }