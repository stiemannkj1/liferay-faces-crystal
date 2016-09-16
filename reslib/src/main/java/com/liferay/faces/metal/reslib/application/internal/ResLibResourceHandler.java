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
package com.liferay.faces.metal.reslib.application.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.faces.metal.reslib.config.ResLibConfigParam;
import com.liferay.faces.util.config.ApplicationConfig;
import com.liferay.faces.util.config.ConfiguredServletMapping;
import com.liferay.faces.util.config.FacesConfig;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * <p>This is a resource handler that is only necessary in a non-Liferay (webapp) environment. The purpose of this class
 * is to provide Alloy/YUI/Bootstrap resources.</p>
 *
 * @author  Neil Griffin
 */
public class ResLibResourceHandler extends ResourceHandlerWrapper {

	// Public Constants
	public static final String LIBRARY_NAME = "liferay-faces-metal-reslib";

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ResLibResourceHandler.class);

	// Private Constants
	private static final String LIFERAY_JS = "liferay.js";
	private static final boolean LIFERAY_PORTAL_DETECTED = ProductFactory.getProduct(Product.Name.LIFERAY_PORTAL)
		.isDetected();
	private static final String ROOT_RESOURCE_DIRECTORY = "metaljs/build/amd/";

	// Private Members
	private ResourceHandler wrappedResourceHandler;

	public ResLibResourceHandler(ResourceHandler wrappedResourceHandler) {
		this.wrappedResourceHandler = wrappedResourceHandler;
	}

	@Override
	public Resource createResource(String resourceName) {

		Resource resource;

		// If Liferay Portal is detected, then do nothing since the resources are included on the Portal page already.
		if (LIFERAY_PORTAL_DETECTED) {
			resource = super.createResource(resourceName);
		}

		// Otherwise,
		else {

			// If an AlloyUI combo resource is to be created, then
			if ("require".equals(resourceName)) {

				// The AlloyUI framework can request a "combo" type of resource, which is either a set of JavaScript
				// resources or a set of CSS resources that are to be combined (concatenated together). In such cases,
				// each resource is specified as a separate URL parameter known as the "module path". For example:
				//
				// http://localhost:8080/my-webapp/javax.faces.resource/require?
				// ln=liferay-faces-metal-reslib
				// &build/widget-base/assets/skins/sam/widget-base.css
				// &build/tabview/assets/skins/sam/tabview.css
				// &build/aui-tabview/assets/skins/sam/aui-tabview.css
				// &build/aui-toggler-base/assets/skins/sam/aui-toggler-base.css
				//
				FacesContext facesContext = FacesContext.getCurrentInstance();
				ExternalContext externalContext = facesContext.getExternalContext();
				List<String> modulePaths = getModulePaths(externalContext);

				// If a module path is not found in the parameter map, then liferay.js is using an EL expression
				// "#{resource['liferay-faces-metal-reslib:require']}" to determine what AlloyUI considers the base path
				// for combo resources. In this case, create a resource whose getRequestPath() method returns the
				// expected base path.
				if (modulePaths.isEmpty()) {
					resource = new BasePathResource("require");
				}

				// Otherwise, if a module path is found in the parameter map, then consider it to be the name of an
				// individual script resource and ask the delegation chain to create it.
				else {

					if (modulePaths.size() > 1) {
						logger.warn(
							"The Alloy Reslib script resource cannot be used to obtain multiple script resources in one request. Use the combo resource to obtain multiple script resources combined in one request. Returning the first requested script.");
					}

					String requireResourceName = modulePaths.get(0);
					resource = super.createResource(requireResourceName, LIBRARY_NAME);
				}
			}

			// Otherwise, ask the delegation chain to create the resource.
			else {

				resource = super.createResource(resourceName);

				// If the resource is for liferay.js, then ensure that the EL expressions within the text of the file
				// are processed.
				if (LIFERAY_JS.equals(resourceName)) {
					resource = new FilteredResourceExpressionImpl(resource);
				}
			}
		}

		return resource;
	}

	@Override
	public Resource createResource(String resourceName, String libraryName) {

		Resource resource;

		if (!LIFERAY_PORTAL_DETECTED && LIBRARY_NAME.equals(libraryName)) {

			if ("require".equals(resourceName)) {
				resource = createResource(resourceName);
			}
			else {

				resource = super.createResource(resourceName, libraryName);

				if (LIFERAY_JS.equals(resourceName)) {
					resource = new FilteredResourceExpressionImpl(resource);
				}
			}
		}
		else {
			resource = super.createResource(resourceName, libraryName);
		}

		return resource;
	}

	@Override
	public Resource createResource(String resourceName, String libraryName, String contentType) {

		Resource resource;

		if (!LIFERAY_PORTAL_DETECTED && LIBRARY_NAME.equals(libraryName)) {

			if ("require".equals(resourceName)) {
				resource = createResource(resourceName);
			}
			else {

				resource = super.createResource(resourceName, libraryName, contentType);

				if (LIFERAY_JS.equals(resourceName)) {
					resource = new FilteredResourceExpressionImpl(resource);
				}
			}
		}
		else {
			resource = super.createResource(resourceName, libraryName, contentType);
		}

		return resource;
	}

	@Override
	public ResourceHandler getWrapped() {
		return wrappedResourceHandler;
	}

	@Override
	public void handleResourceRequest(FacesContext facesContext) throws IOException {

		// If Liferay Portal is detected, then ask the delegation chain to handle the resource request.
		if (LIFERAY_PORTAL_DETECTED) {
			super.handleResourceRequest(facesContext);
		}

		// Otherwise,
		else {

			ExternalContext externalContext = facesContext.getExternalContext();
			Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
			String libraryName = requestParameterMap.get("ln");
			String resourceName = getResourceName(externalContext);

			// If the resource that is to be rendered is a combo or script module resource and the module paths
			// extensions are invalid, then the resource cannot be found.
			if (LIBRARY_NAME.equals(libraryName) && ("require".equals(resourceName)) &&
					!areModulePathsExtensionsValid(externalContext)) {

				externalContext.setResponseHeader("Cache-Control", "private, no-cache, no-store, must-revalidate");
				externalContext.setResponseStatus(HttpServletResponse.SC_NOT_FOUND);
			}

			// Otherwise, pass responsibility for handling the resource to the resource-handler delegation chain.
			else {
				super.handleResourceRequest(facesContext);
			}
		}
	}

	@Override
	public boolean libraryExists(String libraryName) {

		if (!LIFERAY_PORTAL_DETECTED && LIBRARY_NAME.equals(libraryName)) {
			return true;
		}
		else {
			return super.libraryExists(libraryName);
		}
	}

	protected boolean areModulePathsExtensionsValid(ExternalContext externalContext) {

		List<String> modulePaths = getModulePaths(externalContext);
		boolean modulePathsExtensionsValid = false;

		if (modulePaths.size() > 0) {

			String[] comboAllowedFileExtensions = ResLibConfigParam.ComboAllowedFileExtensions.getStringValue(
					externalContext).split(",");
			modulePathsExtensionsValid = true;

			for (String modulePath : modulePaths) {

				boolean validFileExtension = false;

				for (String comboAllowedFileExtension : comboAllowedFileExtensions) {

					if (modulePath.endsWith(comboAllowedFileExtension)) {

						validFileExtension = true;

						break;
					}
				}

				if (!validFileExtension) {

					modulePathsExtensionsValid = false;

					break;
				}
			}
		}

		return modulePathsExtensionsValid;
	}

	protected List<String> getModulePaths(ExternalContext externalContext) {

		List<String> modulePaths = new ArrayList<String>();
		Iterator<String> requestParameterNames = externalContext.getRequestParameterNames();

		while (requestParameterNames.hasNext()) {

			String parameterName = requestParameterNames.next();

			if (parameterName.startsWith(ROOT_RESOURCE_DIRECTORY)) {
				modulePaths.add(parameterName);
			}
		}

		return modulePaths;
	}

	protected String getResourceName(ExternalContext externalContext) {

		// Attempt to get the resource name from the "javax.faces.resource" request parameter. If it exists, then
		// this is probably a non-Liferay portlet environment like Pluto.
		String resourceName = externalContext.getRequestParameterMap().get("javax.faces.resource");

		if (resourceName == null) {

			// If the specified request was extension-mapped (suffix-mapped), then determine the resource name based
			// on the configured mappings to the Faces Servlet.
			Object request = externalContext.getRequest();

			if (request instanceof HttpServletRequest) {
				HttpServletRequest httpServletRequest = (HttpServletRequest) request;
				String servletPath = httpServletRequest.getServletPath();

				if ((servletPath != null) && servletPath.startsWith(RESOURCE_IDENTIFIER)) {

					Map<String, Object> applicationMap = externalContext.getApplicationMap();
					String appConfigAttrName = ApplicationConfig.class.getName();
					ApplicationConfig applicationConfig = (ApplicationConfig) applicationMap.get(appConfigAttrName);
					FacesConfig facesConfig = applicationConfig.getFacesConfig();
					List<ConfiguredServletMapping> configuredFacesServletMappings =
						facesConfig.getConfiguredFacesServletMappings();

					resourceName = servletPath.substring(RESOURCE_IDENTIFIER.length() + 1);

					for (ConfiguredServletMapping configuredFacesServletMapping : configuredFacesServletMappings) {

						String configuredExtension = configuredFacesServletMapping.getExtension();

						if (servletPath.endsWith(configuredExtension)) {
							resourceName = resourceName.substring(0,
									resourceName.length() - configuredExtension.length());

							break;
						}
					}
				}

				// Otherwise, it must be path-mapped.
				else {
					resourceName = httpServletRequest.getPathInfo();
				}
			}
		}

		return resourceName;
	}
}
