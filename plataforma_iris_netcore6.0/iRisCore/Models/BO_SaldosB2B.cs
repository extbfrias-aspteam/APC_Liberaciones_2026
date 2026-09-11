using System;
using System.Collections.Generic;


public class ParticipanteSaldo
{
    public int IdParticipante { get; set; }
    public string NomParticipante { get; set; }
    public string CuentaClabe { get; set; }
    public decimal Saldo { get; set; }
}


public class ParticipanteSaldoUdn
{
    public int idUdn { get; set; }
    public string nomUdn { get; set; }
    public string cuentaClabe { get; set; }
    public decimal saldo { get; set; }
}
public class ConsultaRequest
{
    public DateTime FechaInicio { get; set; }
    public DateTime FechaActual { get; set; }
    public string Descripcion { get; set; }
}