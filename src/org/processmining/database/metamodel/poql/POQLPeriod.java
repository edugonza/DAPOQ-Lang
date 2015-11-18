package org.processmining.database.metamodel.poql;

public class POQLPeriod {
	
	private long start = -1L;
	private long end = -1L;
	
	public POQLPeriod(long start, long end) {
		setStart(start);
		setEnd(end);
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}
	
	@Override
	public int hashCode() {
		String str = start+"#"+end;
		return str.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj.hashCode() == this.hashCode();
	}
}
