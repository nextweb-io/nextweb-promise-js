package io.nextweb.promise.js;

import delight.functional.Closure2;
import delight.functional.Success;

import org.timepedia.exporter.client.ExporterUtil;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import io.nextweb.promise.Fn;
import io.nextweb.promise.exceptions.ExceptionListener;
import io.nextweb.promise.js.internal.JsCallback;
import io.nextweb.promise.js.types.JsArray;
import io.nextweb.promise.js.wrapping.JsWrap;
import io.nextweb.promise.js.wrapping.WrapperCollection;

public class FnJs {

    public static final boolean isJsString(final Object value) {
        return value instanceof String;
    }

    public static final boolean isJsInteger(final Object value) {
        return value instanceof Integer || value instanceof Short || value instanceof Long || value instanceof Byte;
    }

    public static final boolean isJsDouble(final Object value) {
        return value instanceof Float || value instanceof Double;
    }

    public static final boolean isJsBoolean(final Object value) {
        return value instanceof Boolean;
    }

    public static native final boolean isJsFunction(JavaScriptObject obj)/*-{
                                                                         return typeof(obj) === 'function';
                                                                         
                                                                         }-*/;

    public static final JavaScriptObject asJsCallback(final Closure2<Object, Object> closure) {
        return createCallback(ExporterUtil.wrap(JsCallback.wrap(closure)));
    }

    private final static native JavaScriptObject createCallback(JavaScriptObject callback)/*-{
                                                                                             return function(param1, param2) {
                                                                                                 callback.perform(param1, param2);
                                                                                             };
                                                                                             }-*/;

    public static final boolean isBasicJsType(final Object node) {
        return isJsString(node) || isJsInteger(node) || isJsDouble(node) || isJsBoolean(node);
    }

    public static final native void triggerCallbackJs(JavaScriptObject fn, JavaScriptObject jsArray)/*-{
                                                                                                    fn.apply(this, jsArray.getArray());
                                                                                                    }-*/;

    /**
     * <p>
     * Triggers the passed JavaScript object as a function.
     * <p>
     * The parameter must already be an object passable to JavaScript (e.g.
     * JavaScriptObject or a basic type).
     * 
     * @param fn
     * @return
     */
    public static final JsClosure asJsClosure(final JavaScriptObject fn, final ExceptionListener listener) {

        return new JsClosure() {

            @Override
            public void apply(final Object result) {

                try {

                    assert result instanceof JavaScriptObject;

                    triggerSimpleCallbackJs(fn, result);
                } catch (final Throwable t) {
                    // IMPORTANT
                    // Somehow JS closures seem to 'eat up' exceptions
                    // UPDATE: this might not be the case after all..

                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                        @Override
                        public void execute() {

                            // throw new RuntimeException(t);
                            listener.onFailure(Fn.exception(this, t));
                        }
                    });

                }

            }
        };

    }

    public static final native void triggerSimpleCallbackJs(JavaScriptObject fn, Object param1, Object param2)/*-{
                                                                                                              fn(param1, param2);
                                                                                                              }-*/;

    public static final Closure2<Object, Object> asClosure2(final JavaScriptObject fn) {

        return new Closure2<Object, Object>() {

            @Override
            public void apply(final Object param1, final Object param2) {

                assert param1 instanceof JavaScriptObject;
                assert param2 instanceof JavaScriptObject;

                triggerSimpleCallbackJs(fn, param1, param2);

                // triggerCallback(fn, wrappers, new Object[] { result });

            }
        };

    }

    public static final native void triggerSimpleCallbackJs(JavaScriptObject fn, Object param)/*-{
                                                                                              fn(param);
                                                                                              }-*/;

    public static final void triggerCallback(final JavaScriptObject fn, final WrapperCollection wrappers,
            final Object[] params) {
        triggerCallbackJs(fn, ExporterUtil.wrap(JsArray.wrap(JsWrap.toJsoArray(params, wrappers))));
    }

    public static final Success success() {
        return Success.INSTANCE;
    }

}
