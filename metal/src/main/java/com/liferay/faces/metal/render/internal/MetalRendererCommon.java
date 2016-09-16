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

import com.liferay.faces.util.component.ClientComponent;
import com.liferay.faces.util.render.BufferedScriptResponseWriter;
import com.liferay.faces.util.render.RendererUtil;


/**
 * @author  Kyle Stiemann
 */
/* package-private */ class MetalRendererCommon {

	/* package-private */ static void encodeEventCallback(ResponseWriter responseWriter, String varName, String methodName,
		String eventName, String callback) throws IOException {
		responseWriter.write(varName);
		responseWriter.write(".");
		responseWriter.write(methodName);
		responseWriter.write("('");
		responseWriter.write(eventName);
		responseWriter.write("',function(event){");
		responseWriter.write(callback);
		responseWriter.write("});");
	}

	/* package-private */ static void encodeJavaScript(FacesContext facesContext, UIComponent uiComponent, MetalRenderer metalRenderer)
		throws IOException {

		ResponseWriter responseWriter = facesContext.getResponseWriter();

		BufferedScriptResponseWriter bufferedScriptResponseWriter = new BufferedScriptResponseWriter();
		facesContext.setResponseWriter(bufferedScriptResponseWriter);

		metalRenderer.encodeJavaScriptBegin(facesContext, uiComponent);
		metalRenderer.encodeJavaScriptMain(facesContext, uiComponent);
		metalRenderer.encodeJavaScriptCustom(facesContext, uiComponent);
		metalRenderer.encodeJavaScriptEnd(facesContext, uiComponent);

		String[] modules = null;

		metalRenderer.renderScript(facesContext, bufferedScriptResponseWriter.toString(),
			metalRenderer.getModules(facesContext, uiComponent));
		facesContext.setResponseWriter(responseWriter);
	}

	/* package-private */ static void encodeJavaScriptBegin(FacesContext facesContext, UIComponent uiComponent, MetalRenderer metalRenderer)
		throws IOException {

		if (facesContext.getPartialViewContext().isAjaxRequest() && (uiComponent instanceof ClientComponent)) {

			ClientComponent clientComponent = (ClientComponent) uiComponent;
			String clientVarName = metalRenderer.getClientVarName(facesContext, clientComponent);
			String clientKey = clientComponent.getClientKey();

			if (clientKey == null) {
				clientKey = clientVarName;
			}

			ResponseWriter responseWriter = facesContext.getResponseWriter();
			encodeLiferayComponentVar(responseWriter, clientVarName, clientKey);
			responseWriter.write("if(");
			responseWriter.write(clientVarName);
			responseWriter.write("){");
			responseWriter.write(clientVarName);
			responseWriter.write(".destroy();}");
		}
	}

	/* package-private */ static void encodeJavaScriptEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {
		// no-op
	}

	/**
	 * This method renders JavaScript which creates the Metal component and puts it into the Liferay.component map.
	 * Example output of this function is shown below:
	 *
	 * <pre>
	    {@code
	        Liferay.component('clientKey',
	                new A.MetalComponent({
	                    attribute1:value1,
	                    attribute2:value2,
	                    ...
	                    attributeN:valueN
	                })
	        );
	    }
	 * </pre>
	 *
	 * @throws  IOException
	 */
	/* package-private */ static void encodeJavaScriptMain(FacesContext facesContext, UIComponent uiComponent, String metalClassName,
		MetalRenderer metalRenderer) throws IOException {

		ResponseWriter responseWriter = facesContext.getResponseWriter();
		ClientComponent clientComponent = (ClientComponent) uiComponent;
		String clientKey = clientComponent.getClientKey();

		if (clientKey == null) {
			clientKey = metalRenderer.getClientVarName(facesContext, clientComponent);
		}

		// Begin encoding JavaScript to create the Metal JavaScript component and put it in the Liferay.component map.
		responseWriter.write("Liferay.component('");

		String escapedClientKey = RendererUtil.escapeJavaScript(clientKey);
		responseWriter.write(escapedClientKey);
		responseWriter.write("',");

		// Write Metal JavaScript component.
		responseWriter.write("new ");
		responseWriter.write(metalClassName);
		responseWriter.write(".default(");
		metalRenderer.encodeMetalAttributes(facesContext, responseWriter, uiComponent);
		responseWriter.write(")");

		// Close Liferay.component parenthesis.
		responseWriter.write(");");
	}

	/* package-private */ static void encodeLiferayComponentVar(ResponseWriter responseWriter, String clientVarName, String clientKey)
		throws IOException {

		responseWriter.write("var ");
		responseWriter.write(clientVarName);
		responseWriter.write("=");
		encodeLiferayComponent(responseWriter, clientKey);
		responseWriter.write(";");
	}

	private static void encodeLiferayComponent(ResponseWriter responseWriter, String clientKey) throws IOException {

		responseWriter.write("Liferay.component('");

		String escapedClientKey = RendererUtil.escapeJavaScript(clientKey);
		responseWriter.write(escapedClientKey);
		responseWriter.write("')");
	}
}
