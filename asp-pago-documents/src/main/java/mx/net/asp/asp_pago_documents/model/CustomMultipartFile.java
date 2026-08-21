package mx.net.asp.asp_pago_documents.model;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class CustomMultipartFile implements MultipartFile {

private final byte[] fileContent;

private String fileName;

private String originalFilename;

private String contentType;

private File file;

private String destPath = System.getProperty("Java.io.tmpdir");

private FileOutputStream fileOutputStream;

public CustomMultipartFile(byte[] fileData, String name) {
    this.fileContent = fileData;
    this.fileName = name;
    this.originalFilename = name;
    file = new File(destPath + fileName);

}

public File getFile() {
	return file;
}

public void setFile(File file) {
	this.file = file;
}

@Override
public void transferTo(File dest) throws IOException, IllegalStateException {
    fileOutputStream = new FileOutputStream(dest);
    fileOutputStream.write(fileContent);
}

public void clearOutStreams() throws IOException {
if (null != fileOutputStream) {
        fileOutputStream.flush();
        fileOutputStream.close();
        file.deleteOnExit();
    }
}

@Override
public byte[] getBytes() throws IOException {
    return fileContent;
}

@Override
public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(fileContent);
}

@Override
public String getName() {
	// TODO Auto-generated method stub
	return fileName;
}

@Override
public String getOriginalFilename() {
	// TODO Auto-generated method stub
	return originalFilename;
}

@Override
public String getContentType() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public boolean isEmpty() {
	// TODO Auto-generated method stub
	return false;
}

@Override
public long getSize() {
	// TODO Auto-generated method stub
	return 0;
}

}
