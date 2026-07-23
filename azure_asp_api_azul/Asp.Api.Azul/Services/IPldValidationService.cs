namespace Asp.Api.Azul.Services
{
    public interface IPldValidationService
    {
        Task ValidatePersonAsync(string nombre, string apellidoPaterno, string apellidoMaterno, string rfc, string curp, string personalidad, int usuarioSolicitante);
        Task ValidateCompanyAsync(string razonSocial, string rfc, string personalidad, int usuarioSolicitante);
    }
}