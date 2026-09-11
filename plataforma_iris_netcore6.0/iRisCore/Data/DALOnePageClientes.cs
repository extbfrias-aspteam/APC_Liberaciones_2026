using DocumentFormat.OpenXml.Bibliography;
using DocumentFormat.OpenXml.Drawing;
using DocumentFormat.OpenXml.Drawing.Charts;
using DocumentFormat.OpenXml.Office2010.Excel;
using iRisModel;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Data.Sqlite;
using System.Net;
using System.Web.Helpers;
using static ClosedXML.Excel.XLPredefinedFormat;

namespace iRisCore.Data
{
    public class DALOnePageClientes
    {

        public DALOnePageClientes() 
        {
           
        }

        public static List<ClienteB2BModel> GetClienteB2B()
        {
            List<ClienteB2BModel> objCliente  = new List<ClienteB2BModel> ();

            try
            {

                using (var connection = new SqliteConnection("Data Source=dbOnePage.db"))
                {
                    connection.Open();                   


                    var command = connection.CreateCommand();
                    //command.CommandText = @"SELECT * FROM idioma WHERE idIdioma = $id ";
                    command.CommandText = @"select c.idCliente, c.Rfc, c.razonSocial, c.Nombre, c.direccionFiscal, c.idIdioma, c.tieneTraductor, c.horarioAtencion, c.numeroCliente, c.numeroCuenta, c.cuentaClabe, t.descripcionTipoCliente,
                                            c.estatusCliente from cliente c inner join TipoCliente t on (c.idTipoCliente = t.idTipoCliente) order by nombre asc";

                    using (SqliteDataReader reader = command.ExecuteReader())
                    {
                        while (reader.Read())
                        {

                            objCliente.Add(new ClienteB2BModel
                            {
                                idCliente = Convert.ToInt32(reader["idCliente"]),
                                Rfc = reader["rfc"].ToString(),
                                razonSocial = reader["razonSocial"].ToString(),
                                Nombre = reader["Nombre"].ToString(),
                                direccionFiscal = reader["direccionFiscal"].ToString(),
                                idIdioma = Convert.ToInt32(reader["idIdioma"]),
                                tieneTraductor = reader["tieneTraductor"].ToString(),
                                horarioAtencion = reader["horarioAtencion"].ToString(),                               
                                numeroCliente = Convert.ToInt32(reader["numeroCliente"]),
                                numeroCuenta = reader["numeroCuenta"].ToString(),
                                cuentaClabe = reader["cuentaClabe"].ToString(),                                
                                descripcionTipoCliente = reader["descripcionTipoCliente"].ToString().ToUpper(),
                                estatusCliente = reader["estatusCliente"].ToString()
                            });

                        }
                    }
                }
                return objCliente;

            }
            catch (Exception ex)
            {
                Console.Error.WriteLine(ex.Message);
                return null;
            }
        }


        public static ClienteB2BModel SetClienteB2B(ClienteB2BModel model)
        {
            try
            {

                using (var connection = new SqliteConnection("Data Source=dbOnePage.db"))
                {
                    connection.Open();
                    var command = connection.CreateCommand();
                   
                    string sql = @"insert into cliente (Rfc, razonSocial,Nombre,direccionFisica,idIdioma,tieneTraductor,horarioAtencion,numeroCliente,numeroCuenta,cuentaClabe,idTipoCliente,estatusCliente,modeloNegocio) " 
                                        + "values (@Rfc, @razonSocial,@Nombre,@direccionFiscal,@idIdioma,@tieneTraductor,@horarioAtencion,@numeroCliente,@numeroCuenta,@cuentaClabe,@idTipoCliente,@estatusCliente,@modeloNegocio)";
                    string result = "Data Saved";
                    using (var cmd = new SqliteCommand(sql, connection))
                    {
                        try
                        {
                            cmd.Parameters.AddWithValue("@Rfc", model.Rfc);
                            cmd.Parameters.AddWithValue("@razonSocial", model.razonSocial);
                            cmd.Parameters.AddWithValue("@Nombre", model.Nombre);
                            cmd.Parameters.AddWithValue("@direccionFiscal", model.direccionFiscal);
                            cmd.Parameters.AddWithValue("@idIdioma", model.idIdioma);
                            cmd.Parameters.AddWithValue("@tieneTraductor", model.tieneTraductor);
                            cmd.Parameters.AddWithValue("@horarioFinal", model.horarioAtencion);
                            cmd.Parameters.AddWithValue("@numeroCliente", model.numeroCliente);
                            cmd.Parameters.AddWithValue("@numeroCuenta", model.numeroCuenta);
                            cmd.Parameters.AddWithValue("@cuentaClabe", model.cuentaClabe);
                            cmd.Parameters.AddWithValue("@idTipoCliente", model.idTipoCliente);
                            cmd.Parameters.AddWithValue("@estatusCliente", model.estatusCliente);
                            cmd.Parameters.AddWithValue("@modeloNegocio", model.modeloNegocio);                        
                                
                                

                            cmd.ExecuteNonQuery();
                        }
                        catch (Exception ex)
                        {
                            result = ex.ToString();
                        }
                    }
                    return model;
                }

            }
            catch (Exception ex)
            {
                Console.Error.WriteLine(ex.Message);
                return null;
            }
        }


        public static List<TipoClienteSpeiModel> GetTipoClienteSpei()
        {
            List<TipoClienteSpeiModel> objCliente = new List<TipoClienteSpeiModel>();

            try
            {     
                using (var connection = new SqliteConnection("Data Source=dbOnePage.db"))
                {
                    connection.Open();

                    var command = connection.CreateCommand();
                    command.CommandText = @"select * from TipoCliente order by idTipoCliente asc";           

                    using (var reader = command.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            objCliente.Add(new TipoClienteSpeiModel
                            {
                                idTipoCliente = Convert.ToInt32(reader["idTipoCliente"]),
                                descripcionTipoCliente = reader["descripcionTipoCliente"].ToString(),
                                }
                            );
                        }
                    }
                }
                return objCliente;
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine(ex.Message);
            }

            return null;
        }


        public static List<IdiomaModel> GetIdioma()
        {
            List<IdiomaModel> objIdioma = new List<IdiomaModel>();

            try
            {
                using (var connection = new SqliteConnection("Data Source=dbOnePage.db"))
                {
                    connection.Open();

                    var command = connection.CreateCommand();
                    command.CommandText = @"select * from Idioma order by idIdioma asc";

                    using (var reader = command.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            objIdioma.Add(new IdiomaModel
                            {
                                idIdioma = Convert.ToInt32(reader["idIdioma"]),
                                descripcion = reader["Descripcion"].ToString(),
                            }
                            );
                        }
                    }
                }
                return objIdioma;
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine(ex.Message);
            }

            return null;
        }


        //public static List<TipoClienteSpeiModel> GetTipoClienteSpei()
        //{
        //    try
        //    {
        //        using (var connection = new SqliteConnection("Data Source=dbOnePage.db"))
        //        {                  
        //            connection.Open();

        //            var command = connection.CreateCommand();
        //            command.CommandText =@"SELECT * FROM idioma WHERE idIdioma = $id ";
        //            command.Parameters.AddWithValue("$id", 1);

        //            using (var reader = command.ExecuteReader())
        //            {
        //                while (reader.Read())
        //                {
        //                    var name = reader.GetString(1);

        //                    Console.WriteLine($"Hello, {name}!");
        //                }
        //            }
        //        }

        //    } catch (Exception ex)
        //    {
        //        Console.Error.WriteLine(ex.Message );
        //    }

        //    return null;
        //}

    }
}
