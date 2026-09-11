using iRisCore.Models.Interfaces;
using iText.IO.Font.Constants;
using iText.IO.Image;
using iText.Kernel.Font;
using iText.Kernel.Pdf;
using iText.Layout;
using iText.Layout.Element;
using iText.Layout.Properties;
using Newtonsoft.Json;
using static iRisCore.Controllers.BO_FormularioController;

namespace iRisCore.Models.Formularios
{
    public class Formulario_1_1 : IFormulario
    {
      
        public string Id { get; } = "1_1"; // ID del formulario
     
        public byte[] GenerarPDF(MyDataModel registro, Dictionary<string, string> fields)

        {
            byte[] pdfBytes;
            PdfFont arialFont = PdfFontFactory.CreateFont(StandardFonts.HELVETICA);
            string campo1 = fields.ContainsKey("campo1") ? fields["campo1"] : string.Empty;
            string campo2 = fields.ContainsKey("campo2") ? fields["campo2"] : string.Empty;
            string campo3 = fields.ContainsKey("campo3") ? fields["campo3"] : string.Empty;
            string campo4 = fields.ContainsKey("campo4") ? fields["campo4"] : string.Empty;
            string campo5 = fields.ContainsKey("campo5") ? fields["campo5"] : string.Empty;
            string campo6 = fields.ContainsKey("campo6") ? fields["campo6"] : string.Empty;
            string campo7 = fields.ContainsKey("campo7") ? fields["campo7"] : string.Empty;
            string campo8 = fields.ContainsKey("campo8") ? fields["campo8"] : string.Empty;
            string campo9 = fields.ContainsKey("campo9") ? fields["campo9"] : string.Empty;
            string campo10 = fields.ContainsKey("campo10") ? fields["campo10"] : string.Empty;

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
                        //string cabeceraImagePath = "wwwroot/images/Bo_imagenes/asp.jpg";
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
                            .Add(" reporta a este Instituto Central la información de los clientes a los que les ofrecerá Servicios de Participación indirecta en el SPEI, para que estos últimos actúen con el carácter de Participantes Indirectos en términos de lo establecido en las Reglas del SPEI, contenidas en la Circular 14/2017, emitida por el Banco de México.\n\n");

                        // Agregar párrafos al documento
                        document.Add(paragraph1);
                        document.Add(paragraph2);
                        document.Add(paragraph8);


                 

                        #region Tabla
                        // Calcular el ancho de la página
                        float pageWidth = document.GetPdfDocument().GetDefaultPageSize().GetWidth();

                        // Calcular el ancho de la tabla
                        float tableWidth = pageWidth - document.GetLeftMargin() - document.GetRightMargin();
                        Table table = new Table(UnitValue.CreatePointArray(new float[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }));
                        table.SetWidth(tableWidth).SetMarginRight(50);

                        // Celda de encabezado que abarca 11 columnas (Participante SPEI)
                        Cell headerCell = new Cell(1, 11).Add(new Paragraph("Participante SPEI "));
                        headerCell.SetTextAlignment(TextAlignment.CENTER).SetFont(arialFont).SetFontSize(10);
                        table.AddCell(headerCell);

                        // Segunda fila con una celda que abarca 11 columnas (Clave SPEI)
                        Cell claveCell = new Cell(1, 11).Add(new Paragraph("90659 "));
                        claveCell.SetTextAlignment(TextAlignment.CENTER).SetFont(arialFont).SetFontSize(10);
                        table.AddCell(claveCell);

                        // Tercera fila dividida en tres celdas
                        // Primera celda tamaño 1 columna
                        table.AddCell("n");

                        // Segunda celda tamaño 4 columnas
                        table.AddCell(new Cell(1, 4)
                            .Add(new Paragraph("\nInformación del Contrato de Servicios de Participación Indirecta\n"))
                            .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                            .SetFontSize(8));

                        // Tercera celda tamaño 6 columnas
                        table.AddCell(new Cell(1, 6)
                            .Add(new Paragraph("\nInformación del Participante Indirecto\n"))
                            .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                            .SetFontSize(8));

                        // Agregar celdas con texto y tamaño de fuente especificados
                        table.AddCell("");
                        table.AddCell("\n\nFecha de celebración del contrato\n\n").SetFont(arialFont).SetFontSize(8);
                        table.AddCell("\n\nFecha de inicio de prestación de servicios\n\n").SetFont(arialFont).SetFontSize(8);
                        table.AddCell("\n\nFecha de término de prestación de servicios\n\n").SetFont(arialFont).SetFontSize(8);

                        // Celda con "texto 1"
                        Paragraph p1 = new Paragraph();
                        p1.Add(new Text("\n\nDuración del contrato ").SetFont(arialFont).SetFontSize(8));
                        p1.Add(new Text("114").SetFont(arialFont).SetFontSize(5));
                        table.AddCell(new Cell(1, 1).Add(p1).SetFont(arialFont));

                        // Celda con "texto 2"
                        Paragraph p2 = new Paragraph();
                        p2.Add(new Text("\n\nCLABE Asignada\n\n ").SetFont(arialFont).SetFontSize(8));
                        p2.Add(new Text("115").SetFont(arialFont).SetFontSize(5));
                        table.AddCell(new Cell(1, 1).Add(p2).SetFont(arialFont));

                        // Celda con "texto 3"
                        Paragraph p3 = new Paragraph();
                        p3.Add(new Text("\n\nDenominación o razón social\n\n ").SetFont(arialFont).SetFontSize(8));
                        p3.Add(new Text("").SetFont(arialFont).SetFontSize(5));
                        table.AddCell(new Cell(1, 1).Add(p3).SetFont(arialFont));

                        table.AddCell("\n\nNombre Comercial o marca\n\n").SetFont(arialFont).SetFontSize(8);
                        table.AddCell("\n\nRFC con homoclave\n\n").SetFont(arialFont).SetFontSize(8);

                        // Celda con "texto 4"
                        Paragraph p4 = new Paragraph();
                        p4.Add(new Text("\n\nTercia de dígitos identificadores  ").SetFont(arialFont).SetFontSize(8));
                        p4.Add(new Text("116").SetFont(arialFont).SetFontSize(5));
                        table.AddCell(new Cell(1, 1).Add(p4).SetFont(arialFont));

                        // Celda con "texto 5"
                        Paragraph p5 = new Paragraph();
                        p5.Add(new Text("\n\nProveedor de Servicios de Participación Indirecta").SetFont(arialFont).SetFontSize(8));
                        p5.Add(new Text("117").SetFont(arialFont).SetFontSize(5));
                        table.AddCell(new Cell(1, 1).Add(p5).SetFont(arialFont));


                        

                        table.AddCell(CreateCell("", 1, TextAlignment.CENTER, false).SetFont(arialFont).SetFontSize(8)); // Celda vacía para la columna "n"
                        table.AddCell(CreateCell(campo3, 1, TextAlignment.CENTER, false).SetFont(arialFont).SetFontSize(8)); // Fecha de celebración del contrato
                        table.AddCell(CreateCell(campo4, 1, TextAlignment.CENTER, false).SetFont(arialFont).SetFontSize(8)); // Fecha de inicio de prestación de servicios
                        table.AddCell(CreateCell(campo5, 1, TextAlignment.CENTER, false).SetFont(arialFont).SetFontSize(8)); // Fecha de término de prestación de servicios
                        table.AddCell(CreateCell(campo6, 1, TextAlignment.CENTER, false).SetFont(arialFont).SetFontSize(8)); // Duración del contrato
                        table.AddCell(CreateCell(campo7, 1, TextAlignment.CENTER, false).SetFont(arialFont).SetFontSize(8)); // CLABE Asignada
                        table.AddCell(CreateCell(campo1, 1, TextAlignment.CENTER, false).SetFont(arialFont).SetFontSize(7)); // Denominación o razón social
                        table.AddCell(CreateCell(campo8, 1, TextAlignment.CENTER, false).SetFont(arialFont).SetFontSize(7)); // Nombre Comercial o marca
                        table.AddCell(CreateCell(campo2, 1, TextAlignment.CENTER, false).SetFont(arialFont).SetFontSize(7)); // RFC con homoclave
                        table.AddCell(CreateCell(campo9, 1, TextAlignment.CENTER, false).SetFont(arialFont).SetFontSize(8)); // Tercia de dígitos identificadores
                        table.AddCell(CreateCell(campo10, 1, TextAlignment.CENTER, false).SetFont(arialFont).SetFontSize(8)); // Proveedor de Servicios de Participación Indirecta

                        document.Add(table);

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

                        Paragraph paragraph9 = new Paragraph(" \n\n\n\n\n ")
                           .SetTextAlignment(TextAlignment.CENTER)
                           .SetFont(arialFont)
                           .SetFontSize(10);
                        document.Add(paragraph9);
                        Paragraph paragraph7 = new Paragraph();

                        // Agregar el texto con tamaños de fuente especificados
                        paragraph7.Add(new Text("114 ").SetFont(arialFont).SetFontSize(5));
                        paragraph7.Add(new Text("La duración del contrato podrá ser indeterminada; en otros casos, colocar “NA” en el campo. Señalar la duración en días hábiles bancarios.\n").SetFont(arialFont).SetFontSize(7));

                        paragraph7.Add(new Text("115 ").SetFont(arialFont).SetFontSize(5));
                        paragraph7.Add(new Text("Identificador de cuenta asignado al Participante Indirecto, para realizar operaciones en nombre propio.\n").SetFont(arialFont).SetFontSize(7));

                        paragraph7.Add(new Text("116 ").SetFont(arialFont).SetFontSize(5));
                        paragraph7.Add(new Text("Corresponde a los tres dígitos que utilizaran los Participantes para identificar a los Participantes Indirectos conforme a la estructura señalada en la sección “6.14 Estructura de la Clave Básica Estandarizada (CLABE)” del presente Manual, que corresponda para la conformación de CLABEs bajo el esquema de participación indirecta.\n").SetFont(arialFont).SetFontSize(7));

                        paragraph7.Add(new Text("117 ").SetFont(arialFont).SetFontSize(5));
                        paragraph7.Add(new Text("Corresponde a la denominación o razón social de la entidad que en su caso preste algún servicio a un Participante Indirecto, necesario para la prestación de Servicios de Participación Indirecta. Como podrá ser la provisión de programas informáticos; infraestructura de tecnologías de la información y telecomunicaciones, en caso de que el Participante actúe como su propio proveedor, incluir “NA”.\n\n\n").SetFont(arialFont).SetFontSize(7));

                        // Configurar alineación, tamaño de fuente general, y otros estilos para el párrafo
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
