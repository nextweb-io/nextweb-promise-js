package io.nextweb.promise.js.exceptions;

import io.nextweb.promise.exceptions.ExceptionResult;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.UmbrellaException;

public class ExceptionUtils {

	public static final Throwable convertJavaScriptException(Object exception) {
		if (exception instanceof Throwable) {
			return (Throwable) exception;
		}

		return new Exception("JavaScriptException: '" + exception.toString()
				+ "' of class '" + exception.getClass() + "'");
	}

	public static final void triggerExceptionCallback(
			final JavaScriptObject callback, final ExceptionResult r) {
		triggerFailureCallbackJs(callback, r.origin().getClass().toString(),
				unwrap(r.exception()).getMessage(),
				getStacktrace(r.exception()), getOriginTrace(), getJsException(r.exception()));
	}

	public static final JavaScriptObject wrapExceptionResult(
			final ExceptionResult r) {

		return (ExceptionUtils.wrapExceptionResult(r.origin().getClass()
				.toString(), unwrap(r.exception()).getMessage(),
				getStacktrace(r.exception()), getOriginTrace(), getJsException(r.exception())));
	}

	private final static JavaScriptObject getJsException(Throwable ex) {
		Throwable uw = unwrap(ex);
		if (!(uw instanceof JavaScriptException)) {
			return null;
		}
		
		return ((JavaScriptException) uw).getException();
		
		
	}
	
	public static final String getStacktrace(final Throwable r) {
		Throwable unwrapped = unwrap(r);
		String stacktrace;
		try {
			stacktrace = unwrapped.toString() + "<br />\n";
		} catch (Throwable t) {
			stacktrace = "Error creating stacktrace for " + r.getMessage()
					+ ".\n  Exception reported: " + t.getMessage();

		}

		if (unwrapped instanceof JavaScriptException) {
			JavaScriptException jsException = (JavaScriptException) unwrapped;
			if (jsException.getException() != null) {
				stacktrace += "JavaScriptException:<br/>\n"
						+ getJavaScriptExceptionStackTrace(
								jsException.getException()).replaceAll("\n",
								"<br/>\n")
						+ "<br/>\n-- End of JavaScriptException";
			}

		}

		for (final StackTraceElement element : unwrapped.getStackTrace()) {
			stacktrace += element + "<br/>\n";
		}

		stacktrace += getCauseTrace(unwrap(r));
		return stacktrace;
	}

	private static native final String getJavaScriptExceptionStackTrace(
			JavaScriptObject ex)/*-{
								if (!ex.stack) {
								return "JavaScriptException stack trace not available for this browser";
								}
								return ex.stack;
								}-*/;

	private static final String getOriginTrace() {
		try {
			throw new Exception("Origin");
		} catch (Throwable t) {
			return getStacktrace(t);
		}

	}

	private static final Throwable unwrap(final Throwable e) {
		if (e instanceof UmbrellaException) {
			final UmbrellaException ue = (UmbrellaException) e;
			if (ue.getCauses().size() == 1) {
				return unwrap(ue.getCauses().iterator().next());
			}
		}
		return e;
	}

	private static final String getCauseTrace(final Throwable t) {
		if (t.getCause() == null) {
			return "-end of stack trace";
		}

		String res = "Caused By: " + unwrap(t.getCause()).toString()
				+ "<br/>\n";
		for (final StackTraceElement element : unwrap(t.getCause())
				.getStackTrace()) {
			res += element + "<br/>\n";
		}

		return res;
	}

	private static final native JavaScriptObject triggerFailureCallbackJs(
			JavaScriptObject callback, String origin, String exceptionMessage,
			String stacktrace, String originTrace, JavaScriptObject jsException)/*-{
													callback({
													exception: exceptionMessage,
													origin: origin,
													origintrace: originTrace,
													stacktrace: stacktrace,
													jsException: jsException
													});
													}-*/;

	private static final native JavaScriptObject wrapExceptionResult(
			String origin, String exceptionMessage, String stacktrace,
			String originTrace, JavaScriptObject jsException)/*-{
								return {
								exception: exceptionMessage,
								origin: origin,
								origintrace: originTrace,
								stacktrace: stacktrace,
								jsException: jsException
								}
								}-*/;

}