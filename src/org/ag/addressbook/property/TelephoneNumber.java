package org.ag.addressbook.property;

/**
 * An Telephone Number that can be added to a Contact.
 * @author Ashley Gwinnell
 */
public class TelephoneNumber 
{
	private boolean preferred = false;
	private String number;
	private Type type = Type.HOME;
	public enum Type {HOME, CELL, WORK};
	
	public TelephoneNumber() {
		
	}
	public TelephoneNumber(String number, boolean preferred, Type t) {
		this.number = number;
		this.preferred = preferred;
		this.type = t;
	}
	
	public String getNumber() {
		return number;
	}
	public Type getType() {
		return type;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public void setPreferred(boolean preferred) {
		this.preferred = preferred;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public boolean isPreferred() {
		return preferred;
	}
	public String toString() {
		return new String(this.number + " " + preferred + " " + type);
	}
	public String toSearchString() {
		return new String(this.number);
	}
}