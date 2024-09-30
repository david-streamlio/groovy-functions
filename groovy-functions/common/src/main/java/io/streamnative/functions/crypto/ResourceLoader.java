package io.streamnative.functions.crypto;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ResourceLoader {

    public static byte[] loadResourceAsBytes(String resourceName) throws IOException {
        InputStream inputStream = null;

        // 1. Try the current thread's context ClassLoader
        ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
        if (threadClassLoader != null) {
            inputStream = threadClassLoader.getResourceAsStream(resourceName);
            if (inputStream != null) {
                System.out.println("Loaded resource with Thread Context ClassLoader");
                return convertInputStreamToByteArray(inputStream);
            }
        }

        // 2. Try the ClassLoader of this class
        ClassLoader classClassLoader = ResourceLoader.class.getClassLoader();
        if (classClassLoader != null) {
            inputStream = classClassLoader.getResourceAsStream(resourceName);
            if (inputStream != null) {
                System.out.println("Loaded resource with Class's ClassLoader");
                return convertInputStreamToByteArray(inputStream);
            }
        }

        // 3. Try the System ClassLoader
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        if (systemClassLoader != null) {
            inputStream = systemClassLoader.getResourceAsStream(resourceName);
            if (inputStream != null) {
                System.out.println("Loaded resource with System ClassLoader");
                return convertInputStreamToByteArray(inputStream);
            }
        }

        // 4. Try the parent of the System ClassLoader (may be Bootstrap ClassLoader)
        ClassLoader parentClassLoader = systemClassLoader.getParent();
        if (parentClassLoader != null) {
            inputStream = parentClassLoader.getResourceAsStream(resourceName);
            if (inputStream != null) {
                System.out.println("Loaded resource with Parent (Bootstrap) ClassLoader");
                return convertInputStreamToByteArray(inputStream);
            }
        }

        // Resource not found
        System.err.println("Failed to load resource: " + resourceName);
        return null;
    }

    // Helper method to convert InputStream to byte[]
    private static byte[] convertInputStreamToByteArray(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] data = new byte[1024];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        } finally {
            inputStream.close();
        }
    }
}

