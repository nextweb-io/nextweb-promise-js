package io.nextweb.fn.js.internal.callbacks;

import io.nextweb.fn.Closure;

import com.google.gwt.core.client.JavaScriptObject;

public class ClosureCallbackWrapper {

	private final Closure<Object> closure;
	

	public JavaScriptObject getJavaScriptCallback() {
		return createCallback();
	}

	private void callCallback(String param) {
		closure.apply(param);
	}

	private native JavaScriptObject createCallback()/*-{ 
													return function() {
													var self = this;
													var callbackFn = $entry(function(param) {
													self.@io.nextweb.fn.js.internal.callbacks.ClosureCallbackWrapper::callCallback(Ljava.lang.String)(param);
													});
													};
													return callbackFn;
													};
													}-*/;

	public ClosureCallbackWrapper(Closure<Object> closure) {
		super();
		this.closure = closure;
		
	}

}
