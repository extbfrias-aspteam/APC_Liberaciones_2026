namespace iRisCore.Models.Interfaces
{
    public interface IFormulario
    {
        byte[] GenerarPDF(MyDataModel registro, Dictionary<string, string> fields);
        string Id { get; }
    }
}
