using System;
using System.Collections.Generic;

public class Pagination
{
    public int totalRecords { get; set; }
    public int currentPage { get; set; }
    public int totalPages { get; set; }
    public int? nextPage { get; set; }
    public int? prevPage { get; set; }
}

public class MyDataModel
{
    public int id { get; set; }
    public int? prefijo { get; set; }
    public int tipo { get; set; }
    public string clabe { get; set; }
    public string descripcion { get; set; }
    public bool activo { get; set; }
    // public DateTime fechaCreacion { get; set; }
    public string usuarioCreacion { get; set; }
    public object contadorUDN { get; set; }
    public object rfc { get; set; }
    public object domicilio { get; set; }
    public object localidad { get; set; }
    public object colonia { get; set; }
    public object codigoPostal { get; set; }
    public object telefono { get; set; }
    public object fechaInicioMonto { get; set; }
    public object fechaFinMonto { get; set; }
    public int? limite { get; set; }
    public object aliado { get; set; }
    public bool cuentaConcentradora { get; set; }
    public decimal? limitePortal { get; set; }
    public object fechaCreacion { get; set; }
}

public class MyDataResponse2
{
    public string EiyuResponseCode { get; set; }
    public string Descripcion { get; set; }
    public dynamic Content { get; set; }

}

public class MyDataResponse
{
    public List<ContentItem> Content { get; set; }
}

public class ContentItem
{
    public int id { get; set; }
    public int? prefijo { get; set; }
    public int tipo { get; set; }
    public string clabe { get; set; }
    public string descripcion { get; set; }
    public bool Activo { get; set; }
    // Agrega las demás propiedades según la estructura de los datos
}
public class ParticipanteFormulario164
{
    public int idPblu { get; set; }

    public string descripcionPblu { get; set; }
    public int volumenTransferenciaEnviados { get; set; }
    public int volumenTransferenciaRecibidos { get; set; }
    public int montoEnviados { get; set; }
    public int montoRecibidos { get; set; }

    public int numerosCuenta { get; set; }
    // Agrega las demás propiedades según la estructura de los datos
}
public class ParticipanteFormulario16
{
    public string eiyuResponseCode { get; set; }
    public Content content { get; set; }

    public class Content
    {
        public int idPblu { get; set; }
        public string descripcionPblu { get; set; }
        public int volumenTransferenciaEnviados { get; set; }
        public int volumenTransferenciaRecibidos { get; set; }
        public double montoEnviados { get; set; }
        public double montoRecibidos { get; set; }
        public int numerosCuenta { get; set; }
    }


}
public class BitacoraRequest
{
    public int pblu { get; set; }
    public int idFormulario { get; set; }
    public string tramaJson { get; set; }
    public string usuarioCreacion { get; set; }
}

public class ParticipanteSaldos
{
    public int Id_pblu { get; set; }
    public string Descripcion { get; set; }

    public decimal Saldo { get; set; }
}

public class Udns
{
    public int IdUdn { get; set; }
    public string Descripcion { get; set; }
    public decimal? Saldo { get; set; }
    public bool Activo { get; set; }
    public string FechaCreacion { get; set; }
    public string UsuarioCreacion { get; set; }
    public int Pblu { get; set; }
    public decimal? SaldoMin { get; set; }
    public bool NotificacionActiva { get; set; }
    public string PrefijoClabe { get; set; }
    public int ContadorClabe { get; set; }
    public string Clabe { get; set; }
    public decimal? MontoLimite { get; set; }
    public decimal? LimitePortal { get; set; }
}

public class ConfigNotif
{
    //public int Id { get; set; }
    public int IdPblu { get; set; }
    public string Participante { get; set; }
    public int IdUdn { get; set; }
    public string Nombre { get; set; }
    public string TipoNotif { get; set; }
    public string FchInicial { get; set; }
    public string FchFinal { get; set; }
    public int Periodicidad { get; set; }
    public string Inicio { get; set; }
    public string Telefono { get; set; }
    public string Email { get; set; }
    //public string HoraUltNot { get; set; }
}

public class NewConfigNotif
{
    public string tipoNotif { get; set; }
    public string idPblu { get; set; }
    public string idUdn { get; set; }
    public string email { get; set; }
    public string telefono { get; set; }
    public string fchInicial { get; set; }
    public string fchFinal { get; set; }
    public string horaIniNot { get; set; }
    public string periodicidad { get; set; }
}

public class ResponseConfigNotif
{
    public int Codigo { get; set; }
    public string Mensaje { get; set; }
    public dynamic Data { get; set; }
}