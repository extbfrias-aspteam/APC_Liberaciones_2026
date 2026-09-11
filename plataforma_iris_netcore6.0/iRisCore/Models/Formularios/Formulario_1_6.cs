using iRisCore.Models.Interfaces;
using iText.IO.Font.Constants;
using iText.IO.Image;
using iText.Kernel.Font;
using iText.Kernel.Pdf;
using iText.Layout;
using iText.Layout.Element;
using iText.Layout.Properties;

namespace iRisCore.Models.Formularios
{
    public class Formulario_1_6 : IFormulario
    {
        public string Id { get; } = "1_6"; // ID del formulario

        public byte[] GenerarPDF(MyDataModel registro, Dictionary<string, string> fields)
        {
            byte[] pdfBytes;
            PdfFont arialFont = PdfFontFactory.CreateFont(StandardFonts.HELVETICA);
            string Descripcion = fields.ContainsKey("Descripcion") ? fields["Descripcion"] : string.Empty;
            string Volumnen = fields.ContainsKey("Volumnen") ? fields["Volumnen"] : string.Empty;
            string MontoAgregado = fields.ContainsKey("MontoAgregado") ? fields["MontoAgregado"] : string.Empty;
            string NumeroCuenta = fields.ContainsKey("NumeroCuenta") ? fields["NumeroCuenta"] : string.Empty;

                string Limite = fields.ContainsKey("Limite") ? fields["Limite"] : string.Empty;
            string FechaAlcanzaron = fields.ContainsKey("FechaAlcanzaron") ? fields["FechaAlcanzaron"] : string.Empty;
            string solicitud = fields.ContainsKey("solicitud") ? fields["solicitud"] : string.Empty;




            // Creamos un MemoryStream para almacenar el PDF
            using (MemoryStream ms = new MemoryStream())
            {
                // Inicializamos el documento PDF
                using (PdfWriter writer = new PdfWriter(ms))
                {
                    using (PdfDocument pdf = new PdfDocument(writer))
                    {
                        // Creamos una nueva página
                        Document document = new Document(pdf);
                        document.SetMargins(14, 70, 70, 10); // 72 puntos = 2.54 cm superior, 108 puntos = 3.81 cm izquierda, 54 puntos = 1.91 cm inferior, derecha

                        string cabeceraImagePath = Path.Combine("wwwroot", "images", "BO_imagenes", "asp_Heard.jpg");
                        string cabeceraImagePathDerecha = Path.Combine("wwwroot", "images", "BO_imagenes", "asp_soluciones.jpg");
                        string pieImagePath = Path.Combine("wwwroot", "images", "BO_imagenes", "asp_piedepagina.jpg");

                        //string cabeceraImagePath = "wwwroot/images/BO_imagenes/asp.jpg";
                        //string pieImagePath = "ruta_de_la_imagen_de_pie.jpg";
                        DateTime currentDate = DateTime.Now;
                        string formattedDate = currentDate.ToString("dd 'de' MMMM 'de' yyyy");

                        Image cabeceraImage = new Image(ImageDataFactory.Create(cabeceraImagePath));
                        Image cabeceraImageDerecha = new Image(ImageDataFactory.Create(cabeceraImagePathDerecha));

                        // Establecer dimensiones
                        cabeceraImage.SetWidth(UnitValue.CreatePointValue(20.27f * 28.35f)); // Convertir cm a puntos (1 cm = 28.35 puntos)
                        cabeceraImage.SetHeight(UnitValue.CreatePointValue(1.72f * 28.35f)); // Convertir cm a puntos (1 cm = 28.35 puntos)
                        cabeceraImageDerecha.SetWidth(UnitValue.CreatePointValue(7.04f * 28.35f)); // Convertir cm a puntos (1 cm = 28.35 puntos)
                        cabeceraImageDerecha.SetHeight(UnitValue.CreatePointValue(1.03f * 28.35f)); // Convertir cm a puntos (1 cm = 28.35 puntos)

                        document.Add(cabeceraImage);

                        Text textoNegrita = new Text("BANCO DE MÉXICO\nDirección de Operación y Continuidad\n" +
                            "de Sistemas de Pagos e Infraestructura de Mercados\n")
                            .SetBold()
                            .SetFont(arialFont)
                            .SetFontSize(10);

                        // Resto del texto
                        String restoDelTexto = "Avenida 5 de Mayo número 6, Planta Baja,\n" +
                            "Colonia Centro, Alcaldía Cuauhtémoc,\n" +
                            "C.P. 06000, Ciudad de México\n";
                        Paragraph paragraph2 = new Paragraph()
                            .SetTextAlignment(TextAlignment.LEFT)
                            .SetFontSize(10)
                            .SetFont(arialFont)
                            .SetMultipliedLeading(1.0f)
                            .SetMarginLeft(36)
                            .Add(textoNegrita)
                            .Add(restoDelTexto);

                        Paragraph paragraph1 = new Paragraph("La Paz B.C.S., a " + formattedDate)
                            .SetTextAlignment(TextAlignment.RIGHT)
                            .SetFontSize(10)
                            .SetFont(arialFont)
                            .SetMarginLeft(36);

                        Text textoCursiva = new Text("OPCIONES EMPRESARIALES DEL NORESTE SA DE CV SFP")
                            .SetItalic()
                            .SetFont(arialFont)
                            .SetFontSize(10);
                        Text texto1 = new Text("A través de la presente ")
                            .SetFont(arialFont)
                            .SetFontSize(10);


                        Paragraph paragraph8 = new Paragraph()
                            .SetTextAlignment(TextAlignment.JUSTIFIED)
                            .SetFontSize(10)
                            .SetFont(arialFont)
                            .SetMultipliedLeading(1.0f)
                            .SetMarginLeft(36)
                            .Add(texto1)
                            .Add(textoCursiva)
                            .Add(" reporta a este Instituto Central los datos del Participante Indirecto, " +
                     "con el cual este Participante ha celebrado un Contrato de Servicios de " +
                     "Participación Indirecta, que alcanzó los límites operativos establecidos " +
                     "en la 9ª Bis 9, de las Reglas del SPEI, contenidas en la Circular 14/2017, " +
                     "emitida por el Banco de México.\n\n");

                        // Agregar párrafos al documento
                        document.Add(paragraph1);
                        document.Add(paragraph2);
                        document.Add(paragraph8);

                        #region Tabla

                        Table table = new Table(1); // 1 columnas
                        table.SetWidth(UnitValue.CreatePercentValue(100))
                            .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                            .SetMarginLeft(36);// Ancho de la tabla

                        // Agregar celdas a la tabla
                        table.AddCell(CreateCell("Participante SPEI", 1, TextAlignment.CENTER, true)
                            .SetFont(arialFont)
                            .SetFontSize(10));
                        table.AddCell(CreateCell("90659 " , 1, TextAlignment.CENTER, true)
                            .SetFont(arialFont)
                            .SetFontSize(10));
                        table.AddCell(CreateCell("Participante Indirecto: " + Descripcion, 1, TextAlignment.LEFT, false)
                            .SetFont(arialFont)
                            .SetFontSize(10));
                       
                        table.AddCell(CreateCell("Volumen de transferencias de fondos enviados o recibidas:  " + Volumnen, 1, TextAlignment.LEFT, false)
                            .SetFont(arialFont)
                            .SetFontSize(10));
                        table.AddCell(CreateCell("Monto agregado de las transferencias enviadas o recibidas:  "+ MontoAgregado, 1, TextAlignment.LEFT, false)
                            .SetFont(arialFont)
                            .SetFontSize(10));
                        
                        table.AddCell(CreateCell("Número de Cuentas de Cliente Indirecto:  "+ NumeroCuenta, 1, TextAlignment.LEFT, false)
                            .SetFont(arialFont)
                            .SetFontSize(10));
                        table.AddCell(CreateCell("Límite(s) alcanzado(s):  "+ Limite, 1, TextAlignment.LEFT, false)
                            .SetFont(arialFont)
                            .SetFontSize(10));
                        table.AddCell(CreateCell("Fecha en la que se alcanzaron:  "+ FechaAlcanzaron, 1, TextAlignment.LEFT, false)
                            .SetFont(arialFont)
                            .SetFontSize(10));
                        table.AddCell(CreateCell("Señalar si el Participante Indirecto ya presentó su solicitud de admisión como Participante:  "+ solicitud, 1, TextAlignment.LEFT, false).SetFont(arialFont).SetFontSize(10));

                        // Agregar tabla al documento
                        document.Add(table);
                        #endregion

                        Paragraph paragraph3 = new Paragraph("\n\nA t e n t a m e n t e,")
                            .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                            .SetFontSize(10);
                        document.Add(paragraph3);

                        Paragraph paragraph4 = new Paragraph("OPCIONES EMPRESARIALES DEL NORESTE SA DE CV SFP")
                            .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                            .SetFontSize(10);
                        document.Add(paragraph4);

                        Paragraph paragraph5 = new Paragraph("Rafael Alberto Toscano Sánchez y Responsable de Cumplimiento Normativo SPEI")
                            .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                            .SetFontSize(10);
                        document.Add(paragraph5);


                        Paragraph paragraph7 = new Paragraph("\r\n121 Se contemplan tanto las operaciones enviadas como las recibidas por el Participante Indirecto, en un periodo máximo de doce meses calendarios consecutivos\r\n122 Se contemplan tanto las transferencias enviadas como las recibidas por el Participante Indirecto, en un periodo mínimo de doce meses calendarios consecutivos.\r\n123 Información de las cuentas de clientes indirectos que hayan registrado saldos positivos, en cualquier momento, o que con cargo a tales cuentas se hubiera enviado, al menos, una transferencia de fondos en los últimos doce meses calendario.\r\n124 Listar las categorías que alcanzaron los límites establecidos en la 9ª. Bis 9 de las Reglas del SPEI.\r\n\n")
                            .SetTextAlignment(TextAlignment.JUSTIFIED)
                            .SetFontSize(7)
                            .SetFont(arialFont)
                            .SetMultipliedLeading(1.0f)
                            .SetMarginLeft(36);
                        document.Add(paragraph7);

                        Image pieImage = new Image(ImageDataFactory.Create(pieImagePath));
                        pieImage.SetFixedPosition(10, 0); // Set the absolute position (X, Y)


                        pieImage.SetWidth(20.27f * 28.35f);
                        pieImage.SetHeight(1.72f * 28.35f);
                        document.Add(pieImage);
                        // Añadir la imagen al documento
                        document.Add(pieImage);

                        // Cerrar el documento

                        static Cell CreateCell(string content, int colspan, TextAlignment alignment, bool isHeader)
                        {
                            Cell cell = new Cell(1, colspan)
                                .Add(new Paragraph(content))
                                .SetTextAlignment(alignment)
                                .SetPadding(5);
                            if (isHeader)
                            {
                                cell.SetBold();
                            }
                            return cell;
                        }
                        document.Close();
                        // Establecer el tipo de contenido de la respuesta
                        //Response.ContentType = "application/pdf";
                    }

                    // Convertimos el MemoryStream en un array de bytes
                    pdfBytes = ms.ToArray();
                }
            }

            return pdfBytes;
        }
    }
}
