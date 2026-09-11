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
    public class Formulario_1_2 : IFormulario
    {
        public string Id { get; } = "1_2"; // ID del formulario

        public byte[] GenerarPDF(MyDataModel registro, Dictionary<string, string> fields)
        {
            byte[] pdfBytes;
            PdfFont arialFont = PdfFontFactory.CreateFont(StandardFonts.HELVETICA);
            string Descripcion = fields.ContainsKey("Descripcion") ? fields["Descripcion"] : string.Empty;
            string RFC = fields.ContainsKey("RFC") ? fields["RFC"] : string.Empty;
            string Tercia = fields.ContainsKey("Tercia") ? fields["Tercia"] : string.Empty;
            string CLABE = fields.ContainsKey("CLABE") ? fields["CLABE"] : string.Empty;
            string Fecha = fields.ContainsKey("Fecha") ? fields["Fecha"] : string.Empty;


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
                        string formattedDate2 = currentDate.ToString("yyyy'-'MM'-'dd");
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
                            .Add("SFP reporta a este Instituto Central la información de los conjuntos de CLABE que han sido asignados en sustento de los Servicios Participación Indirecta que este Participante del SPEI ofrece. Lo anterior, conforme a la regulación aplicable para el esquema de participación indirecta detallado en las Reglas del SPEI, contenidas en la Circular 14/2017, emitida por el Banco de México.  " +
                            "\n\n");

                        // Agregar párrafos al documento
                        document.Add(paragraph1);
                        document.Add(paragraph2);
                        document.Add(paragraph8);

                        #region Tabla
                        // Calcular el ancho de la página
                        float pageWidth = document.GetPdfDocument().GetDefaultPageSize().GetWidth();

                        // Calcular el ancho de la tabla
                        float tableWidth = pageWidth - document.GetLeftMargin() - document.GetRightMargin();

                        // Crear la tabla con el ancho calculado
                        Table table = new Table(new float[] { 1, 1, 1, 1, 1 });
                        table.SetMarginLeft(30);
                        // Celda de encabezado que abarca 11 columnas (Participante SPEI)
                        Cell headerCell = new Cell(1, 5).Add(new Paragraph("Participante SPEI "));
                        headerCell.SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                            .SetFontSize(10);

                        table.AddCell(headerCell);

                        // Celda de encabezado que abarca 11 columnas (Participante SPEI)
                        Cell headerCell2 = new Cell(1, 5).Add(new Paragraph("90659" ));
                        headerCell2.SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                            .SetFontSize(10);

                        table.AddCell(headerCell2);

                        Cell cell11 = new Cell(2, 1).Add(new Paragraph("#"))
                        .SetFont(arialFont)
                         .SetFontSize(10);
                        table.AddCell(cell11);

                        Cell cell12 = new Cell(1, 2).Add(new Paragraph("\n\nParticipante Indirecto\n\n"))
                             .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                         .SetFontSize(10);
                        table.AddCell(cell12);

                        Cell cell13 = new Cell(1, 2).Add(new Paragraph("\n\nInformación de los Clientes Indirectos\n\n"))
                             .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                         .SetFontSize(10);
                        table.AddCell(cell13);


                        Cell cell14 = new Cell(1, 1).Add(new Paragraph("\n\n\n\nDenominación o razón social\n\n\n\n"))
                             .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                         .SetFontSize(10);
                        table.AddCell(cell14);
                        Cell cell15= new Cell(1, 1).Add(new Paragraph("\n\n\n\nTercia o tercias de dígitos identificadores asignadas\n\n\n\n "))
                             .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                         .SetFontSize(10);
                        table.AddCell(cell15);
                        Cell cell16 = new Cell(1, 1).Add(new Paragraph("\n\n\n\nCLABE Asignada al Cliente Indirecto\n\n\n\n"))
                             .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                         .SetFontSize(10);
                        table.AddCell(cell16);
                        Cell cell17 = new Cell(1, 1).Add(new Paragraph("\n\n\n\nFecha de asignación de CLABE\n\n\n\n"))
                             .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                         .SetFontSize(10);
                        table.AddCell(cell17);

                        table.AddCell(CreateCell("", 1, TextAlignment.CENTER, false)
                           .SetFont(arialFont)
                           .SetFontSize(8)); 
                        table.AddCell(CreateCell(Descripcion, 1, TextAlignment.CENTER, false)
                            .SetFont(arialFont)
                            .SetFontSize(8)); 
                        table.AddCell(CreateCell(Tercia, 1, TextAlignment.CENTER, false)
                            .SetFont(arialFont)
                            .SetFontSize(8)); 
                        table.AddCell(CreateCell(CLABE, 1, TextAlignment.CENTER, false)
                            .SetFont(arialFont)
                            .SetFontSize(8)); 
                        table.AddCell(CreateCell(Fecha, 1, TextAlignment.CENTER, false)
                            .SetFont(arialFont)
                            .SetFontSize(8)); 

                        Cell CreateCell(string text, int colspan, TextAlignment alignment, bool isHeader)
                        {
                            Cell cell = new Cell(1, colspan)
                                .Add(new Paragraph(text))
                                .SetTextAlignment(alignment)
                                .SetPadding(5)
                                .SetWidth(5f);
                            if (isHeader)
                            {
                                cell.SetBold();
                            }
                            return cell;
                        }


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
                        Paragraph paragraph5 = new Paragraph("Rafael Alberto Toscano Sánchez y Responsable de Cumplimiento Normativo SPEI.\n\n\n\n\n")
                            .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                            .SetFontSize(10);
                        document.Add(paragraph5);
                 

                        Paragraph paragraph7 = new Paragraph();

                        // Agregar el texto con tamaños de fuente especificados
                        paragraph7.Add(new Text("118 ").SetFont(arialFont).SetFontSize(5));
                        paragraph7.Add(new Text("Conforme a la estructura señalada en la sección “6.14 Estructura de la Clave Básica Estandarizada (CLABE)” del presente Manual, incluir los 3 dígitos que utilizarán los Participantes para identificar a los clientes de dicho Participante Indirecto para la conformación de CLABEs bajo el esquema de participación indirecta.\n").SetFont(arialFont).SetFontSize(7));

                       
                        paragraph7.SetTextAlignment(TextAlignment.JUSTIFIED)
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
                        document.Close();
                    }

                    // Convertimos el MemoryStream en un array de bytes
                    pdfBytes = ms.ToArray();
                }
            }

            return pdfBytes;
        }
    }
}
