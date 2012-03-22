package org.ag.addressbook.property;

/**
 * An EmailAddress that can be added to a Contact.
 * @author Ashley Gwinnell
 */
public class EmailAddress 
{
	private boolean preferred = false;
	private Type type = Type.HOME;
	public enum Type {HOME, WORK};
	private String address;
	
	public EmailAddress() {
		
	}
	public EmailAddress(String address, boolean preferred, Type t) {
		this.address = address;
		this.preferred = preferred;
		this.type = t;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddress() {
		return address;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	public Type getType() {
		return type;
	}
	
	public void setPreferred(boolean preferred) {
		this.preferred = preferred;
	}
	public boolean isPreferred() {
		return preferred;
	}
	
	public String toString() {
		return new String(this.address + " " + preferred + " " + type);
	}
	
	public String toSearchString() {
		return new String(this.address);
	}
	
}
