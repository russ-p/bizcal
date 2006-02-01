package bizcal.swing.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.swing.ImageIcon;

public class ResourceIcon
	extends ImageIcon
{
	private static final long serialVersionUID = 1L;
	
	public ResourceIcon(String path)
		throws Exception
	{
		super(getBytes(path));
	}
	
	private static byte[] getBytes(String path)
		throws Exception
	{
		InputStream stream = ResourceIcon.class.getResourceAsStream(path);
        BufferedInputStream inputStream = new BufferedInputStream(stream);
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
