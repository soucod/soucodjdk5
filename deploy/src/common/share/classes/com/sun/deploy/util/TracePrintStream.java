/*
 * @(#)TracePrintStream.java	1.2 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.deploy.util;

import java.io.ByteArrayOutputStream;

public class TracePrintStream extends ByteArrayOutputStream {

    public TracePrintStream() {}
    
    private void enQueueData() {
	String message = toString();
	reset();
	Trace.print(message, TraceLevel.DEFAULT);
    }


    public void write(int b) {
	super.write(b);

	// Add data to queue when end of line or EOF.
	if (b == '\n' || b == '\r') {
	    enQueueData();
	}
    }

    public void write(byte b[], int off, int len) {
	super.write(b,off,len);

	// Add data to queue when end of line or EOF.
	if (b[len-1] == '\n' || b[len-1] == '\r') {
	   enQueueData();
	}
    }

}
