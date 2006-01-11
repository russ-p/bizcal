package bizcal.web;

public interface WebCalendarCallback 
{
	public String getDetailURL()
		throws Exception;
	
	public String getStarttimeParamName()
		throws Exception;

	public String getCalendarParamName()
		throws Exception;
	
	public class BaseImpl
		implements WebCalendarCallback
	{
		public String getDetailURL()
			throws Exception
		{
			return null;
		}

		public String getStarttimeParamName()
			throws Exception
		{
			return "starttime";
		}
		
		public String getCalendarParamName()
			throws Exception
		{
			return "cal";
		}
	}
}
