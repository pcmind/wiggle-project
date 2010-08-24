package org.ufrj.db4o.internal.entity.query;

public class Select {

	private String alias;
	
	private Select next;

	public void setNext(Select next) {
		this.next = next;
	}

	public Select getNext() {
		return next;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}
	
	public void insereNext(Select select){
		if(this.next==null){
			next = select;
		}else{
			getNext().insereNext(select);
		}
	}
	
}
