/*
 * @(#)MultiUIDefaults.java	1.16 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import java.util.Enumeration;
import java.util.Locale;



/**
 * 
 * @version 1.10 02/02/00
 * @author Hans Muller
 */
class MultiUIDefaults extends UIDefaults
{
    private UIDefaults[] tables;

    public MultiUIDefaults(UIDefaults[] defaults) {
	super();
	tables = defaults;
    }

    public MultiUIDefaults() {
	super();
	tables = new UIDefaults[0];
    }


    public Object get(Object key) 
    {
	Object value = super.get(key);
	if (value != null) {
	    return value;
	}

	for(int i = 0; i < tables.length; i++) {
	    UIDefaults table = tables[i];
	    value = (table != null) ? table.get(key) : null;
	    if (value != null) {
		return value;
	    }
	}

	return null;
    }


    public Object get(Object key, Locale l) 
    {
	Object value = super.get(key,l);
	if (value != null) {
	    return value;
	}

	for(int i = 0; i < tables.length; i++) {
	    UIDefaults table = tables[i];
	    value = (table != null) ? table.get(key,l) : null;
	    if (value != null) {
		return value;
	    }
	}

	return null;
    }


    public int size() {
	int n = super.size();
	for(int i = 0; i < tables.length; i++) {
	    UIDefaults table = tables[i];
	    n += (table != null) ? table.size() : 0;
	}
	return n;
    }


    public boolean isEmpty() {
	return size() == 0;
    }


    public Enumeration keys() 
    {
	Enumeration[] enums = new Enumeration[1 + tables.length];
	enums[0] = super.keys();
	for(int i = 0; i < tables.length; i++) {
	    UIDefaults table = tables[i];
	    if (table != null) {
		enums[i + 1] = table.keys();
	    }
	}
	return new MultiUIDefaultsEnumerator(enums);
    }


    public Enumeration elements() 
    {
	Enumeration[] enums = new Enumeration[1 + tables.length];
	enums[0] = super.elements();
	for(int i = 0; i < tables.length; i++) {
	    UIDefaults table = tables[i];
	    if (table != null) {
		enums[i + 1] = table.elements();
	    }
	}
	return new MultiUIDefaultsEnumerator(enums);
    }

    protected void getUIError(String msg) {
        if (tables.length > 0) {
            tables[0].getUIError(msg);
        } else {
            super.getUIError(msg);
        }
    }

    private static class MultiUIDefaultsEnumerator implements Enumeration
    {
	Enumeration[] enums;
	int n = 0;

	MultiUIDefaultsEnumerator(Enumeration[] enums) {
	    this.enums = enums;
	}

	public boolean hasMoreElements() {
	    for(int i = n; i < enums.length; i++) {
		Enumeration e = enums[i];
		if ((e != null) && (e.hasMoreElements())) {
		    return true;
		}
	    }
	    return false;
	}

	public Object nextElement() {
	    for(; n < enums.length; n++) {
		Enumeration e = enums[n];
		if ((e != null) && (e.hasMoreElements())) {
		    return e.nextElement();
		}
	    }
	    return null;
	}
    }


    public Object remove(Object key) 
    {
	Object value = super.remove(key);
	if (value != null) {
	    return value;
	}

	for(int i = 0; i < tables.length; i++) {
	    UIDefaults table = tables[i];
	    value = (table != null) ? table.remove(key) : null;
	    if (value != null) {
		return value;
	    }
	}

	return null;
    }


    public void clear() {
	super.clear();
	for(int i = 0; i < tables.length; i++) {
	    UIDefaults table = tables[i];
	    if (table != null) {
		table.clear();
	    }
	}
    }

    public synchronized String toString() {
	StringBuffer buf = new StringBuffer();
	buf.append("{");
	Enumeration keys = keys();
	while (keys.hasMoreElements()) {
	    Object key = keys.nextElement();
	    buf.append(key + "=" + get(key) + ", ");
	}
	int length = buf.length();
	if (length > 1) {
	    buf.delete(length-2, length);
	}
	buf.append("}");
	return buf.toString();
    }
}
