using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.IO;

namespace iRisCore.Controllers
{
    public class ReporteDiarioLiquidacionController : Controller
    {
        [HttpGet]
        [Authorize]
        public IActionResult ReporteDiarioLiquidacion()
        {
            return View();
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> ReadFileNLQ(IList<IFormFile> files)
        {
            
            string csvData = "";
            string[] valor;
            try
            {               
                long size = files.Sum(f => f.Length);
                foreach (var formFile in files)
                {
                    if (formFile.Length > 0)
                    {
                        var path = Environment.CurrentDirectory + "/Uploads/";
                        
                        if (!Directory.Exists(path))
                        {
                            Directory.CreateDirectory(path);
                        }
                        var filePath = path + Path.GetFileName(formFile.FileName);

                        using (var stream = System.IO.File.Create(filePath))
                        {
                            await formFile.CopyToAsync(stream);
                        }

                        csvData = System.IO.File.ReadAllText(filePath);
                        if (System.IO.File.Exists(filePath))
                            System.IO.File.Delete(filePath);
                    }
                }                
            }
            catch (IOException ex) {
                logiRis.Add("Reportería NLQ:" + ex.Message);
            }

            List<NLQModel> lstLiquidacion = new List<NLQModel>();

            try
            {
               

                foreach (string row in csvData.Split('\n'))
                {
                    if (!string.IsNullOrEmpty(row))
                    {
                        if (row.Substring(0, 15) == "ClaveMovimiento")
                        {

                        }
                        else
                        {                          

                            valor = row.ToString().Split('|');

                            lstLiquidacion.Add(new NLQModel
                            {
                                ClaveMovimiento = valor[0],
                                Movimiento = valor[1],
                                MovimientoOriginal = valor[2],
                                Tarjeta = valor[3],
                                CodigoTx = valor[4],
                                Comercio = valor[5],
                                Ciudad = valor[6],
                                Pais = valor[7],
                                MCC = valor[8],
                                Referencia = valor[9],
                                ImporteAut = string.Format("{0:#.00}", Double.Parse(valor[10]) / 100),
                                MonedaAut = valor[11],
                                ImporteOtraMoneda = string.Format("{0:#.00}", Double.Parse(valor[12]) / 100),
                                OtraMoneda = valor[13],
                                ImporteCompensado = string.Format("{0:#.00}", Double.Parse(valor[14]) / 100),
                                MonedaCompensacion = valor[15],
                                Autorizacion = valor[16],
                                FechaAut = valor[17],
                                FechaPresentacion = valor[18],
                                FechaProcesamiento = valor[19],
                                FechaJuliana = valor[20],
                                Compensada = valor[21],
                            });                            
                        }
                    }
                }
            }
            catch (Exception ex)
            { }

                return Json(lstLiquidacion);
        }



        //            var resultCompensadas = (from m in lstLiquidacion
        //                                     where m.Compensada == "COMPENSADA"
        //                                     select m).ToList();

        //            objLiquidacion.sumNLQCompensada = "0.00";

        //            if (resultCompensadas.Count > 0)
        //            {
        //                objLiquidacion.sumNLQCompensada = string.Format("{0:#.00}", resultCompensadas.Sum(x => Convert.ToDouble(x.ImporteCompensado)));
        //            }

        //            var resultDevueltas = (from m in lstLiquidacion
        //                                   where m.Compensada == "DEVUELTA"
        //                                   select m).ToList();

        //            objLiquidacion.sumNLQDevuelta = "0.00";

        //            if (resultDevueltas.Count > 0)
        //            {
        //                objLiquidacion.sumNLQDevuelta = string.Format("{0:#.00}", resultDevueltas.Sum(x => Convert.ToDouble(x.ImporteCompensado)));
        //            }

        //            objLiquidacion.totalNQLLiquidacion = string.Format("{0:#.00}", double.Parse(objLiquidacion.sumNLQCompensada) - double.Parse(objLiquidacion.sumNLQDevuelta));


        //            objLiquidacion.detalleNLQ = lstLiquidacion;

        //            jsonresult = Json(new { Tipo = "Success", FileName = objLiquidacion, Message = "Archivo procesado correctamente" }, JsonRequestBehavior.AllowGet);
        //            jsonresult.MaxJsonLength = int.MaxValue;

        //        }
        //        catch (Exception ex)
        //        {
        //            jsonresult = Json(new { Tipo = "Error", FileName = objLiquidacion, Message = ex.Message }, JsonRequestBehavior.AllowGet);
        //        }
        //    }
        //    return jsonresult;
        //}

    }
}
