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
package com.liferay.faces.metal.component.dialog.internal;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.UnsafeSanitizedContentOrdainer;
import com.google.template.soy.tofu.SoyTofu;

import com.liferay.faces.metal.component.dialog.Dialog;
import com.liferay.faces.metal.render.internal.MetalRendererBase;
import com.liferay.faces.util.component.ClientComponent;
import com.liferay.faces.util.component.ComponentUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;

import jodd.json.JsonSerializer;


/**
 * @author  Vernon Singleton
 */

//J-
@FacesRenderer(componentFamily = Dialog.COMPONENT_FAMILY, rendererType = Dialog.RENDERER_TYPE)
@ResourceDependencies(
	{
		@ResourceDependency(library = "liferay-faces-metal-reslib", name = "css/bootstrap.min.css"),
		@ResourceDependency(library = "liferay-faces-metal-reslib", name = "require.js"),
		@ResourceDependency(library = "liferay-faces-metal-reslib", name = "liferay.js")
	}
)
//J+
public class DialogRenderer extends MetalRendererBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(DialogRenderer.class);

	// Private Constants
	private static final String CONFIG_KEY_PREFIX = DialogRenderer.class.getName() + "_JSON_CONFIG_KEY_";

	public static String escapeClientId(String clientId) {
		String escapedClientId = clientId;

		if (escapedClientId != null) {

			// JSF clientId values contain colons, which must be preceeded by double backslashes in order to have them
			// work with JavaScript functions like AUI.one(String). http://yuilibrary.com/projects/yui3/ticket/2528057
			escapedClientId = escapedClientId.replaceAll("[:]", "\\\\:");
		}

		return escapedClientId;
	}

	@Override
	public void encodeJavaScriptCustom(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		Dialog dialog = (Dialog) uiComponent;
		ResponseWriter responseWriter = facesContext.getResponseWriter();
		String clientId = uiComponent.getClientId(facesContext);
		String escapedClientId = ComponentUtil.escapeClientId(clientId);

		if (!dialog.isAutoShow()) {

			ClientComponent clientComponent = (ClientComponent) uiComponent;
			String clientVarName = getClientVarName(facesContext, clientComponent);
			String clientKey = clientComponent.getClientKey();

			if (clientKey == null) {
				clientKey = clientVarName;
			}

			encodeLiferayComponentVar(responseWriter, clientVarName, clientKey);
			responseWriter.write(clientVarName);
			responseWriter.write(
				".on('visibleChanged', function(event) { if (event.newVal) { document.querySelector('#");
			responseWriter.write(escapedClientId);
			responseWriter.write("').style = null; }});");
		}
	}

	@Override
	public void encodeMarkupBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		ResponseWriter responseWriter = facesContext.getResponseWriter();
		responseWriter.startElement("div", uiComponent);
		responseWriter.writeAttribute("id", uiComponent.getClientId(facesContext), "id");
		responseWriter.writeAttribute("style", "display: none;", "style");
		responseWriter.startElement("div", uiComponent);
		responseWriter.writeAttribute("id", uiComponent.getClientId(facesContext) + "_children", "id");
	}

	@Override
	public void encodeMarkupEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		ResponseWriter responseWriter = facesContext.getResponseWriter();
		responseWriter.endElement("div");

		SoyFileSet.Builder builder = SoyFileSet.builder();
		URL url = DialogRenderer.class.getResource("/metaljs/build/soy/metal-modal/src/Modal.soy");
		builder.add(url);

		SoyFileSet soyFileSet = builder.build();
		SoyTofu tofu = soyFileSet.compileToTofu();
		SoyTofu.Renderer soyRenderer = tofu.newRenderer("Modal.render");
		Map<String, Object> config = new HashMap<String, Object>();
		String body = "temp";
		SanitizedContent soyBody = UnsafeSanitizedContentOrdainer.ordainAsSafe(body, SanitizedContent.ContentKind.HTML);
		config.put("body", soyBody);

		UIComponent headerFacet = uiComponent.getFacet("header");
		String header = null;

		if (headerFacet != null) {

			responseWriter.startElement("div", uiComponent);
			responseWriter.writeAttribute("id", uiComponent.getClientId(facesContext) + "_header", "id");
			headerFacet.encodeAll(facesContext);
			responseWriter.endElement("div");
			header = "temp";

			SanitizedContent soyHeader = UnsafeSanitizedContentOrdainer.ordainAsSafe(header,
					SanitizedContent.ContentKind.HTML);
			config.put("header", soyHeader);
		}

		UIComponent footerFacet = uiComponent.getFacet("footer");
		String footer = null;

		if (footerFacet != null) {

			responseWriter.startElement("div", uiComponent);
			responseWriter.writeAttribute("id", uiComponent.getClientId(facesContext) + "_footer", "id");
			footerFacet.encodeAll(facesContext);
			responseWriter.endElement("div");
			footer = "temp";

			SanitizedContent soyFooter = UnsafeSanitizedContentOrdainer.ordainAsSafe(header,
					SanitizedContent.ContentKind.HTML);
			config.put("footer", soyFooter);
		}

		soyRenderer.setData(config);
		responseWriter.write(soyRenderer.render());
		config.put("body", body);
		responseWriter.startElement("script", uiComponent);
		responseWriter.writeAttribute("script", "text/javascript", null);

		String clientId = uiComponent.getClientId(facesContext);
		String escapedClientId = ComponentUtil.escapeClientId(clientId);
		responseWriter.write("var newParent = document.querySelector('#" + escapedClientId +
			" > div.modal > div.modal-dialog > div.modal-content > .modal-body');\n" +
			"var oldParent = document.querySelector('#" + escapedClientId + "_children');\n" +
			"newParent.innerHTML = '';\n" + "while (oldParent.childNodes.length > 0) {\n" +
			"    newParent.appendChild(oldParent.childNodes[0]);\n" + "}" +
			"oldParent.parentNode.removeChild(oldParent);");

		if (header != null) {

			config.put("header", header);
			responseWriter.write("newParent = document.querySelector('#" + escapedClientId +
				" > div.modal > div.modal-dialog > div.modal-content > .modal-header');\n" +
				"oldParent = document.querySelector('#" + escapedClientId + "_header');\n" +
				"newParent.innerHTML = '';\n" + "while (oldParent.childNodes.length > 0) {\n" +
				"    newParent.appendChild(oldParent.childNodes[0]);\n" + "}" +
				"oldParent.parentNode.removeChild(oldParent);");
		}

		if (footer != null) {

			config.put("footer", footer);
			responseWriter.write("newParent = document.querySelector('#" + escapedClientId +
				" > div.modal > div.modal-dialog > div.modal-content > .modal-footer');\n" +
				"oldParent = document.querySelector('#" + escapedClientId + "_footer');\n" +
				"newParent.innerHTML = '';\n" + "while (oldParent.childNodes.length > 0) {\n" +
				"    newParent.appendChild(oldParent.childNodes[0]);\n" + "}" +
				"oldParent.parentNode.removeChild(oldParent);");
		}

		responseWriter.write("document.querySelector('#" + escapedClientId + "').style = null;");
		responseWriter.endElement("script");
		responseWriter.endElement("div");

		facesContext.getAttributes().put(CONFIG_KEY_PREFIX + uiComponent.hashCode(), config);
	}

	@Override
	public void encodeMetalAttributes(FacesContext facesContext, ResponseWriter respoonseWriter,
		UIComponent uiComponent) throws IOException {

		Map<String, Object> config = (Map<String, Object>) facesContext.getAttributes().remove(CONFIG_KEY_PREFIX +
				uiComponent.hashCode());

		if (config != null) {

			String clientId = uiComponent.getClientId();
			String escapedClientId = escapeClientId(clientId);
			config.put("element", "#" + escapedClientId + " > div.modal");

			Dialog dialog = (Dialog) uiComponent;
			config.put("visible", dialog.isAutoShow());

			JsonSerializer jsonSerializer = new JsonSerializer();
			String jsonConfig = jsonSerializer.serialize(config);
			respoonseWriter.write(jsonConfig);
		}
	}

	@Override
	public String getMetalClassName(FacesContext facesContext, UIComponent uiComponent) {
		return "Modal";
	}

	@Override
	public String[] getModules(FacesContext facesContext, UIComponent uiComponent) {
		return new String[] { "metal-modal/src/Modal" };
	}
}
