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
package com.liferay.faces.crystal.component.selectmanylistbox;
//J-

import javax.annotation.Generated;
import javax.faces.component.html.HtmlSelectManyListbox;

import com.liferay.faces.util.component.Styleable;


/**
 * @author	Bruno Basto
 * @author	Kyle Stiemann
 */
@Generated(value = "com.liferay.crystal.tools.builder.FacesBuilder")
public abstract class SelectManyListboxBase extends HtmlSelectManyListbox implements Styleable {

	// Public Constants
	public static final String COMPONENT_TYPE = "com.liferay.faces.crystal.component.selectmanylistbox.SelectManyListbox";
	public static final String RENDERER_TYPE = "com.liferay.faces.crystal.component.selectmanylistbox.SelectManyListboxRenderer";

	public SelectManyListboxBase() {
		super();
		setRendererType(RENDERER_TYPE);
	}

	/**
	 * <code>label</code> attribute description:
	 * <br /><br />
	 * A localized label for this component that is typically only rendered in a FacesMessage when validation fails.
	 */
	@Override
	public String getLabel() {

		String label = super.getLabel();

		if (label == null) {

			javax.faces.context.FacesContext facesContext = javax.faces.context.FacesContext.getCurrentInstance();

			if (facesContext.getCurrentPhaseId() == javax.faces.event.PhaseId.PROCESS_VALIDATIONS) {
				label = com.liferay.faces.util.component.ComponentUtil.getComponentLabel(this);
			}
		}

		return label;
	}

	/**
	 * <code>styleClass</code> attribute description:
	 * <br /><br />
	 * List of CSS class names (separated by spaces) that are to be rendered within the class attribute.
	 */
	@Override
	public String getStyleClass() {

		// getStateHelper().eval(PropertyKeys.styleClass, null) is called because
		// super.getStyleClass() may return the styleClass name of the super class.
		String styleClass = (String) getStateHelper().eval(PropertyKeys.styleClass, null);

		return com.liferay.faces.util.component.ComponentUtil.concatCssClasses(styleClass, "crystal-select-many-listbox");
	}
}
//J+
