package org.ag.util;

import java.util.ArrayList;

/**
 * A bunch of String Utility methods.
 * @author Ashley Gwinnell
 */
public class StringUtil 
{
	/**
	 * String.split(String regex) doesn't do the job properly.
	 * We want it to keep items that are empty!
	 * @param subject The string to be splitting.
	 * @param splitAt The character to split at!
	 * @return The array of Strings after splitting!
	 */
	public static String[] splitWithoutTrimming(String subject, char splitAt) {
		ArrayList<String> strings = new ArrayList<String>();
		int beginIndex = 0;
		for (int i = 0; i < subject.length(); i++) {
			if (subject.charAt(i) == splitAt) {
				strings.add(subject.substring(beginIndex, i));
				beginIndex = i + 1;
			}
		}
		strings.add(subject.substring(subject.lastIndexOf(';')+1));
		String[] strs = new String[strings.size()];
		for (int i = 0; i < strings.size(); i++) {
			strs[i] = strings.get(i);
		}
		return strs;
	}
}
