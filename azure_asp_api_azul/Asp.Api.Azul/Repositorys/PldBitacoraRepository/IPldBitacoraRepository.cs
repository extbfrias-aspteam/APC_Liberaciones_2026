using Asp.Api.Azul.Entities.DataBase;

namespace Asp.Api.Azul.Repositorys.PldBitacoraRepository
{
    public interface IPldBitacoraRepository
    {
        Task Insert(BitacoraPld bitacora);
    }
}
