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
import com.google.template.soy.tofu.SoyTofu;
import java.io.IOException;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.liferay.faces.metal.component.popover.Popover;
import com.liferay.faces.metal.render.internal.DelegatingMetalRendererBase;
import com.liferay.faces.metal.render.internal.StringResponseWriter;
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
public class PopoverRenderer extends DelegatingMetalRendererBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(PopoverRenderer.class);

	// Private Constants
	private static final String JSON_CONFIG_KEY_PREFIX = PopoverRenderer.class.getName() + "_JSON_CONFIG_KEY_";

	@Override
	public String getDelegateComponentFamily() {
		return Popover.COMPONENT_FAMILY;
	}

	@Override
	public String getDelegateRendererType() {
		return "javax.faces.Group";
	}

	@Override
	public void encodeMetalAttributes(FacesContext facesContext, ResponseWriter respoonseWriter, UIComponent uiComponent) throws IOException {

		String jsonConfig = (String) facesContext.getAttributes().remove(JSON_CONFIG_KEY_PREFIX + uiComponent.hashCode());

		if (jsonConfig != null) {
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
		ResponseWriter stringResponseWriter = new StringResponseWriter(responseWriter);
		facesContext.setResponseWriter(stringResponseWriter);
	}

	@Override
	public void encodeMarkupEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		if (uiComponent.isRendered()) {

			StringResponseWriter stringResponseWriter = (StringResponseWriter) facesContext.getResponseWriter();
			ResponseWriter originalResponseWriter = stringResponseWriter.getWrapped();
			originalResponseWriter.startElement("div", uiComponent);
			originalResponseWriter.writeAttribute("id", uiComponent.getClientId(facesContext), "id");
			SoyFileSet.Builder builder = SoyFileSet.builder();
			URL url = PopoverRenderer.class.getResource("/metaljs/build/soy/metal-popover/src/Popover.soy");
			builder.add(url);

			SoyFileSet soyFileSet = builder.build();
			SoyTofu tofu = soyFileSet.compileToTofu();
			SoyTofu.Renderer soyRenderer = tofu.newRenderer("Popover.render");
			Map<String, String> config = new HashMap<String, String>();
			config.put("content", stringResponseWriter.toString());
			stringResponseWriter = new StringResponseWriter(originalResponseWriter);
			facesContext.setResponseWriter(stringResponseWriter);
			UIComponent headerFacet = uiComponent.getFacet("header");

			if (headerFacet != null) {

				headerFacet.encodeAll(facesContext);
				config.put("title", stringResponseWriter.toString());
			}

			soyRenderer.setData(config);
			facesContext.setResponseWriter(originalResponseWriter);
			originalResponseWriter.write(soyRenderer.render());
			JsonSerializer jsonSerializer = new JsonSerializer();
			String jsonConfig = jsonSerializer.serialize(config);
			facesContext.getAttributes().put(JSON_CONFIG_KEY_PREFIX + uiComponent.hashCode(), jsonConfig);
			originalResponseWriter.endElement("div");
		}
	}
}
