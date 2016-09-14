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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.liferay.faces.util.client.Script;
import com.liferay.faces.util.component.ClientComponent;
import com.liferay.faces.util.render.DelegatingClientComponentRendererBase;
import com.liferay.faces.util.render.RendererUtil;


/**
 * @author  Neil Griffin
 */
public abstract class DelegatingMetalRendererBase extends DelegatingClientComponentRendererBase
	implements MetalRenderer {

	@Override
	public void decode(FacesContext facesContext, UIComponent uiComponent) {
		super.decode(facesContext, uiComponent);
	}

	@Override
	public void decodeClientBehaviors(FacesContext facesContext, UIComponent uiComponent) {
		RendererUtil.decodeClientBehaviors(facesContext, uiComponent);
	}

	@Override
	public void encodeBoolean(ResponseWriter responseWriter, String attributeName, Boolean attributeValue,
		boolean first) throws IOException {
		encodeBooleanProperty(responseWriter, attributeName, attributeValue, first);
	}

	@Override
	public void encodeClientId(ResponseWriter responseWriter, String attributeName, String clientId, boolean first)
		throws IOException {
		encodeClientIdProperty(responseWriter, attributeName, clientId, first);
	}

	@Override
	public void encodeClientId(ResponseWriter responseWriter, String attributeName, String clientId,
		UIComponent uiComponent, boolean first) throws IOException {
		encodeClientIdProperty(responseWriter, attributeName, clientId, uiComponent, first);
	}

	@Override
	public void encodeEventCallback(ResponseWriter responseWriter, String varName, String methodName, String eventName,
		String callback) throws IOException {
		MetalRendererCommon.encodeEventCallback(responseWriter, varName, methodName, eventName, callback);
	}

	@Override
	public void encodeInteger(ResponseWriter responseWriter, String attributeName, Integer attributeValue,
		boolean first) throws IOException {
		encodeIntegerProperty(responseWriter, attributeName, attributeValue, first);
	}

	@Override
	public void encodeJavaScript(FacesContext facesContext, UIComponent uiComponent) throws IOException {
		MetalRendererCommon.encodeJavaScript(facesContext, uiComponent, this);
	}

	@Override
	public void encodeJavaScriptBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException {
		MetalRendererCommon.encodeJavaScriptBegin(facesContext, uiComponent, this);
	}

	@Override
	public void encodeJavaScriptCustom(FacesContext facesContext, UIComponent uiComponent) throws IOException {
		// Default method provided as a no-op for convenience to subclasses.
	}

	@Override
	public void encodeJavaScriptEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {
		MetalRendererCommon.encodeJavaScriptEnd(facesContext, uiComponent);
	}

	@Override
	public void encodeJavaScriptMain(FacesContext facesContext, UIComponent uiComponent) throws IOException {
		MetalRendererCommon.encodeJavaScriptMain(facesContext, uiComponent,
			getMetalClassName(facesContext, uiComponent), this);
	}

	@Override
	public void encodeLiferayComponentVar(ResponseWriter responseWriter, String clientVarName, String clientKey)
		throws IOException {
		MetalRendererCommon.encodeLiferayComponentVar(responseWriter, clientVarName, clientKey);
	}

	@Override
	public void encodeNonEscapedObject(ResponseWriter responseWriter, String attributeName, Object attributeValue,
		boolean first) throws IOException {
		encodeNonEscapedObjectProperty(responseWriter, attributeName, attributeValue, first);
	}

	@Override
	public void encodeString(ResponseWriter responseWriter, String attributeName, Object attributeValue, boolean first)
		throws IOException {
		encodeStringProperty(responseWriter, attributeName, attributeValue, first);
	}

	@Override
	public void encodeWidgetRender(ResponseWriter responseWriter, boolean first) throws IOException {
		encodeBoolean(responseWriter, "render", true, first);
	}

	@Override
	public String getClientVarName(FacesContext facesContext, ClientComponent clientComponent) {
		return super.getClientVarName(facesContext, clientComponent);
	}

	@Override
	public void renderScript(FacesContext facesContext, String bufferedScriptString, String[] modules) {
		super.renderScript(facesContext, bufferedScriptString, modules, Script.ModulesType.ES6);
	}
}
