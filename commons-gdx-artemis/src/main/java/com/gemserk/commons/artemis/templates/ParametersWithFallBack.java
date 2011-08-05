package com.gemserk.commons.artemis.templates;

import java.util.HashMap;
import java.util.Map;

import com.gemserk.componentsengine.utils.Parameters;

/**
 * A Parameters implementation that tires to get object from other Parameters instance first, if they fail then it tries to load them from a fall back parameters map.
 * 
 * @author acoppes
 * 
 */
public class ParametersWithFallBack implements Parameters {

	private Map<String, Object> fallBackParameters;
	private Parameters parameters;

	/**
	 * Sets the Parameters instance to get items from.
	 * 
	 * @param parameters
	 */
	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	public ParametersWithFallBack() {
		fallBackParameters = new HashMap<String, Object>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String id) {
		Object o = parameters.get(id);
		if (o == null)
			return (T) fallBackParameters.get(id);
		return (T) o;
	}

	@Override
	public <T> T get(String id, T defaultValue) {
		T t = get(id);
		if (t == null)
			return defaultValue;
		return t;
	}

	@Override
	public void put(String id, Object value) {
		fallBackParameters.put(id, value);
	}

}