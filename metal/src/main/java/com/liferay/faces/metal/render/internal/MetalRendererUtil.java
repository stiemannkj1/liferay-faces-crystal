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

import java.util.Set;
import java.util.TreeSet;

import java.util.Collections;
import java.util.HashSet;


/**
 * @author  Kyle Stiemann
 */
public class MetalRendererUtil {

	public static String getMetalBeginScript(String[] modules) {

		Set<String> sortedModules = null;

		if (modules != null) {

			sortedModules = new TreeSet<String>();

			for (String module : modules) {
				sortedModules.add(module.trim());
			}
		}

		return getMetalBeginScript(sortedModules);
	}

	public static String getMetalBeginScript(Set<String> sortedModules) {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("require(['");
		boolean first = true;

		for (String module : sortedModules) {

			if (!first) {
				stringBuilder.append("','");
			}

			stringBuilder.append(module);
			first = false;
		}

		stringBuilder.append("'], function(");

		Set<String> variableNames = new HashSet<String>(sortedModules);
		first = true;

		for (String module : sortedModules) {

			if (!first) {
				stringBuilder.append(",");
			}

			String moduleVariable = generateVariableName(module, Collections.unmodifiableSet(variableNames));
			variableNames.add(moduleVariable);
			stringBuilder.append(moduleVariable);
		}

		stringBuilder.append("){");

		return stringBuilder.toString();
	}

	private static String generateVariableName(String module, Set<String> variableNames) {

		String variableName = module;

		if ("metal/src/metal".equals(module)) {
			variableName = "metal";
		}
		else {	
			StringBuilder sb = new StringBuilder(module.length());

			char c = module.charAt(0);

			boolean modified = true;

			if (('a' <= c) && (c <= 'z') ||
				(c == '_')) {

				sb.append(c);

				modified = false;
			}
			else if (('A' <= c) && (c <= 'Z')) {
				sb.append((char)(c + 32));
			}
			else {
				sb.append('_');
			}

			boolean startNewWord = false;

			for (int i = 1; i < module.length(); i++) {
				c = module.charAt(i);

				if (('a' <= c) && (c <= 'z')) {
					if (startNewWord) {
						sb.append((char)(c - 32));

						startNewWord = false;
					}
					else {
						sb.append(c);
					}
				}
				else if (('A' <= c) &&
						 (c <= 'Z') ||
						 (('0' <= c) && (c <= '9')) ||
						 (c == '_')) {

					sb.append(c);

					startNewWord = false;
				}
				else {
					modified = true;

					startNewWord = true;
				}
			}

			String safeName = module;

			if (modified) {
				safeName = sb.toString();

				variableName = safeName;
			}

			int i = 1;

			while (variableNames.contains(variableName)) {
				variableName = safeName.concat(String.valueOf(i++));
			}

		}
		
		return variableName;
	}
}
