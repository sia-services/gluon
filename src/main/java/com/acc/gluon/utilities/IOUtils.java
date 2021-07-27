package com.acc.gluon.utilities;

import javax.ws.rs.core.MultivaluedMap;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {

    /**
     * header sample
     * {
     * 	Content-Type=[image/png],
     * 	Content-Disposition=[form-data; name="file"; filename="filename.extension"]
     * }
     **/
    // TODO: check Content-Type must be excelX
    // TODO: check filename must end with .xlsx
    // get uploaded filename
    public static String getFileName(MultivaluedMap<String, String> headers) {

        String[] contentDisposition = headers.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");

                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return null;
    }

    public static byte[] toByteArray(InputStream input, int initialSize) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream(initialSize);
        try (var buffered = new BufferedInputStream(input)) {
            buffered.transferTo(output);
        }
        output.flush();
        return output.toByteArray();
    }

    // This is a workaround for read resource from IDE
    public static InputStream getResourceAsStream(Class<?> clazz, final String resourcePath) {
        var in = clazz.getResourceAsStream(resourcePath);

        if (in == null) {
            // this is how we load file within editor (eg eclipse)
            in = clazz.getClassLoader().getResourceAsStream(resourcePath);
        }
        if (in == null) throw new RuntimeException("Can not load " + resourcePath + " file");

        return in;
    }

    // convert byte to string
    public static byte[] getResourceAsByte(Class<?> clazz, final String resourcePath) throws IOException {
        try (var stream = getResourceAsStream(clazz, resourcePath)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024*1024);
            stream.transferTo(out);
            out.flush();
            return out.toByteArray();
        }
    }


}
