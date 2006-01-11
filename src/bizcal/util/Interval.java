package bizcal.util;

/**
 * @author Fredrik Bertilsson
 */
public class Interval
{
    private Comparable start;
    private Comparable end;
    private boolean includeStart = true;
    private boolean includeEnd = false;
    
    public Interval(Comparable start, Comparable end)
    {
        this.start = start;
        this.end = end;
    }
    
    protected Interval()
    {    	
    }
        
    public Comparable getStart() throws Exception {
		return start;
	}

	public void setStart(Comparable start) throws Exception {
		this.start = start;
	}

	public Comparable getEnd() throws Exception {
		return end;
	}

	public void setEnd(Comparable end) throws Exception {
		this.end = end;
	}
    
    
    /**
	 * @return Returns the includeEnd.
	 */
    public boolean isIncludeEnd()
    {
        return includeEnd;
    }
    /**
     * @param includeEnd The includeEnd to set.
     */
    public void setIncludeEnd(boolean includeEnd)
    {
        this.includeEnd = includeEnd;
    }
    
    /**
     * @return Returns the includeStart.
     */
    public boolean isIncludeStart()
    {
        return includeStart;
    }
    /**
     * @param includeStart The includeStart to set.
     */
    public void setIncludeStart(boolean includeStart)
    {
        this.includeStart = includeStart;
    }
    
    public boolean contains(Comparable obj)
    	throws Exception
    {
        if (start != null) {
           if (obj.compareTo(getStart()) < 0)
               return false;
           if (!includeStart && obj.compareTo(getStart()) == 0)
               return false;
        }
        if (end != null) {
            if (obj.compareTo(getEnd()) > 0)
                return false;
            if (!includeEnd && obj.compareTo(getEnd()) == 0)
                return false;
        }
        return true;        
    }
    
    public boolean contains(Interval interval) throws Exception {
		if (start != null) {
			int cmp = interval.getStart().compareTo(start);
			if (cmp < 0)
				return false;
			if (cmp == 0) {
				if (!includeStart && interval.isIncludeStart())
					return false;
			}
		}
		if (end != null) {
			if (interval.getEnd() == null)
				return false;
			int cmp = interval.getEnd().compareTo(end);
			if (cmp > 0)
				return false;
			if (cmp == 0) {
				if (!includeEnd && interval.isIncludeEnd())
					return false;
			}
		}
		return true;
	}
    
    public boolean overlap(Interval interval)
    	throws Exception
    {
        Interval tmpInterv = new Interval(getStart(), getEnd());
        tmpInterv.setIncludeStart(false);
        tmpInterv.setIncludeEnd(false);
        if (tmpInterv.contains(interval.getStart()))
            return true;
        if (tmpInterv.contains(interval.getEnd()))
            return true;
        if (interval.contains(getStart()) &&
            interval.contains(getEnd()))
            return true;
        return false;
    }
    
    public Interval intersection(Interval interval)
    	throws Exception
    {
    	Comparable start = getStart();
    	Comparable end = getEnd();
    	if (interval.getStart().compareTo(start) > 0)
    		start = interval.getStart();
    	if (interval.getEnd().compareTo(end) < 0)
    		end = interval.getEnd();
    	if (start.compareTo(end) > 0)
    		return null;
    	return new Interval(start, end);
    }

    public Interval union(Interval interval) throws Exception {
		Comparable start = getStart();
		Comparable end = getEnd();
		if (interval.getStart().compareTo(start) < 0)
			start = interval.getStart();
		if (interval.getEnd().compareTo(end) > 0)
			end = interval.getEnd();
		return new Interval(start, end);
	}
    
	public boolean equals(Object other)
	{
	    try {
    	    if (other instanceof Interval) {
    	        Interval interval = (Interval) other;
	    	    return NullSafe.equals(getStart(), interval.getStart()) &&
	    	    	NullSafe.equals(getEnd(), interval.getEnd());
    	    } else
    	        return false;
	    } catch (Exception e) {
	        throw BizcalException.create(e);
	    }
	}
	
	public String toString()
	{
	    try {
		    StringBuffer str = new StringBuffer();
		    if (getStart() != null)
		        str.append(getStart().toString());
		    str.append(" - ");
		    if (getEnd() != null)
		        str.append(getEnd().toString());
		    return str.toString();
	    } catch (Exception e) {
	        throw BizcalException.create(e);
	    }
	}
    
}
