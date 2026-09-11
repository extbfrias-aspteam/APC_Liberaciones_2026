using DocumentFormat.OpenXml;
using DocumentFormat.OpenXml.Packaging;
using DocumentFormat.OpenXml.Wordprocessing;
using System.IO;
using System.Globalization;

namespace iRisCore.Models.Formularios
{
    public class Formulario_1_1wp
    {
        public byte[] GeneraWordFormulario1(FormlarioParticpanteModal participante)
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
                    run9.Append(new Text("    reporta a este Instituto Central la información de los Clientes a los que les ofrecerá Servicios de Participación indirecta en el SPEI, para que éstos últimos actúen con el carácter de Participantes Indirectos en términos de lo establecido en las Reglas del SPEI, contenidas en la Circular 14/2017, emitidas por Banco de México."));

                    // Agregar los tres fragmentos al párrafo
                    para.Append(run7);
                    para.Append(run8);
                    para.Append(run9);

                    para.Append(new Break());
                    para.Append(new Break());

                    // Agregar el párrafo al cuerpo del documento
                    body.AppendChild(para);


                    // Crear una nueva tabla
                    Table table = new Table();

                    // Configurar las propiedades de la tabla con márgenes y ancho
                    TableProperties tableProperties = new TableProperties(
                        new TableBorders(
                            new TopBorder() { Val = new EnumValue<BorderValues>(BorderValues.Single), Size = 8 },
                            new BottomBorder() { Val = new EnumValue<BorderValues>(BorderValues.Single), Size = 8 },
                            new LeftBorder() { Val = new EnumValue<BorderValues>(BorderValues.Single), Size = 8 },
                            new RightBorder() { Val = new EnumValue<BorderValues>(BorderValues.Single), Size = 8 },
                            new InsideHorizontalBorder() { Val = new EnumValue<BorderValues>(BorderValues.Single), Size = 8 },
                            new InsideVerticalBorder() { Val = new EnumValue<BorderValues>(BorderValues.Single), Size = 8 }
                        ),
                        new TableWidth() { Width = "100%", Type = TableWidthUnitValues.Pct },  // Establecer ancho al 100%
                        new TableCellMarginDefault(
                            new TableCellLeftMargin() { Width = 0, Type = TableWidthValues.Dxa },
                            new TableCellRightMargin() { Width = 0, Type = TableWidthValues.Dxa }
                        )
                    );

                    // Establecer los márgenes de la tabla
                    TableLook tableLook = new TableLook() { FirstRow = true, LastRow = false, NoHorizontalBand = true, NoVerticalBand = true };
                    tableProperties.AppendChild(tableLook);

                    table.AppendChild(tableProperties);

                    // Crear la primera fila (Encabezado grande)
                    TableRow row1 = new TableRow();

                    TableCell cell1_1 = CreateTableCell("Opciones Empresariales del Noreste, S.A. de C.V. S.F.P", 11, true, true, 11);
                    TableCellProperties cell1_1Props = new TableCellProperties(new GridSpan() { Val = 11 }); 
                    cell1_1.TableCellProperties = cell1_1Props;
                    row1.Append(cell1_1);

                    table.Append(row1);

                    // Crear la segunda fila
                    TableRow row2 = new TableRow();

                    TableCell cell2_1 = CreateTableCell("90659", 11, false, true);
                    TableCellProperties cell2_1Props = new TableCellProperties(new GridSpan() { Val = 11 });
                    cell2_1.TableCellProperties = cell2_1Props;
                    row2.Append(cell2_1);

                    table.Append(row2);

               
                    TableRow row3 = new TableRow();
                    row3.Append(
                        CreateTableCell("#", 11, false, true), 
                        CreateTableCell("Información del Contrato de Servicios de Participación indirecta", 11, false, true, 4),  
                        CreateTableCell("Información del Participante Indirecto", 11, false, true, 6) 
                    );
                    table.Append(row3);

                    // Crear la cuarta fila (Sub-Encabezados) con ciertos campos en negritas
                    TableRow row4 = new TableRow();
                    row4.Append(
                        CreateTableCell("", 11, false, true),  
                        CreateTableCell("Fecha de celebración del contrato", 11, false, true), 
                        CreateTableCell("Fecha de inicio de prestación de servicios", 11, false, true),  
                        CreateTableCell("Fecha de término de prestación de servicios", 11, false, true),
                        CreateTableCell("Duración del contrato", 11, false, true), 
                        CreateTableCell("Clabe Asignada\n115", 11, false, true),  
                        CreateTableCell("Denominación o razón social", 11, false, true), 
                        CreateTableCell("Nombre Comercial o marca", 11, false, true),  
                        CreateTableCell("RFC con homoclave", 11, false, true), 
                        CreateTableCell("Tercia de dígitos identificadores\n116", 11, false, true),  
                        CreateTableCell("Proveedor de Servicios de Participación indirecta\n117", 11, false, true)  
                    );
                    table.Append(row4);


                    // Crear la quinta fila (Formato de fechas y celdas vacías)
                    TableRow row5 = new TableRow();

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

                    row5.Append(
                        CreateTableCell("", 11, false, true),
                        CreateTableCell(fechaCelebracionFormateada, 11, false, true),
                        CreateTableCell(fechaCelebracionFormateada2, 11, false, true),
                         CreateTableCell(fechaCelebracionFormateada3, 11, false, true),
                          CreateTableCell(participante.Content[0].duracionContrato, 11, false, true),
                           CreateTableCell(participante.Content[0].clabe, 11, false, true),
                            CreateTableCell(participante.Content[0].razonSocial, 11, false, true),
                             CreateTableCell(participante.Content[0].nomComerciaMarca, 11, false, true),
                              CreateTableCell(participante.Content[0].rfc, 11, false, true),
                               CreateTableCell(participante.Content[0].tercia, 11, false, true),
                                CreateTableCell(participante.Content[0].proveedor, 11, false, true)
                    );
                    table.Append(row5);

                    // Agregar filas vacías
                    for (int i = 0; i < 0; i++)
                    {
                        TableRow emptyRow = new TableRow();
                        for (int j = 0; j < 11; j++)
                        {
                            emptyRow.Append(CreateEmptyTableCell());
                        }
                        table.Append(emptyRow);
                    }

                    // Agregar la tabla al cuerpo del documento
                    body.Append(table);

                    TableCell CreateTableCell(string text, int fontSize, bool bold, bool hasBorder = true, int? gridSpan = null, HighlightColorValues? highlight = null)
                    {
                        TableCell cell = new TableCell();

                        // Crear el párrafo con el texto
                        Paragraph para = new Paragraph();
                        Run run = new Run();
                        RunProperties runProps = new RunProperties();

                        // Aplicar fuente Calibri
                        RunFonts runFonts = new RunFonts() { Ascii = "Calibri", HighAnsi = "Calibri" };
                        runProps.Append(runFonts);

                        if (bold)
                        {
                            runProps.Append(new Bold());
                        }

                        runProps.Append(new FontSize() { Val = (fontSize * 2).ToString() });
                        if (highlight.HasValue)
                        {
                            runProps.Append(new Highlight() { Val = highlight.Value });
                        }
                        run.Append(runProps);
                        run.Append(new Text(text));

                        // Centrar horizontalmente
                        para.Append(new ParagraphProperties(new Justification() { Val = JustificationValues.Center }));
                        para.Append(run);

                        // Agregar el párrafo a la celda
                        cell.Append(para);

                        // Propiedades de la celda, incluyendo alineación vertical
                        TableCellProperties cellProperties = new TableCellProperties();

                        if (gridSpan.HasValue)
                        {
                            cellProperties.Append(new GridSpan() { Val = gridSpan.Value });
                        }

                        cellProperties.Append(new TableCellVerticalAlignment() { Val = TableVerticalAlignmentValues.Center });

                        // Agregar borde si se solicita
                        if (hasBorder)
                        {
                            cellProperties.Append(new TableCellBorders(
                                new TopBorder() { Val = BorderValues.Single },
                                new BottomBorder() { Val = BorderValues.Single },
                                new LeftBorder() { Val = BorderValues.Single },
                                new RightBorder() { Val = BorderValues.Single }
                            ));
                        }

                        // Agregar las propiedades a la celda
                        cell.Append(cellProperties);

                        return cell;
                    }

                    TableCell CreateEmptyTableCell()
                    {
                        // Crear celdas vacías con justificación centrada
                        return CreateTableCell("", 9, false, true);
                    }

                    Paragraph breakParagraph1 = new Paragraph(new Run(new Break()));
                    body.Append(breakParagraph1);
                    Paragraph breakParagraph2 = new Paragraph(new Run(new Break()));
                    


                    // Crear el texto de "Atentamente" centrado en la hoja
                    Paragraph paragraphAtentamente = new Paragraph();
                    Run runAtentamente = new Run(new Text("A t e n t a m e n t e,"));
                    body.Append(breakParagraph2);
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
                    Run runFirmaElectronica = new Run(new Text("[FIRMA ELECTRÓNICA]"));
                    RunProperties runPropsFirmaElectronica = new RunProperties(new RunFonts() { Ascii = "Calibri" }, new FontSize() { Val = "22" }); // Tamaño 11
                    runFirmaElectronica.PrependChild(runPropsFirmaElectronica);
                    paragraphFirmaElectronica.Append(new ParagraphProperties(new Justification() { Val = JustificationValues.Center }));
                    paragraphFirmaElectronica.Append(runFirmaElectronica);

                    // Crear el texto del nombre centrado
                    Paragraph paragraphNombre = new Paragraph();
                    Run runNombre = new Run(new Text(participante.Content[0].nombreFirmante));
                    RunProperties runPropsNombre = new RunProperties(new RunFonts() { Ascii = "Calibri" }, new FontSize() { Val = "22" }, new Bold(), new Underline() { Val = UnderlineValues.Single }); // Tamaño 11 y negritas
                    runNombre.PrependChild(runPropsNombre);
                    paragraphNombre.Append(new ParagraphProperties(new Justification() { Val = JustificationValues.Center }));
                    paragraphNombre.Append(runNombre);

                    // Crear el texto del título centrado
                    Paragraph paragraphTitulo = new Paragraph();
                    Run runTitulo = new Run(new Text(participante.Content[0].cargoPuesto));
                    RunProperties runPropsTitulo = new RunProperties(new RunFonts() { Ascii = "Calibri" }, new FontSize() { Val = "22" }); // Tamaño 11
                    runTitulo.PrependChild(runPropsTitulo);
                    paragraphTitulo.Append(new ParagraphProperties(new Justification() { Val = JustificationValues.Center }));
                    paragraphTitulo.Append(runTitulo);


                    // Agregar los párrafos al cuerpo del documento
                    body.Append(paragraphAtentamente);
                    body.Append(paragraphEmpresa);
                    body.Append(paragraphFirmaElectronica);
                    body.Append(paragraphNombre);
                    body.Append(paragraphTitulo);


                     Paragraph breakParagraph = new Paragraph(new Run(new Break()));
                    body.Append(breakParagraph);
                
                    // Crear el primer párrafo alineado a la izquierda
                    Paragraph paragraph11 = new Paragraph();
                    Run run11 = new Run(new Text("114: la duración del contrato podrá ser indeterminada; en estos casos, colocar “NA” en el campo. Señalar la duración en días hábiles bancarios."));
                    RunProperties runProps11 = new RunProperties(new RunFonts() { Ascii = "Calibri" }, new FontSize() { Val = "22" }); // Tamaño 11
                    run11.PrependChild(runProps11);
                    paragraph11.Append(new ParagraphProperties(new Justification() { Val = JustificationValues.Left }));
                    paragraph11.Append(run11);

                    // Crear un salto de línea
                  

                    // Crear el segundo párrafo alineado a la izquierda
                    Paragraph paragraph21 = new Paragraph();
                    Run run21 = new Run(new Text("115: Identificador de cuenta asignado al Participante Indirecto, para realizar operaciones en nombre propio."));
                    RunProperties runProps21 = new RunProperties(new RunFonts() { Ascii = "Calibri" }, new FontSize() { Val = "22" }); // Tamaño 11
                    run21.PrependChild(runProps21);
                    paragraph21.Append(new ParagraphProperties(new Justification() { Val = JustificationValues.Left }));
                    paragraph21.Append(run21);

                    // Crear el tercer párrafo alineado a la izquierda
                    Paragraph paragraph31 = new Paragraph();
                    Run run31 = new Run(new Text("116: Corresponde a los 3 dígitos que utilizarán los Participantes para identificar a los Participantes Indirectos conforme a la estructura señalada en la sección “6.14 Estructura de la Clave Básica Estandarizada (clabe)” del manual SPEI de Banxico en el Visor, que corresponda para la conformación de las CLABEs bajo el esquema de partición indirecta."));
                    RunProperties runProps31 = new RunProperties(new RunFonts() { Ascii = "Calibri" }, new FontSize() { Val = "22" }); // Tamaño 11
                    run31.PrependChild(runProps31);
                    paragraph31.Append(new ParagraphProperties(new Justification() { Val = JustificationValues.Left }));
                    paragraph31.Append(run31);

                    // Crear el cuarto párrafo alineado a la izquierda
                    Paragraph paragraph41 = new Paragraph();
                    Run run41 = new Run(new Text("117: Corresponde a la denominación o razón social de la entidad que en su caso preste algún servicio, a un Participante Indirecto, necesario para la prestación de Servicios de Participación Indirecta. Como podrá ser la provisión de programas informáticos; infraestructura de tecnologías de la información y telecomunicaciones; en caso de que el Participante actúe como su propio proveedor, Incluir “NA”."));
                    RunProperties runProps41 = new RunProperties(new RunFonts() { Ascii = "Calibri" }, new FontSize() { Val = "22" }); // Tamaño 11
                    run41.PrependChild(runProps41);
                    paragraph41.Append(new ParagraphProperties(new Justification() { Val = JustificationValues.Left }));
                    paragraph41.Append(run41);

                    // Agregar los párrafos al cuerpo del documento
                    body.Append(paragraph11);
                 
                    body.Append(paragraph21);
                  
                    body.Append(paragraph31);
                  
                    body.Append(paragraph41);



                    mainPart.Document.Save();
                }

                // Devolvemos el MemoryStream como un arreglo de bytes.
                return memoryStream.ToArray();
            }
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
