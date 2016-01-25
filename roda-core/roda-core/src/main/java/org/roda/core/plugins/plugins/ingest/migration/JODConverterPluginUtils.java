package org.roda.core.plugins.plugins.ingest.migration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.roda.core.util.CommandException;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

public class JODConverterPluginUtils {

  public static Path runJODConverter(InputStream is, String inputFormat, String outputFormat) throws IOException,
    CommandException {

    // write the inputstream data on a new file (absolute path needed)
    Path input = Files.createTempFile("copy", "." + inputFormat);
    byte[] buffer = new byte[is.available()];
    is.read(buffer);
    OutputStream os = new FileOutputStream(input.toFile());
    os.write(buffer);
    os.close();
    is.close();

    Path output = Files.createTempFile("result", "." + outputFormat);

    File inputFile = new File(input.toString());
    File outputFile = new File(output.toString());

    // connect to an OpenOffice.org instance running on port 8100
    OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
    connection.connect();

    // convert
    DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
    converter.convert(inputFile, outputFile);

    // close the connection
    connection.disconnect();

    return output;
  }

  public static Path runJODConverter(Path input, String inputFormat, String outputFormat) throws IOException,
    CommandException {

    Path output = Files.createTempFile("result", "." + outputFormat);

    File inputFile = new File(input.toString());
    File outputFile = new File(output.toString());

    // connect to an OpenOffice.org instance running on port 8100
    OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
    connection.connect();

    // convert
    DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
    converter.convert(inputFile, outputFile);

    // close the connection
    connection.disconnect();

    return output;
  }

}
