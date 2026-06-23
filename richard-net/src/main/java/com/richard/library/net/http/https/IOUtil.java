package com.richard.library.net.http.https;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {


    public static String readFully(InputStream inputStream) throws IOException {

        if (inputStream == null) {
            return "";
        }

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream); ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[1024];
            int available = 0;

            while ((available = bufferedInputStream.read(buffer)) >= 0) {
                byteArrayOutputStream.write(buffer, 0, available);
            }

            return byteArrayOutputStream.toString();
        }
    }
}
