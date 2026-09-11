using DocumentFormat.OpenXml.Drawing.Charts;
using Fingers10.ExcelExport.Attributes;

namespace iRisCore.ExcelTemplate
{
    public class MovtoReport
    {
        [IncludeInReport(Order = 1)]
        public string Autorizacion { get; set; }

        [IncludeInReport(Order = 2)]
        public string ClaveMovimiento { get; set; }

        [IncludeInReport(Order = 3)]
        public string ConceptoMovimiento { get; set; }

        [IncludeInReport(Order = 4)]
        public string DescripcionComercio { get; set; }

        [IncludeInReport(Order = 5)]
        public string DescripcionPais { get; set; }

        [IncludeInReport(Order = 6)]
        public string DescripcionPostOperacion { get; set; }

        [IncludeInReport(Order = 7)]
        public string EstatusPostOperacion { get; set; }

        [IncludeInReport(Order = 8)]
        public DateTime FechaMovimiento { get; set; }

        [IncludeInReport(Order = 9)]
        public string IDMovimiento { get; set; }

        [IncludeInReport(Order = 10)]
        public double ImporteMovimiento { get; set; }

        [IncludeInReport(Order = 11)]
        public double ImporteOrigenMovimiento { get; set; }

        [IncludeInReport(Order = 12)]
        public string MCC { get; set; }

        [IncludeInReport(Order = 13)]
        public string MonedaMovimiento { get; set; }

        [IncludeInReport(Order = 14)]
        public string MonedaOrigenMovimiento { get; set; }

        [IncludeInReport(Order = 15)]
        public string NumeroAfiliacionComercio { get; set; }

        [IncludeInReport(Order = 16)]
        public string Observaciones { get; set; }

        [IncludeInReport(Order = 17)]
        public string PaisComercio { get; set; }

        [IncludeInReport(Order = 18)]
        public int? Referencia { get; set; }

        [IncludeInReport(Order = 19)]
        public double SaldoDespuesMovimiento { get; set; }

        [IncludeInReport(Order = 20)]
        public string TipoMovimientoCargoAbono { get; set; }

    }
}