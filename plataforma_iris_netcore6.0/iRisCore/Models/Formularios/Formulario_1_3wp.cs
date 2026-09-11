using DocumentFormat.OpenXml;
using DocumentFormat.OpenXml.Packaging;
using DocumentFormat.OpenXml.Wordprocessing;
using System.IO;
using System.Globalization;

namespace iRisCore.Models.Formularios
{
    public class Formulario_1_3wp
    {
        public byte[] GeneraWordFormulario3(ContentFormulario3 participantes)
        {
            // Ruta del archivo existente.
            string filePath = "wwwroot/images/BO_imagenes/formulario.docx";

            // Creamos una copia del archivo original en un MemoryStream.
            using (MemoryStream memoryStream = new MemoryStream())
            {
                using (FileStream fileStream = new FileStream(filePath, FileMode.Open, FileAccess.Read))
                {
                    fileStream.CopyTo(memoryStream);
                }

                // Abrimos la copia del documento en el MemoryStream.
                using (WordprocessingDocument wordDocument = WordprocessingDocument.Open(memoryStream, true))
                {
                    MainDocumentPart mainPart = wordDocument.MainDocumentPart;
                    Body body = mainPart.Document.Body;


                    RunProperties runProperties = new RunProperties();
                    RunFonts runFonts = new RunFonts() { Ascii = "Calibri", HighAnsi = "Calibri" };
                    runProperties.Append(runFonts);


                    // Agregar el primer párrafo con la fecha formateada
                    DateTime currentDate = DateTime.Now;
                    string formattedDate = currentDate.ToString("dd 'de' MMMM 'de' yyyy", new CultureInfo("es-ES"));
                    Paragraph para1 = CreateParagraph("La Paz B.C.S., a " + formattedDate, JustificationValues.Right, 11);
                    body.AppendChild(para1);
                    // Agregar un salto de línea
                    para1.Append(new Break());

                    // Crear el segundo párrafo con saltos de línea y texto en negritas
                    Paragraph para2 = new Paragraph();

                    Run run1 = new Run();
                    RunProperties runProps1 = new RunProperties();
                    run1.Append(runProperties.CloneNode(true));
                    runProps1.Append(new Bold()); // Aplicar negritas
                    run1.Append(runProps1);


                    run1.Append(new Text("BANCO DE MÉXICO"));
                    para2.Append(run1);

                    // Agregar un salto de línea
                    para2.Append(new Break());

                    Run run2 = new Run();
                    RunProperties runProps2 = new RunProperties();
                    runProps2.Append(new Bold()); // Aplicar negritas
                    run2.Append(runProps2);
                    run2.Append(runProperties.CloneNode(true));


                    run2.Append(new Text("Dirección de Operación y Continuidad"));
                    para2.Append(run2);

                    para2.Append(new Break());
                 

                    Run run3 = new Run();

                     RunProperties runProps3 = new RunProperties();
                    runProps3.Append(new Bold()); // Aplicar negritas
                    run3.Append(runProps3);
                    run3.Append(runProperties.CloneNode(true));
                    run3.Append(new Text("de Sistemas de Pagos e Infraestructura de Mercados"));
                    para2.Append(run3);

                    para2.Append(new Break());

                    Run run4 = new Run();
                    run4.Append(runProperties.CloneNode(true));
                    run4.Append(new Text("Avenida 5 de Mayo número 6, piso 7,"));
                    para2.Append(run4);

                    para2.Append(new Break());

                    Run run5 = new Run();
                    run5.Append(runProperties.CloneNode(true));
                    run5.Append(new Text("Colonia Centro, Alcaldía Cuauhtémoc,"));
                    para2.Append(run5);

                    para2.Append(new Break());

                    Run run6 = new Run();
                    run6.Append(runProperties.CloneNode(true));
                    run6.Append(new Text("C.P. 06000, Ciudad de México."));
                    para2.Append(run6);
                 

                    // Agregar el párrafo al cuerpo del documento
                    body.AppendChild(para2);

                     para2.Append(new Break());
                    para2.Append(new Break());



                    // Crear el párrafo justificado
                    Paragraph para = new Paragraph();

                    // Configurar la justificación a Justified
                    para.Append(new ParagraphProperties());

                    // Crear la primera parte del texto
                    // Crear la primera parte del texto
                    Run run7 = new Run();
                    run7.Append(runProperties.CloneNode(true));
                    run7.Append(new Text("A través de la presente   "));  // Espacio al final para separar

                    // Crear la segunda parte del texto, en negritas y subrayado
                    Run run8 = new Run();
                    run8.Append(runProperties.CloneNode(true));
                    RunProperties runProperties1 = new RunProperties();
                    runProperties1.Append(new Bold());
                    runProperties1.Append(new Underline() { Val = UnderlineValues.Single });
                    run8.Append(runProperties1);
                    run8.Append(new Text(" Opciones Empresariales del Noreste, S.A. de C.V. S.F.P "));  // Espacio al final

                    // Crear la tercera parte del texto
                    Run run9 = new Run();
                    run9.Append(runProperties.CloneNode(true));
                    run9.Append(new Text("    reporta a este Instituto Central la información de los conjuntos CLABE que han sido dados de baja en sustento de los Servicios de Participación Indirecta que este Participante del SPEI ofrece. Lo anterior, conforme a la regulación aplicable para el esquema de participación indirecta detallado en las Reglas del SPEI, contenidas en la Circular 14/2017, emitidas por el Banco de México. "));

                    // Agregar los tres fragmentos al párrafo
                    para.Append(run7);
                    para.Append(run8);
                    para.Append(run9);

                    para.Append(new Break());
                    para.Append(new Break());

                    // Agregar el párrafo al cuerpo del documento
                    body.AppendChild(para);

                    Table table = new Table();

                    // Propiedades de la tabla (bordes)
                    TableProperties tblProperties = new TableProperties(
                        new TableBorders(
                            new TopBorder() { Val = BorderValues.Single, Size = 4 },
                            new BottomBorder() { Val = BorderValues.Single, Size = 4 },
                            new LeftBorder() { Val = BorderValues.Single, Size = 4 },
                            new RightBorder() { Val = BorderValues.Single, Size = 4 },
                            new InsideHorizontalBorder() { Val = BorderValues.Single, Size = 4 },
                            new InsideVerticalBorder() { Val = BorderValues.Single, Size = 4 }
                        )
                    );
                    table.AppendChild(tblProperties);

                    // Primera fila con el encabezado principal de la tabla
                    TableRow headerRow1 = new TableRow();
                    headerRow1.Append(CreateTableCell("Opciones Empresariales del Noreste, S.A. de C.V. S.F.P", true, "10800", 6));
                    table.AppendChild(headerRow1);

                    // Segunda fila: número "90659"
                    TableRow headerRow2 = new TableRow();
                    headerRow2.Append(CreateTableCell("90659", false, "10800", 6));
                    table.AppendChild(headerRow2);

                    // Tercera fila: encabezados
                    TableRow rowEncabezados = new TableRow();
                    rowEncabezados.Append(CreateTableCell("#", true, "1200", 1));
                    rowEncabezados.Append(CreateTableCell("Participante Indirecto", true, "2400", 3));
                    rowEncabezados.Append(CreateTableCell("Información de los Clientes Indirectos", true, "4800", 2));
                    table.AppendChild(rowEncabezados);

                    // Cuarta fila: Subencabezados
                    TableRow subEncabezados = new TableRow();
                    subEncabezados.Append(CreateTableCell("", true, "2400", 1));
                    subEncabezados.Append(CreateTableCell("Denominación o razón social", true, "2400", 2));
                    subEncabezados.Append(CreateTableCell("Tercia o tercias de dígitos identificadores asignadas 119", true, "2400", 1));
                    subEncabezados.Append(CreateTableCell("CLABE asignada al Cliente Indirecto", true, "2400", 1));
                    subEncabezados.Append(CreateTableCell("Fecha de baja de CLABE", true, "2400", 1));
                    table.AppendChild(subEncabezados);

                    // Agregar filas con los datos de los participantes
                    int rowNumber = 1;
                    if (participantes.ListadoCA != null && participantes.ListadoCA.Count > 0)
                    {
                        // Iteramos sobre los elementos en ListadoCA
                        foreach (var participante in participantes.ListadoCA)
                        {
                            TableRow dataRow = new TableRow();
                            dataRow.Append(CreateTableCell(rowNumber.ToString(), false, "1200", 1));
                            dataRow.Append(CreateTableCell(participante.RazonSocial, false, "2400", 2));

                            // Texto resaltado en rojo
                            TableCell cellResaltada = CreateTableCell(participante.Tercia.ToString(), false, "2400", 1);
                            dataRow.Append(cellResaltada);

                            dataRow.Append(CreateTableCell(participante.ClabeClienteIndirecto, false, "2400", 1));
                            dataRow.Append(CreateTableCell(participante.fechaBajaClabe.ToString("yyyy/MM/dd"), false, "2400", 1));

                            table.AppendChild(dataRow);
                            rowNumber++;
                        }
                    }
                    else
                    {
                        // Manejar el caso en que no hay datos en ListadoCA
                        throw new Exception("No hay datos disponibles en ListadoCA.");
                    }

                    // Agregar la tabla al cuerpo del documento
                    body.AppendChild(table);


                    // Crear el texto de "Atentamente" centrado en la hoja
                    Paragraph paragraphAtentamente = new Paragraph();
                    Run runAtentamente = new Run(new Text("A t e n t a m e n t e,"));
                   
                    RunProperties runPropsAtentamente = new RunProperties(new RunFonts() { Ascii = "Calibri" }, new FontSize() { Val = "22" }); // Tamaño 11 (22 es el valor en medio punto)
                    runAtentamente.PrependChild(runPropsAtentamente);
                    paragraphAtentamente.Append(new ParagraphProperties(new Justification() { Val = JustificationValues.Center }));
                    paragraphAtentamente.Append(runAtentamente);
               
                    Paragraph paragraphEmpresa = new Paragraph();
                    Run runEmpresa = new Run(new Text("Opciones Empresariales del Noreste, S.A. de C.V. S.F.P"));
                    RunProperties runPropsEmpresa = new RunProperties(new RunFonts() { Ascii = "Calibri" }, new FontSize() { Val = "22" }, new Bold(), new Underline() { Val = UnderlineValues.Single }); // Tamaño 11 y negritas
                    runEmpresa.PrependChild(runPropsEmpresa);
                    paragraphEmpresa.Append(new ParagraphProperties(new Justification() { Val = JustificationValues.Center }));
                    paragraphEmpresa.Append(runEmpresa);
                    Paragraph breakParagraph3 = new Paragraph(new Run(new Break()));
                    body.Append(breakParagraph3);


                    // Crear el texto de la firma electrónica centrado
                    Paragraph paragraphFirmaElectronica = new Paragraph();
                    Run runFirmaElectronica = new Run(new Text(""));
                    RunProperties runPropsFirmaElectronica = new RunProperties(new RunFonts() { Ascii = "Calibri" }, new FontSize() { Val = "22" }); // Tamaño 11
                    runFirmaElectronica.PrependChild(runPropsFirmaElectronica);
                    paragraphFirmaElectronica.Append(new ParagraphProperties(new Justification() { Val = JustificationValues.Center }));
                    paragraphFirmaElectronica.Append(runFirmaElectronica);



                    // Crear el texto del título centrado
                    Paragraph paragraphFirma = new Paragraph();
                    Run runFirma1 = new Run(new Text("[FIRMA ELECTRÓNICA]"));
                    RunProperties runFirma11 = new RunProperties(new RunFonts() { Ascii = "Calibri" }, new FontSize() { Val = "22" }); // Tamaño 11
                    runFirma1.PrependChild(runFirma11);
                    paragraphFirma.Append(new ParagraphProperties(new Justification() { Val = JustificationValues.Center }));
                    paragraphFirma.Append(runFirma1);

                    // Crear el texto del nombre centrado
                    Paragraph paragraphNombre = new Paragraph();
                    Run runNombre = new Run(new Text(participantes.NombreFirmante));
                    RunProperties runPropsNombre = new RunProperties(new RunFonts() { Ascii = "Calibri" }, new FontSize() { Val = "22" }, new Bold(), new Underline() { Val = UnderlineValues.Single }); // Tamaño 11 y negritas
                    runNombre.PrependChild(runPropsNombre);
                    paragraphNombre.Append(new ParagraphProperties(new Justification() { Val = JustificationValues.Center }));
                    paragraphNombre.Append(runNombre);

                    // Crear el texto del título centrado
                    Paragraph paragraphTitulo = new Paragraph();
                    Run runTitulo = new Run(new Text(participantes.CargoPuesto));
                    RunProperties runPropsTitulo = new RunProperties(new RunFonts() { Ascii = "Calibri" }, new FontSize() { Val = "22" }); // Tamaño 11
                    runTitulo.PrependChild(runPropsTitulo);
                    paragraphTitulo.Append(new ParagraphProperties(new Justification() { Val = JustificationValues.Center }));
                    paragraphTitulo.Append(runTitulo);

                    // Agregar los párrafos al cuerpo del documento
                    body.Append(paragraphAtentamente);
                    body.Append(paragraphEmpresa);
                    body.Append(paragraphFirmaElectronica);

                    body.Append(paragraphFirma);
                    body.Append(paragraphNombre);
                    body.Append(paragraphTitulo);


                    Paragraph breakParagraph = new Paragraph(new Run(new Break()));
                    body.Append(breakParagraph);
                
                    // Crear el primer párrafo alineado a la izquierda
                    Paragraph paragraph11 = new Paragraph();
                    Run run11 = new Run(new Text("119: conforme a la estructura señalada en la sección “6.14 Estructura de la Clave Básica Estandarizada (clabe)” del Manual SPEI, incluir los 3 dígitos que utilizarán los participantes para identificar a los clientes de dicho Participante Indirecto para la conformación de CLABEs bajo el esquema de participación indirecta.\r\n "));
                    RunProperties runProps11 = new RunProperties(new RunFonts() { Ascii = "Calibri" }, new FontSize() { Val = "22" }); // Tamaño 11
                    run11.PrependChild(runProps11);
                    paragraph11.Append(new ParagraphProperties(new Justification() { Val = JustificationValues.Left }));
                    paragraph11.Append(run11);

                   

                    // Agregar los párrafos al cuerpo del documento
                    body.Append(paragraph11);
                 
                   


                    mainPart.Document.Save();
                }
           
                // Devolvemos el MemoryStream como un arreglo de bytes.
                return memoryStream.ToArray();
            }
        }


        // Método auxiliar para crear una celda de tabla
        private TableCell CreateTableCell(string text, bool isBold, string width, int colSpan = 1, bool highlight = false)
        {
            TableCell cell = new TableCell();
            TableCellProperties props = new TableCellProperties();
            props.Append(new TableCellWidth() { Width = width, Type = TableWidthUnitValues.Dxa });
            if (colSpan > 1)
            {
                props.Append(new GridSpan() { Val = colSpan });
            }
            cell.Append(props);

            RunProperties runProps = new RunProperties(new RunFonts() { Ascii = "Calibri" }, new FontSize() { Val = "22" });
          
            if (highlight)
            {
                runProps.Append(new Highlight() { Val = HighlightColorValues.Red });
            }

            Paragraph paragraph = new Paragraph(new Run(runProps, new Text(text)));
            paragraph.ParagraphProperties = new ParagraphProperties(new Justification() { Val = JustificationValues.Center });
            cell.Append(paragraph);

            return cell;
        }

        // Método para crear un párrafo con el texto, justificación y tamaño de fuente especificados.
        private Paragraph CreateParagraph(string text, JustificationValues justification, int fontSize)
        {
            Paragraph paragraph = new Paragraph();

            Run run = new Run();
            RunProperties runProperties = new RunProperties();
            runProperties.FontSize = new FontSize() { Val = (fontSize * 2).ToString() }; // OpenXML usa half-points
            run.Append(runProperties);
            run.Append(new Text(text));

            ParagraphProperties paragraphProperties = new ParagraphProperties();
            paragraphProperties.Justification = new Justification() { Val = justification };

            paragraph.Append(paragraphProperties);
            paragraph.Append(run);

            return paragraph;
        }
    }
}
