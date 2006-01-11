package bizcal.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class BizcalException
        extends RuntimeException
{
    private static final long serialVersionUID = 1L;    

    public BizcalException(Throwable e)
    {
        super(e);
        setStackTrace(e.getStackTrace());
    }

    public StackTraceElement[] getStackTrace()
    {
        return getCause().getStackTrace();
    }

    public static RuntimeException create(Throwable e)
    {
        if (e instanceof RuntimeException)
            return (RuntimeException) e;
        return new BizcalException(e);
    }
    
    public static String getStackTraceString(Throwable e)
    {
    	StringWriter writer = new StringWriter();
    	e.printStackTrace(new PrintWriter(writer));
    	return writer.getBuffer().toString();
    }
    
    public String getStackTraceString()
    {
    	return getStackTraceString(this);
    }
}