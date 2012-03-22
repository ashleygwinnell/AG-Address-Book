package org.ag.util.recentfileset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * RecentFileSet
 * This is used to keep a record of recently opened files outside of runtime.
 * @author Ashley Gwinnell
 */
public class RecentFileSet 
{
	private static int GENERATED_ID = 0;
	
	private int id;
	private int size;
	private ArrayList<String> items;
	private String filename;
	
	/**
	 * Create a RecentFileSet with a maximum of 8 recent files.
	 */
	public RecentFileSet() 
	{
		this(8);
	}
	
	/**
	 * Create a RecentFileSet with the specified number of recent files.
	 * @param size The size of the RecentFileSet.
	 */
	public RecentFileSet(int size) {
		this.id = RecentFileSet.generateId();
		this.size = size;
		this.items = new ArrayList<String>();
		this.filename = new String("recentfiles_" + this.id + ".adr");
		this.items = this.get();
	}
	
	/**
	 * Used internally to keep a record of the recent file set used.
	 * WARNING: A program ideally should only have one RecentFileSet object per runtime.
	 * @return
	 */
	private static int generateId() {
		int id = GENERATED_ID;
		GENERATED_ID++;
		return id;
	}
	
	/**
	 * Add a String to the recent file set.
	 * @param file The absolute path of the file to add to the recent fileset.
	 */
	public void add(String file) 
	{
		if (this.items.contains(file)) {
			return;
		}
		this.items.add(file);
		if (this.items.size() > this.size) {
			this.items.clear();
			List<String> sublist = this.items.subList(0, this.size);
			for (int i = 0; i < sublist.size(); i++) {
				this.items.add(sublist.get(i));
			}
		}
		
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try
		{
			fos = new FileOutputStream(this.filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(this.items);
			out.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * Get a list of the recent files.
	 * @return a list of the recent files.
	 */
	public ArrayList<String> get() 
	{
		ArrayList<String> localfiles = new ArrayList<String>();
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try
		{
			fis = new FileInputStream(this.filename);
			in = new ObjectInputStream(fis);
			localfiles = (ArrayList<String>) in.readObject();
			in.close();
		}
		catch(FileNotFoundException ex)
		{
			// no recent files!
		}
		catch(ClassNotFoundException ex)
		{
			ex.printStackTrace();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		return localfiles;
	}
	
	/**
	 * Determines whether a String is in the recent file set.
	 * @param filename the String (filename) to check.
	 * @return true if it is a recent file.
	 */
	public boolean isRecentFile(String filename) 
	{
		return this.isRecentFile(new File(filename));
	}
	
	/**
	 * Determines whether a File is in the recent file set.
	 * @param file the File to check.
	 * @return true if it is a recent file.
	 */
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