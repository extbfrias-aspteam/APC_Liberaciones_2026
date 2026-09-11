using Fingers10.ExcelExport.Attributes;
namespace iRisCore.ExcelTemplate
{
    public class InventarioTarjeta
    {
        [IncludeInReport(Order = 1)]
        public string PAN { get; set; }

        [IncludeInReport(Order = 2)]
        public string Estatus { get; set; }

        [IncludeInReport(Order = 3)]
        public string Manufactura { get; set; }
    }
}
