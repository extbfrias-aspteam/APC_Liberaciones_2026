using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;


public class BO_ConsultaOperacionesRequest
{
    public string fecha { get; set; }
    public string tipoOperacion { get; set; }
    public List<string> cveRastreo { get; set; }

}
public class Response
{
    public int Codigo { get; set; }
    public string Mensaje { get; set; }
    public dynamic Data { get; set; }
}
public class BO_ConsultaOperacionesResponse
{
    public string EiyuResponseCode { get; set; }
    public string Descripcion { get; set; }
    public List<BO_ConsultaOperaciones> Content { get; set; }
}

public class BO_ConsultaOperaciones
{
    public string fchOperacion { get; set; }
    public string fchLiquidacion { get; set; }
    public string cveRastreo { get; set; }
    public string cveRastreo2 { get; set; }
    public string bancoEmisor { get; set; }
    public string rfcOrigen { get; set; }
    public string cbeOrigen { get; set; }
    public string bancoBeneficiario { get; set; }
    public string rfcDestino { get; set; }
    public string cbeBeneficiaria { get; set; }
    public string monto { get; set; }
    public string conceptoPago { get; set; }
    public string referencia { get; set; }
    public string estatusOperacion { get; set; }
    public string motivoRechazo { get; set; }
    public string udn { get; set; }
    public string folioPaquete { get; set; }
}

