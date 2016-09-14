requirejs.config({
	baseUrl: "#{resource['liferay-faces-metal-reslib:require']}&metaljs/build/amd/"
});

Liferay = window.Liferay || {};
if (!Liferay.zIndex) {
	Liferay.zIndex = {
			DOCK: 10,
			DOCK_PARENT: 20,
			ALERT: 430,
			DROP_AREA: 440,
			DROP_POSITION: 450,
			DRAG_ITEM: 460,
			OVERLAY: 1000,
			WINDOW: 1200,
			MENU: 5000,
			TOOLTIP: 10000
	};
}

require(['metal/src/metal'], function(metal) {
	(function(metal, Liferay) {
		var components = {},
			componentsFn = {};

		Liferay.component = function(id, value) {
			var retVal,
				component;

			if (arguments.length === 1) {
				component = components[id];

				if (component && metal.core.isFunction(component)) {
					componentsFn[id] = component;

					component = component();

					components[id] = component;
				}

				retVal = component;
			}
			else {
				retVal = (components[id] = value);
			}

			return retVal;
		};

		Liferay._components = components;
		Liferay._componentsFn = components;
	})(metal, Liferay);
});