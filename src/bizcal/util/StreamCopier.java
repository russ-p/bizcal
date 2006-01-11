package bizcal.util;

import java.io.*;

/**
 * Copies the content from one stream to another.
 *
 * @author Fredrik Bertilsson
 */
public class StreamCopier
{
    public static void copy(InputStream anInStream, OutputStream anOutStream)
            throws IOException
    {
        byte[] bs = new byte[8192];
        int length;
        while ((length = anInStream.read(bs)) != -1) {
            anOutStream.write(bs,0,length);
        }
        anOutStream.flush();
        anOutStream.close();
        anInStream.close();
    }

    public static String copy(InputStream anInStream)
            throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(anInStream));
        StringBuffer result = new StringBuffer();

        String line = reader.readLine();
        while (line != null) {
            result.append(line + "\n");
            line = reader.readLine();
        }
        reader.close();
        return result.toString();
    }

    public static byte[] copyToByteArray(InputStream anInStream)
            throws IOException
    {
        BufferedInputStream inputStream = new BufferedInputStream(anInStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream outputStream = new BufferedOutputStream(byteArrayOutputStream);

        int ch = inputStream.read();
        while (ch != -1) {
            outputStream.write(ch);
            ch = inputStream.read();
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

}
