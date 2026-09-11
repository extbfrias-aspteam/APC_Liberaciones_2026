
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
    public class Formulario_1_3p
    {
        public byte[] GeneraPdfFormulario3(ContentFormulario3 participantes)
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
                        Text textoCursiva = new Text("Opciones Empresariales del Noreste, S.A. de C.V. S.F.P")
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
                            .Add(" reporta a este Instituto Central la información de los conjuntos CLABE que han sido dados de baja en sustento de los Servicios de Participación Indirecta que este Participante del SPEI ofrece. Lo anterior, conforme a la regulación aplicable para el esquema de participación indirecta detallado en las Reglas del SPEI, contenidas en la Circular 14/2017, emitidas por el Banco de México.\r\n \r\n\n\n");

                        document.Add(paragraph1);
                        document.Add(paragraph2);
                        document.Add(paragraph8);


                        #region Tabla



                        // Define el tamaño de fuente global
                        float fontSize = 11;
                        float fontSize8 = 8;

                        float[] columnWidths = new float[] { 1, 6, 6, 6, 6 };  // Ajuste de las columnas según la estructura de la tabla

                        Table table = new Table(UnitValue.CreatePercentArray(columnWidths))
                            .SetBorder(new SolidBorder(1))
                            .SetWidth(UnitValue.CreatePercentValue(100))
                            .SetFixedPosition(50, 250, 500);

                        // Primera fila: Encabezado de la tabla que abarca todas las columnas
                        Cell headerCell = new Cell(1, 6)
                            .Add(new Paragraph(textoCursiva))
                            .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                            .SetFontSize(fontSize)
                            .SetBold()
                            .SetPadding(5);
                        table.AddCell(headerCell);

                        // Segunda fila: Número 90659 centrado
                        Cell numberCell = new Cell(1, 6)
                            .Add(new Paragraph("90659"))
                            .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                            .SetFontSize(fontSize);
                        table.AddCell(numberCell);

                        // Tercera fila: Encabezados "Participante Indirecto" y "Información de los Clientes Indirectos"
                        table.AddCell(CreateCell("#", 1, TextAlignment.CENTER, true, arialFont, fontSize));
                        table.AddCell(CreateCell("Participante Indirecto", 2, TextAlignment.CENTER, true, arialFont, fontSize));
                        table.AddCell(CreateCell("Información de los Clientes Indirectos", 2, TextAlignment.CENTER, true, arialFont, fontSize));

                        // Cuarta fila: Subencabezados
                        table.AddCell(CreateCell("#", 1, TextAlignment.CENTER, true, arialFont, fontSize));
                        table.AddCell(CreateCell("Denominación o razón social", 1, TextAlignment.LEFT, true, arialFont, fontSize));
                        table.AddCell(CreateCell("Tercia o tercias de\n  dígitos\n identificadores \n asignadas 119", 1, TextAlignment.LEFT, true, arialFont, fontSize));
                        table.AddCell(CreateCell("CLABE \n asignada al \n   Cliente\n  Indirecto", 1, TextAlignment.LEFT, true, arialFont, fontSize));
                        table.AddCell(CreateCell("Fecha de baja \n  de CLABE", 1, TextAlignment.LEFT, true, arialFont, fontSize));

                        // Itera sobre el arreglo de participantes y agrega las filas dinámicamente
                        int rowNumber = 1;
                        if (participantes.ListadoCA != null && participantes.ListadoCA.Count > 0)
                        {
                            foreach (var participante in participantes.ListadoCA)
                            {
                                table.AddCell(CreateCell(rowNumber.ToString(), 1, TextAlignment.CENTER, false, arialFont, fontSize8));
                                table.AddCell(CreateCell(participante.RazonSocial, 1, TextAlignment.CENTER, false, arialFont, fontSize8));

                          
                                table.AddCell(CreateCell(participante.Tercia.ToString(), 1, TextAlignment.CENTER, false, arialFont, fontSize8));
                                table.AddCell(CreateCell(participante.ClabeClienteIndirecto, 1, TextAlignment.CENTER, false, arialFont, fontSize8));

                                // Convierte FechaAsignacionClabe a string con formato
                                table.AddCell(CreateCell(participante.fechaBajaClabe.ToString("yyyy/MM/dd"), 1, TextAlignment.CENTER, false, arialFont, fontSize8));

                                rowNumber++;  // Incrementa el contador de filas
                            }
                        }

                        // Agregar la tabla al documento
                        document.Add(table);
                        // Método para crear celdas



                        #endregion

                        Paragraph paragraph3 = new Paragraph("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nA t e n t a m e n t e,")
                            .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                            .SetFontSize(11);
                        document.Add(paragraph3);
                        Paragraph paragraph4 = new Paragraph(textoCursiva)
                            .SetTextAlignment(TextAlignment.CENTER)
                            .SetFont(arialFont)
                             .SetBold()
                            .SetFontSize(11)
                             .SetUnderline(0.5f, -1.5f);
                        document.Add(paragraph4);


                        Paragraph paragraph19 = new Paragraph("[FIRMA ELECTRÓNICA]")
                           .SetTextAlignment(TextAlignment.CENTER)
                          .SetFont(arialFont)
                            .SetFontSize(10);
                        document.Add(paragraph19);

                        Paragraph paragraph5 = new Paragraph(participantes.NombreFirmante)
                         .SetTextAlignment(TextAlignment.CENTER)
                        .SetFont(arialFont)
                         .SetFontSize(10)
                        .SetBold()
                        .SetUnderline(0.5f, -1.5f);

                        document.Add(paragraph5);





                        Paragraph paragraph9 = new Paragraph(participantes.CargoPuesto)
                           .SetTextAlignment(TextAlignment.CENTER)
                          .SetFont(arialFont)
                            .SetFontSize(10);
                        document.Add(paragraph9);


                        Paragraph paragraph7 = new Paragraph("\r\n\n\n\n\n119: conforme a la estructura señalada en la sección “6.14 Estructura de la Clave Básica Estandarizada (clabe)” del Manual SPEI, incluir los 3 dígitos que utilizarán los participantes para identificar a los clientes de dicho Participante Indirecto para la conformación de CLABEs bajo el esquema de participación indirecta.\r\n")
                            .SetFontSize(11)
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
                .SetPadding(0);  // Reducir el padding


            Cell cell = new Cell(1, colspan)
                .Add(paragraph)
                .SetVerticalAlignment(VerticalAlignment.MIDDLE)
                .SetBorder(new SolidBorder(1));

            return cell;
        }

    }
}
