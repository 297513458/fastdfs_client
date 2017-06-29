/**
* Copyright (C) 2008 Happy Fish / YuQing
*
* FastDFS Java Client may be copied only under the terms of the GNU Lesser
* General Public License (LGPL).
* Please visit the FastDFS Home Page http://www.csource.org/ for more detail.
**/

package com.fastdfs.common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ini file reader / parser
 * 
 * @author Happy Fish / YuQing
 * @version Version 1.0
 */
public class IniFileReader {
	private Map<String, Object> map = new ConcurrentHashMap<String, Object>();
	private String conf_filename;

	/**
	 * @param conf_filename
	 *            config filename
	 */
	public IniFileReader(String conf_filename) throws FileNotFoundException, IOException {
		this.conf_filename = conf_filename;
		loadFromFile(conf_filename);
	}

	/**
	 * get the config filename
	 * 
	 * @return config filename
	 */
	public String getConfFilename() {
		return this.conf_filename;
	}

	/**
	 * get string value from config file
	 * 
	 * @param name
	 *            item name in config file
	 * @return string value
	 */
	public String getStrValue(String name) {
		Object obj;
		obj = this.map.get(name);
		if (obj == null) {
			return null;
		}

		if (obj instanceof String) {
			return (String) obj;
		}
		return ((List<String>) obj).get(0);
	}

	public IniFileReader(InputStream is) throws FileNotFoundException, IOException {
		loadFromFile(is);
	}

	/**
	 * get int value from config file
	 * 
	 * @param name
	 *            item name in config file
	 * @param default_value
	 *            the default value
	 * @return int value
	 */
	public int getIntValue(String name, int default_value) {
		String szValue = this.getStrValue(name);
		if (szValue == null) {
			return default_value;
		}

		return Integer.parseInt(szValue);
	}

	/**
	 * get boolean value from config file
	 * 
	 * @param name
	 *            item name in config file
	 * @param default_value
	 *            the default value
	 * @return boolean value
	 */
	public boolean getBoolValue(String name, boolean default_value) {
		String szValue = this.getStrValue(name);
		if (szValue == null) {
			return default_value;
		}

		return szValue.equalsIgnoreCase("yes") || szValue.equalsIgnoreCase("on") || szValue.equalsIgnoreCase("true")
				|| szValue.equals("1");
	}

	private void loadFromFile(InputStream is) throws FileNotFoundException, IOException {
		InputStreamReader fReader;
		BufferedReader buffReader;
		String line;
		String[] parts;
		String name;
		String value;
		Object obj;
		List<Object> valueList;
		fReader = new InputStreamReader(is);
		buffReader = new BufferedReader(fReader);
		if (map == null)
			map = new ConcurrentHashMap<String, Object>();
		try {
			while ((line = buffReader.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0 || line.charAt(0) == '#') {
					continue;
				}

				parts = line.split("=", 2);
				if (parts.length != 2) {
					continue;
				}

				name = parts[0].trim();
				value = parts[1].trim();

				obj = this.map.get(name);
				if (obj == null) {
					this.map.put(name, value);
				} else if (obj instanceof String) {
					valueList = new ArrayList<Object>();
					valueList.add(obj);
					valueList.add(value);
					this.map.put(name, valueList);
				} else {
					valueList = (List<Object>) obj;
					valueList.add(value);
				}
			}
		} finally {
			fReader.close();
		}
	}

	/**
	 * get all values from config file
	 * 
	 * @param name
	 *            item name in config file
	 * @return string values (array)
	 */
	public String[] getValues(String name) {
		Object obj;
		String[] values;

		obj = this.map.get(name);
		if (obj == null) {
			return null;
		}

		if (obj instanceof String) {
			values = new String[1];
			values[0] = (String) obj;
			return values;
		}

		Object[] objs = ((List<Object[]>) obj).toArray();
		values = new String[objs.length];
		System.arraycopy(objs, 0, values, 0, objs.length);
		return values;
	}

	@SuppressWarnings("unchecked")
	private void loadFromFile(String conf_filename) throws FileNotFoundException, IOException {
		FileReader fReader;
		BufferedReader buffReader;
		String line;
		String[] parts;
		String name;
		String value;
		Object obj;
		List<Object> valueList;
		fReader = new FileReader(conf_filename);
		buffReader = new BufferedReader(fReader);
		if (this.map == null)
			this.map = new ConcurrentHashMap<String, Object>();
		try {
			while ((line = buffReader.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0 || line.charAt(0) == '#') {
					continue;
				}

				parts = line.split("=", 2);
				if (parts.length != 2) {
					continue;
				}

				name = parts[0].trim();
				value = parts[1].trim();

				obj = this.map.get(name);
				if (obj == null) {
					this.map.put(name, value);
				} else if (obj instanceof String) {
					valueList = new ArrayList<Object>();
					valueList.add(obj);
					valueList.add(value);
					this.map.put(name, valueList);
				} else {
					valueList = (List<Object>) obj;
					valueList.add(value);
				}
			}
		} finally {
			fReader.close();
		}
	}
}
