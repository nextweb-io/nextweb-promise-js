package io.nextweb.promise.js.callbacks;

import delight.async.callbacks.SimpleCallback;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExporterUtil;
import org.timepedia.exporter.client.NoExport;

import com.google.gwt.core.client.JavaScriptObject;

import io.nextweb.promise.js.exceptions.ExceptionUtils;

@Export
public class JsSimpleCallbackWrapper implements Exportable {

    @NoExport
    SimpleCallback wrapped;

    @NoExport
    public final static JavaScriptObject wrap(final SimpleCallback callback) {
        final JsSimpleCallbackWrapper wrapper = new JsSimpleCallbackWrapper();
        wrapper.wrapped = callback;
        return ExporterUtil.wrap(wrapper);
    }

    @Export
    public void onFailure(final JavaScriptObject t) {

        wrapped.onFailure(ExceptionUtils.convertToJavaException(t));
    }

    @Export
    public void onSuccess() {
        wrapped.onSuccess();
    }

    public JsSimpleCallbackWrapper() {
        super();
    }

}
