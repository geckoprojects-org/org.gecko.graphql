package org.gecko.whiteboard.graphql.instrumentation;

import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import graphql.servlet.InstrumentationProvider;

/**
 * 
 * @author jalbert
 * @since 5 Dec 2018
 */
public class TracingInstrumentationProvider implements InstrumentationProvider {
	@Override
	public Instrumentation getInstrumentation() {
		return new TracingInstrumentation();
	}
}