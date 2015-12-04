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
package com.liferay.faces.crystal.component.selectrating.internal;
//J-

import java.io.IOException;

import javax.annotation.Generated;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.liferay.faces.crystal.render.internal.DelegatingCrystalRendererBase;

import com.liferay.faces.crystal.component.selectrating.SelectRating;


/**
 * @author	Bruno Basto
 * @author	Kyle Stiemann
 */
@Generated(value = "com.liferay.crystal.tools.builder.FacesBuilder")
public abstract class SelectRatingRendererBase extends DelegatingCrystalRendererBase {

	// Protected Constants
	protected static final String CLIENT_KEY = "clientKey";

	// Modules
	protected static final String[] MODULES = { "aui-rating" };

	@Override
	public void encodeCrystalAttributes(FacesContext facesContext, ResponseWriter responseWriter, UIComponent uiComponent) throws IOException {

		SelectRating selectRating = (SelectRating) uiComponent;
		boolean first = true;

		encodeHiddenAttributes(facesContext, responseWriter, selectRating, first);
	}

	@Override
	public String getCrystalClassName(FacesContext facesContext, UIComponent uiComponent) {
		return "Rating";
	}

	@Override
	protected String[] getModules(FacesContext facesContext, UIComponent uiComponent) {
		return MODULES;
	}

	protected void encodeHiddenAttributes(FacesContext facesContext, ResponseWriter responseWriter, SelectRating selectRating, boolean first) throws IOException {
		// no-op
	}

	@Override
	public String getDelegateComponentFamily() {
		return SelectRating.COMPONENT_FAMILY;
	}

	@Override
	public String getDelegateRendererType() {
		return "javax.faces.Radio";
	}
}
//J+
