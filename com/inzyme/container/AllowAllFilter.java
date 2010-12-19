package com.inzyme.container;

public class AllowAllFilter implements IFilter {
	public AllowAllFilter () {
	}
	
	public boolean qualifies(Object _element) {
		return true;
	}
}
