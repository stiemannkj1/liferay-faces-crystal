/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.crystal.component.inputfile.internal;
//J-

import java.io.IOException;

import javax.annotation.Generated;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.liferay.faces.crystal.component.inputfile.internal.InputFileRendererCompat;

import com.liferay.faces.crystal.component.inputfile.InputFile;


/**
 * @author	Bruno Basto
 * @author	Kyle Stiemann
 */
@Generated(value = "com.liferay.crystal.tools.builder.FacesBuilder")
public abstract class InputFileRendererBase extends InputFileRendererCompat {

	// Protected Constants
	protected static final String APPEND_NEW_FILES = "appendNewFiles";
	protected static final String AUTO = "auto";
	protected static final String CLIENT_KEY = "clientKey";
	protected static final String CONTENT_TYPES = "contentTypes";
	protected static final String FILE_UPLOAD_LISTENER = "fileUploadListener";
	protected static final String LOCATION = "location";
	protected static final String MAX_FILE_SIZE = "maxFileSize";
	protected static final String MULTIPLE = "multiple";
	protected static final String SHOW_PREVIEW = "showPreview";
	protected static final String SHOW_PROGRESS = "showProgress";
}
//J+
