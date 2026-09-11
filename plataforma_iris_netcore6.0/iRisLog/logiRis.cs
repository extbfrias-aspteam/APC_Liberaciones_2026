using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace iRisLog
{
    public class logiRis
    {
        private static string Path = Environment.CurrentDirectory + "/Logs/";

        private static string PathSeg = Environment.CurrentDirectory + "/Logs_Seg/";

        public static void Add(string sLog, string tipoLog = "OPE")
        {
            CreateDirectory(tipoLog);
            string nombre = GetNameFile();
            string cadena = "";

            try
            {
                StreamWriter sw = null;

                cadena += DateTime.Now + " - " + sLog + Environment.NewLine;

                if (tipoLog == "OPE")
                {
                    sw = new StreamWriter(Path + "/" + nombre, true);

                }
                else
                {
                    sw = new StreamWriter(PathSeg + "/" + nombre, true);

                }
                sw.Write(cadena);
                sw.Close();
            }
            catch (Exception ex)
            {
                Console.WriteLine("Error writing log: " + ex.Message);
            }

        }

        #region HELPER
        private static string GetNameFile()
        {
            string nombre = "";

            nombre = "log_" + DateTime.Now.Year + "_" + DateTime.Now.Month + "_" + DateTime.Now.Day + ".txt";

            return nombre;
        }

        private static void CreateDirectory(string tipoLog)
        {
            try
            {
                if (tipoLog == "OPE")
                {
                    if (!Directory.Exists(Path))
                        Directory.CreateDirectory(Path);
                }
                else
                {
                    if (!Directory.Exists(PathSeg))
                        Directory.CreateDirectory(PathSeg);
                }

            }
            catch (DirectoryNotFoundException ex)
            {
                //throw new Exception(ex.Message);
                Console.WriteLine("Error writing log: " + ex.Message);
            }
        }
        #endregion

    }
}

