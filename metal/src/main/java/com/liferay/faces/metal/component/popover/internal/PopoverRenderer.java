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
package com.liferay.faces.metal.component.popover.internal;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.UnsafeSanitizedContentOrdainer;
import com.google.template.soy.tofu.SoyTofu;
import java.io.IOException;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.liferay.faces.metal.component.popover.Popover;
import com.liferay.faces.metal.render.internal.MetalRendererBase;
import com.liferay.faces.metal.render.internal.StringResponseWriter;
import com.liferay.faces.util.component.ClientComponent;
import com.liferay.faces.util.component.ComponentUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import jodd.json.JsonSerializer;


/**
 * @author  Vernon Singleton
 */

//J-
@FacesRenderer(componentFamily = Popover.COMPONENT_FAMILY, rendererType = Popover.RENDERER_TYPE)
@ResourceDependencies(
	{
		@ResourceDependency(library = "liferay-faces-metal-reslib", name = "css/bootstrap.min.css"),
		@ResourceDependency(library = "liferay-faces-metal-reslib", name = "require.js"),
		@ResourceDependency(library = "liferay-faces-metal-reslib", name = "liferay.js")
	}
)
//J+
public class PopoverRenderer extends MetalRendererBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(PopoverRenderer.class);

	// Private Constants
	private static final String CONFIG_KEY = PopoverRenderer.class.getName() + "_JSON_CONFIG_KEY";

	@Override
	public void encodeJavaScriptCustom(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		ResponseWriter responseWriter = facesContext.getResponseWriter();
		String clientId = uiComponent.getClientId(facesContext);
		String escapedClientId = ComponentUtil.escapeClientId(clientId);
		
		ClientComponent clientComponent = (ClientComponent) uiComponent;
		String clientVarName = getClientVarName(facesContext, clientComponent);
		String clientKey = clientComponent.getClientKey();

		if (clientKey == null) {
			clientKey = clientVarName;
		}

		encodeLiferayComponentVar(responseWriter, clientVarName, clientKey);
		responseWriter.write(clientVarName + ".on('visibleChanged', function(event) { if (event.newVal) { document.querySelector('#" + escapedClientId + "').style.display = null; }});");
	}

	@Override
	public void encodeMetalAttributes(FacesContext facesContext, ResponseWriter respoonseWriter, UIComponent uiComponent) throws IOException {

		Map<String, Object> config = (Map<String, Object>) uiComponent.getAttributes().remove(CONFIG_KEY);

		if (config != null) {

			String clientId = uiComponent.getClientId();
			String escapedClientId = escapeClientId(clientId);
			config.put("element", "#" + escapedClientId + " > div.popover");
			Popover popover = (Popover) uiComponent;
			String for_ = popover.getFor();
			UIComponent forComponent = popover.findComponent(for_);
			String escapedForClientId = for_;

			if (forComponent != null) {

				String forComponentClientId = forComponent.getClientId();
				escapedForClientId = escapeClientId(forComponentClientId);
			}

			config.put("selector", "#" + escapedForClientId);
			config.put("visible", false);
			JsonSerializer jsonSerializer = new JsonSerializer();
			String jsonConfig = jsonSerializer.serialize(config);
			respoonseWriter.write(jsonConfig);
		}
	}

	@Override
	public String getMetalClassName(FacesContext facesContext, UIComponent uiComponent) {
		return "Popover";
	}

	@Override
	public String[] getModules(FacesContext facesContext, UIComponent uiComponent) {
		return new String[] { "metal-popover/src/Popover" };
	}

	
	@Override
	public void encodeMarkupBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		ResponseWriter responseWriter = facesContext.getResponseWriter();
		responseWriter.startElement("div", uiComponent);
		responseWriter.writeAttribute("id", uiComponent.getClientId(facesContext), "id");
		responseWriter.writeAttribute("style", "display: none;", "style");
	}

	@Override
	public void encodeMarkupEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		if (uiComponent.isRendered()) {

			ResponseWriter responseWriter = facesContext.getResponseWriter();
			SoyFileSet.Builder builder = SoyFileSet.builder();
			URL url = PopoverRenderer.class.getResource("/metaljs/build/soy/metal-popover/src/Popover.soy");
			builder.add(url);

			SoyFileSet soyFileSet = builder.build();
			SoyTofu tofu = soyFileSet.compileToTofu();
			SoyTofu.Renderer soyRenderer = tofu.newRenderer("Popover.render");
			Map<String, Object> config = new HashMap<String, Object>();
			Popover popover = (Popover) uiComponent;
			String content = (String) popover.getAttributes().get("content");
			SanitizedContent soyContent = UnsafeSanitizedContentOrdainer.ordainAsSafe(content, SanitizedContent.ContentKind.HTML);
			config.put("content", soyContent);
			String title = (String) popover.getAttributes().get("headerText");

			if (title != null) {

				SanitizedContent soyTitle = UnsafeSanitizedContentOrdainer.ordainAsSafe(title, SanitizedContent.ContentKind.HTML);
				config.put("title", soyTitle);
			}

			config.put("elementClasses", popover.getStyleClass());
			soyRenderer.setData(config);
			responseWriter.write(soyRenderer.render());
			responseWriter.endElement("div");
			config.put("content", content);
			String delay = (String) popover.getAttributes().get("delay");

			if (delay != null) {
				config.put("delay", delay);
			}

			if (title != null) {
				config.put("title", title);
			}

			uiComponent.getAttributes().put(CONFIG_KEY, config);
		}
	}

	public static String escapeClientId(String clientId) {
		String escapedClientId = clientId;

		if (escapedClientId != null) {

			// JSF clientId values contain colons, which must be preceeded by double backslashes in order to have them
			// work with JavaScript functions like AUI.one(String). http://yuilibrary.com/projects/yui3/ticket/2528057
			escapedClientId = escapedClientId.replaceAll("[:]", "\\\\:");
		}

		return escapedClientId;
	}
}
