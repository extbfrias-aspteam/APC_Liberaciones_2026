using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace iRisCore.Controllers
{
    public class ReporteDiarioActividadDiariaController : Controller
    {
        [HttpGet]
        [Authorize]
        public IActionResult ReporteDiarioActividadDiaria()
        {
            return View();
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> ReadFileDA(IList<IFormFile> files)
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
            catch (IOException ex)
            {
                logiRis.Add("Reportería DA:" + ex.Message);
            }

            List<DAModel> lstActividadDiaria = new List<DAModel>();

            try
            {
                foreach (string row in csvData.Split('\n'))
                {
                    if (!string.IsNullOrEmpty(row))
                    {
                        if (row.Substring(0, 2) == "19")
                        {

                        }
                        else if (row.Substring(0, 2) == "99")
                        {

                        }
                        else
                        { 

                            lstActividadDiaria.Add(new DAModel
                            {
                                NumeroTarjeta = row.Substring(2, 19).TrimStart(),
                                FechaTransaccion = !string.IsNullOrEmpty(row.Substring(21, 8).TrimStart()) ? row.Substring(21, 8).Substring(0, 4) + "/" + row.Substring(21, 8).Substring(4, 2) + "/" + row.Substring(21, 8).Substring(6, 2) : "",
                                HoraTransaccion = !string.IsNullOrEmpty(row.Substring(29, 6).TrimStart()) ? row.Substring(29, 6).Substring(0, 2) + ":" + row.Substring(29, 6).Substring(2, 2) + ":" + row.Substring(29, 6).Substring(4, 2) : "",
                                Importe = !string.IsNullOrEmpty(row.Substring(35, 11).TrimStart()) ? string.Format("{0:C2}", Convert.ToDouble(row.Substring(35, 11).Substring(0, 9) + "." + row.Substring(35, 11).Substring(9, 2))) : "",
                                ImporteUSD = !string.IsNullOrEmpty(row.Substring(46, 11).TrimStart()) ? string.Format("{0:C2}", Convert.ToDouble(row.Substring(46, 11).Substring(0, 9) + "." + row.Substring(46, 11).Substring(9, 2))) : "",
                                NumeroComercio = row.Substring(57, 9).TrimStart(),
                                Secuencia = row.Substring(66, 12).TrimStart(),
                                ClaveTransaccion = row.Substring(77, 2).TrimStart(),
                                ClaveRespuesta = row.Substring(80, 3).TrimStart(),
                                NumeroAutorizacion = row.Substring(83, 6).TrimStart(),
                                DescripcionComercio = row.Substring(89, 50).TrimStart(),
                                DescripcionPaisOrigen = row.Substring(139, 50).TrimStart(),
                                ConceptoMovimiento = row.Substring(189, 50).TrimStart(),
                                NumeroEmpleado = row.Substring(239, 11).TrimStart(),
                                MAaReportar = row.Substring(250, 50).TrimStart(),
                                CuentaCacao = row.Substring(300, 50).TrimStart(),
                                CCEmpresaSubempresa = row.Substring(350, 50).TrimStart()
                            });
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                return Ok(ex.Message);
            }

            return Json(lstActividadDiaria);
        }
    }
}
