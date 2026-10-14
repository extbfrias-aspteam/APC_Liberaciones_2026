export interface Registro {
    id: number;
    folio: string;
    productoId: string;
    causaId: number;
    tipo: number;
    reportado: boolean;
    periodo: number;
    producto: string;
    subproducto: string;
    estatus: number;
  }
  export interface ApiResponse {
    success: boolean;
    status: string;
    errors: { [key: string]: string[] };
    message?: string;
    data?: string[];
  }
  export interface ResponseCondusef {
    [key: string]: ApiResponse;
  }
  export interface DropdownItem {
    id: any;
    name: string;
  }