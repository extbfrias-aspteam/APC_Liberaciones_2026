using Asp.Api.Azul.Entities.DataBase;
using Asp.Api.Azul.Infrastructure.Resilience;
using Microsoft.Extensions.Configuration;
using Npgsql;

namespace Asp.Api.Azul.Repositorys.PldBitacoraRepository
{
    public class PldBitacoraRepository : IPldBitacoraRepository
    {
        private readonly IConfiguration _configuration;
        private readonly ResilientExecutor _resilientExecutor;

        public PldBitacoraRepository(IConfiguration configuration, ResilientExecutor resilientExecutor)
        {
            _configuration = configuration;
            _resilientExecutor = resilientExecutor;
        }

        public async Task Insert(BitacoraPld bitacora)
        {
            await _resilientExecutor.ExecuteAsync(async () =>
            {
                using var connection = new NpgsqlConnection(_configuration.GetConnectionString("BluDbConnection"));
                await connection.OpenAsync();

                var query = "INSERT INTO bitacora_pld (rfc, curp, id_pblu, request_body, error_message) " +
                    "VALUES(@rfc, @curp, @id_pblu, @request_body, @error_message);";

                using var command = new NpgsqlCommand(query, connection);
                command.Parameters.AddWithValue("@rfc", (object?)bitacora.rfc ?? DBNull.Value);
                command.Parameters.AddWithValue("@curp", (object?)bitacora.curp ?? DBNull.Value);
                command.Parameters.AddWithValue("@id_pblu", (object?)bitacora.id_pblu ?? DBNull.Value);
                command.Parameters.AddWithValue("@request_body", (object?)bitacora.request_body ?? DBNull.Value);
                command.Parameters.AddWithValue("@error_message", (object?)bitacora.error_message ?? DBNull.Value);

                await command.ExecuteNonQueryAsync();
            });
        }
    }
}
