package org.ag.util.recentfileset;

import java.io.File;
import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 * 
 * @author Ashley Gwinnell
 * @deprecated
 * @see {@link RecentFileSet}
 */
public class RecentFileSetPreferences 
{
	private Preferences preferences;
	
	public RecentFileSetPreferences() 
	{
		this(RecentFileSetPreferences.class);
	}
	
	public RecentFileSetPreferences(Class<?> c) {
		this.preferences = Preferences.userNodeForPackage(c);
	}
	
	public void add(String file) 
	{
		ArrayList<String> files = this.get();
		switch (files.size()) {
			case 0:
				this.preferences.put("recentfile_0", file);
				break;
			case 1:
				this.preferences.put("recentfile_1", this.preferences.get("recentfile_0", "null"));
				this.preferences.put("recentfile_0", file);
				break;
			case 2:
				this.preferences.put("recentfile_2", this.preferences.get("recentfile_1", "null"));
				this.preferences.put("recentfile_1", this.preferences.get("recentfile_0", "null"));
				this.preferences.put("recentfile_0", file);
				break;
			case 3:
				this.preferences.put("recentfile_2", this.preferences.get("recentfile_1", "null"));
				this.preferences.put("recentfile_1", this.preferences.get("recentfile_0", "null"));
				this.preferences.put("recentfile_0", file);
				break;
		}
	}
	
	public ArrayList<String> get() 
	{
		ArrayList<String> files = new ArrayList<String>();
		String zero = this.preferences.get("recentfile_0", "null");
		if (!zero.equals("null")) {
			files.add(zero);
		}
		String one = this.preferences.get("recentfile_1", "null");
		if (!one.equals("null")) {
			files.add(one);
		}
		String two = this.preferences.get("recentfile_2", "null");
		if (!two.equals("null")) {
			files.add(two);
		}
		return files;
	}
	
	public boolean isRecentFile(String filename) 
	{
		return this.isRecentFile(new File(filename));
	}
	
	public boolean isRecentFile(File file) {
		ArrayList<String> files = this.get();
		for (int i = 0; i < files.size(); i++) {
			if (file.getAbsoluteFile().equals(files.get(i))) {
				return true;
			}
		}
		return false;
	}
	
}
