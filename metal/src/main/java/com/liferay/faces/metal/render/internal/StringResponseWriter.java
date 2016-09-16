/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.faces.metal.render.internal;

import java.io.IOException;
import java.io.StringWriter;

import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;


/**
 * @author  Kyle Stiemann
 */
public class StringResponseWriter extends ResponseWriterWrapper {

	// Private Data Members
	private ResponseWriter wrappedResponseWriter;
	private StringWriter stringWriter;

	public StringResponseWriter(ResponseWriter wrappedResponseWriter) {
		this.wrappedResponseWriter = wrappedResponseWriter;
		this.stringWriter = new StringWriter();
	}

	@Override
	public ResponseWriter getWrapped() {
		return wrappedResponseWriter;
	}

	@Override
	public String toString() {
		return stringWriter.toString();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		stringWriter.write(cbuf, off, len);
	}
}
