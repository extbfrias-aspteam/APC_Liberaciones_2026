
using iText.Kernel.Pdf;
using iText.Layout;
using iText.Kernel.Utils;
using iText.Kernel.Events;
using iText.Kernel.Pdf.Canvas;
using iText.Kernel.Pdf.Xobject;
using iText.IO.Font;
using System;
using System.IO;
using iText.Kernel.Font;
using iText.Layout.Element;
using iText.IO.Image;
using iText.Layout.Properties;
using iText.Layout.Borders;

namespace iRisCore.Models.Formularios
{
    public class Formulario_1_1p
    {
        public byte[] GeneraPdfFormulario1(FormlarioParticpanteModal participante)
        {
            string pdfFilePath = "wwwroot/images/BO_imagenes/FormularioPdf2.pdf";
            string calibriFontPath = Path.Combine("wwwroot", "fonts", "calibri.ttf");
            PdfFont arialFont = PdfFontFactory.CreateFont(calibriFontPath, PdfEncodings.IDENTITY_H);

            using (MemoryStream outputPdfStream = new MemoryStream())
            {
                using (PdfWriter writer = new PdfWriter(outputPdfStream))
                {
                    using (PdfDocument outputPdf = new PdfDocument(writer))
                    {
                        // Establecer tamaño carta para todas las páginas del documento
                        outputPdf.SetDefaultPageSize(iText.Kernel.Geom.PageSize.LETTER);

                        // Abre el PDF de origen y crea un objeto FormXObject de la primera página
                        using (PdfDocument sourcePdf = new PdfDocument(new PdfReader(pdfFilePath)))
                        {
                            PdfPage firstPage = sourcePdf.GetPage(1);
                            PdfFormXObject formXObject = firstPage.CopyAsFormXObject(outputPdf);

                            // Agregar manejador de eventos para repetir contenido
                            outputPdf.AddEventHandler(PdfDocumentEvent.END_PAGE, new RepeatedContentEventHandler(formXObject));
                        }

                        Document document = new Document(outputPdf);
                        document.SetMargins(72, 72, 72, 72);

                        DateTime currentDate = DateTime.Now;
                        string formattedDate = currentDate.ToString("dd 'de' MMMM 'de' yyyy");

                       

                        Paragraph paragraph1 = new Paragraph("La Paz B.C.S., a " + formattedDate)
                            .SetTextAlignment(TextAlignment.RIGHT)
                            .SetFontSize(11)
                            .SetFont(arialFont);

                        Paragraph paragraph2 = new Paragraph()
                            .SetTextAlignment(TextAlignment.LEFT)
                            .SetFontSize(11)
                            .SetFont(arialFont)
                            .SetMultipliedLeading(1.0f)
                            .Add(new Text("BANCO DE MÉXICO\nDirección de Operación y Continuidad\n" +
                                          "de Sistemas de Pagos e Infraestructura de Mercados\n")
                                 .SetBold())
                            .Add("Avenida 5 de Mayo número 6, piso 7,\n" +
                                 "Colonia Centro, Alcaldía Cuauhtémoc,\n" +
                                 "C.P. 06000, Ciudad de México\n\n");

                        // Crear el texto en negritas
                        Text textoCursiva = new Text("OPCIONES EMPRESARIALES DEL NORESTE SA DE CV SFP")
                            .SetBold()                   // Aplicar negritas
                            .SetFont(arialFont)           // Aplicar fuente
                            .SetFontSize(11);             // Definir el tamaño de la fuente

                        // Aplicar el subrayado
                        textoCursiva.SetTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE);

                        // Dibujar una línea debajo del texto para simular el subrayado
                        float underlineThickness = 0.75f;   // Espesor de la línea de subrayado
                        float underlineYPosition = -1.5f;   // Posición de la línea debajo del texto
                        textoCursiva.SetUnderline(underlineThickness, underlineYPosition);

                        Paragraph paragraph8 = new Paragraph()
                            .SetTextAlignment(TextAlignment.JUSTIFIED)
                            .SetFontSize(11)
                            .SetFont(arialFont)
                            .SetMultipliedLeading(1.0f)
                            .Add("A través de la presente ")
                            .Add(textoCursiva)
                            .Add(" reporta a este Instituto Central la información de los clientes a los que les ofrecerá Servicios de Participación indirecta en el SPEI, para que estos últimos actúen con el carácter de Participantes Indirectos en términos de lo establecido en las Reglas del SPEI, contenidas en la Circular 14/2017, emitida por el Banco de México.\n\n");

                        document.Add(paragraph1);
                        document.Add(paragraph2);
                        document.Add(paragraph8);


                        #region Tabla

                        // Define el tamaño de fuente global
                        float fontSize = 10;
                        float fontSize8 = 8;

                        // Ejemplo para ajustar el ancho de las columnas
                        float[] columnWidths = new float[] { 2, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4 };
                        Table table = new Table(UnitValue.CreatePercentArray(columnWidths))
                             .SetBorder(new SolidBorder(1))
                            .SetWidth(UnitValue.CreatePercentValue(50))
                            .SetFixedPosition(10, 250, 500);




                        // Crea la celda del encabezado con el tamaño de fuente ajustado
                        Cell headerCell = new Cell(1, 11)
                            .Add(new Paragraph("Opciones Empresariales del Noreste, S.A. de C.V. S.F.P"))
                            .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                            .SetFontSize(fontSize)
                            .SetBold()
                            .SetPadding(5);
                        table.AddCell(headerCell);

                        // Crea la celda clave con el tamaño de fuente ajustado
                        Cell claveCell = new Cell(1, 11)
                            .Add(new Paragraph("90659"))
                            .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                            .SetFontSize(fontSize)
                            .SetPadding(5);
                        table.AddCell(claveCell);

                        // Agrega celdas con el tamaño de fuente ajustado usando el método CreateCell
                        table.AddCell(CreateCell("n", 1, TextAlignment.CENTER, true, arialFont, fontSize));
                        table.AddCell(CreateCell("\nInformación del Contrato de Servicios de Participación Indirecta\n", 4, TextAlignment.CENTER, false, arialFont, fontSize));
                        table.AddCell(CreateCell("\nInformación del Participante Indirecto\n", 6, TextAlignment.CENTER, false, arialFont, fontSize));

                        // Agrega celdas con el tamaño de fuente ajustado
                        table.AddCell(CreateCell("", 1, TextAlignment.CENTER, false, arialFont, fontSize));
                        table.AddCell(CreateCell("\n\nFecha de celebración del contrato\n\n", 1, TextAlignment.CENTER, false, arialFont, fontSize));
                        table.AddCell(CreateCell("\n\nFecha de inicio de prestación de servicios\n\n", 1, TextAlignment.CENTER, false, arialFont, fontSize));
                        table.AddCell(CreateCell("\n\nFecha de término de prestación de servicios\n\n", 1, TextAlignment.CENTER, false, arialFont, fontSize));
                        table.AddCell(CreateCell("\n\nDuración del contrato\n\n", 1, TextAlignment.CENTER, false, arialFont, fontSize));
                        table.AddCell(CreateCell("\n\nCLABE Asignada\n\n", 1, TextAlignment.CENTER, false, arialFont, fontSize));
                        table.AddCell(CreateCell("\n\nDenominación o razón social\n\n", 1, TextAlignment.CENTER, false, arialFont, fontSize));
                        table.AddCell(CreateCell("\n\nNombre Comercial o marca\n\n", 1, TextAlignment.CENTER, false, arialFont, fontSize));
                        table.AddCell(CreateCell("\n\nRFC con homoclave\n\n", 1, TextAlignment.CENTER, false, arialFont, fontSize));
                        table.AddCell(CreateCell("\n\nTercia de dígitos identificadores\n\n", 1, TextAlignment.CENTER, false, arialFont, fontSize));
                        table.AddCell(CreateCell("\n\nProveedor de Servicios de Participación Indirecta\n\n", 1, TextAlignment.CENTER, false, arialFont, fontSize));

                        // Agrega celdas con el tamaño de fuente ajustado


                        string fechaCelebracionFormateada;
                        if (DateTime.TryParseExact(
                                participante.Content[0].fechaCelebracion,
                                "yyyy-MM-dd HH:mm:ss",
                                null,
                                System.Globalization.DateTimeStyles.None,
                                out DateTime fecha))
                        {
                            fechaCelebracionFormateada = fecha.ToString("yyyy/MM/dd");
                        }
                        else
                        {
                            fechaCelebracionFormateada = "Fecha inválida"; // Mensaje en caso de error
                        }


                        string fechaCelebracionFormateada2;
                        if (DateTime.TryParseExact(
                                participante.Content[0].inicioServicio,
                                "yyyy-MM-dd HH:mm:ss",
                                null,
                                System.Globalization.DateTimeStyles.None,
                                out DateTime fecha2))
                        {
                            fechaCelebracionFormateada2 = fecha2.ToString("yyyy/MM/dd");
                        }
                        else
                        {
                            fechaCelebracionFormateada2 = "Fecha inválida"; // Mensaje en caso de error
                        }


                        string fechaCelebracionFormateada3;
                        if (DateTime.TryParseExact(
                                participante.Content[0].terminoServicio,
                                "yyyy-MM-dd HH:mm:ss",
                                null,
                                System.Globalization.DateTimeStyles.None,
                                out DateTime fecha3))
                        {
                            fechaCelebracionFormateada3 = fecha3.ToString("yyyy/MM/dd");
                        }
                        else
                        {
                            fechaCelebracionFormateada3 = "Fecha inválida"; // Mensaje en caso de error
                        }
                        table.AddCell(CreateCell("", 1, TextAlignment.CENTER, false, arialFont, fontSize));
                        table.AddCell(CreateCell(fechaCelebracionFormateada, 1, TextAlignment.CENTER, false, arialFont, fontSize8));
                        table.AddCell(CreateCell(fechaCelebracionFormateada2, 1, TextAlignment.CENTER, false, arialFont, fontSize8));
                        table.AddCell(CreateCell(fechaCelebracionFormateada3, 1, TextAlignment.CENTER, false, arialFont, fontSize8));
                        table.AddCell(CreateCell(participante.Content[0].duracionContrato, 1, TextAlignment.CENTER, false, arialFont, fontSize8));
                        table.AddCell(CreateCell(participante.Content[0].clabe, 1, TextAlignment.CENTER, false, arialFont, fontSize8));
                        table.AddCell(CreateCell(participante.Content[0].razonSocial, 1, TextAlignment.CENTER, false, arialFont, fontSize8));
                        table.AddCell(CreateCell(participante.Content[0].nomComerciaMarca, 1, TextAlignment.CENTER, false, arialFont, fontSize8));
                        table.AddCell(CreateCell(participante.Content[0].rfc, 1, TextAlignment.CENTER, false, arialFont, fontSize8));
                        table.AddCell(CreateCell(participante.Content[0].tercia, 1, TextAlignment.CENTER, false, arialFont, fontSize8));
                        table.AddCell(CreateCell(participante.Content[0].proveedor, 1, TextAlignment.CENTER, false, arialFont, fontSize8));

                        document.Add(table);


                        #endregion

                        Paragraph paragraph3 = new Paragraph("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nA t e n t a m e n t e,")
                            .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                            .SetFontSize(10);
                        document.Add(paragraph3);
                        Paragraph paragraph4 = new Paragraph("OPCIONES EMPRESARIALES DEL NORESTE SA DE CV SFP")
                            .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                             .SetBold()
                            .SetFontSize(10)
                             .SetUnderline(0.5f, -1.5f);
                        document.Add(paragraph4);


                        Paragraph paragraph19 = new Paragraph("[FIRMA ELECTRÓNICA]")
                    .SetTextAlignment(TextAlignment.CENTER)
                   .SetFont(arialFont)
                     .SetFontSize(10);
                        document.Add(paragraph19);


                        Paragraph paragraph5 = new Paragraph(participante.Content[0].nombreFirmante)
                            .SetTextAlignment(TextAlignment.CENTER)  
                            .SetFont(arialFont)                      
                            .SetFontSize(10)   
                            .SetBold()
                            .SetUnderline(0.5f, -1.5f);             

                        document.Add(paragraph5);




                        Paragraph paragraph9 = new Paragraph( participante.Content[0].cargoPuesto)
                            .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                            .SetFontSize(10);
                        document.Add(paragraph9);


                        Paragraph paragraph7 = new Paragraph("\r\n\n\n\n\n114 La duración del contrato podrá ser indeterminada; en otros casos, colocar “NA” en el campo. Señalar la duración en días hábiles bancarios.\r\n115 Identificador de cuenta asignado al Participante Indirecto, para realizar operaciones en nombre propio.\r\n116 Corresponde a los tres dígitos que utilizaran los Participantes para identificar a los Participantes Indirectos conforme a la estructura señalada en la sección “6.14 Estructura de la Clave Básica Estandarizada (CLABE)” del presente Manual, que corresponda para la conformación de CLABEs bajo el esquema de participación indirecta.\r\n117 Corresponde a la denominación o razón social de la entidad que en su caso preste algún servicio a un Participante Indirecto, necesario para la prestación de Servicios de Participación Indirecta. Como podrá ser la provisión de programas informáticos; infraestructura de tecnologías de la información y telecomunicaciones, en caso de que el Participante actúe como su propio proveedor, incluir “NA”.\r\n\r\n\n\n\n")
                            .SetTextAlignment(TextAlignment.JUSTIFIED)
                            .SetFontSize(10)
                            .SetFont(arialFont)
                            .SetMultipliedLeading(1.0f)
                            .SetMarginLeft(36);
                        document.Add(paragraph7);


                        document.Close();
                    }
                }

                return outputPdfStream.ToArray();
            }
        }


        private class RepeatedContentEventHandler : IEventHandler
        {
            private readonly PdfFormXObject formXObject;

            public RepeatedContentEventHandler(PdfFormXObject formXObject)
            {
                this.formXObject = formXObject;
            }

            public void HandleEvent(Event currentEvent)
            {
                PdfDocumentEvent pdfDocEvent = (PdfDocumentEvent)currentEvent;
                PdfPage page = pdfDocEvent.GetPage();
                PdfCanvas canvas = new PdfCanvas(page.NewContentStreamBefore(), page.GetResources(), pdfDocEvent.GetDocument());
                canvas.AddXObjectAt(formXObject, 0, 0);
            }
        }

        private Cell CreateCell(string content, int colspan, TextAlignment alignment, bool bold, PdfFont font, float fontSize)
        {
            Paragraph paragraph = new Paragraph(content)
                .SetFont(font)
                .SetFontSize(fontSize)
                .SetTextAlignment(alignment)
                .SetPadding(0) // Reducir el padding
                .SetFixedLeading(12);

            if (bold)
            {
                paragraph.SetBold();
            }

            Cell cell = new Cell(1, colspan)
                .Add(paragraph)
                .SetVerticalAlignment(VerticalAlignment.MIDDLE)
                .SetBorder(new SolidBorder(1))
                .SetWidth(UnitValue.CreatePercentValue(100f / colspan));


            return cell;
        }

    }
}
