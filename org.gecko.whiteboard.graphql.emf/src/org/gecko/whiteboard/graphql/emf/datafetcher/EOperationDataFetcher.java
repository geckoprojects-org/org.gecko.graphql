package org.gecko.whiteboard.graphql.emf.datafetcher;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.util.EcoreUtil;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class EOperationDataFetcher implements DataFetcher<Object> {

	private final EOperation operation;
	private EObject rootObject = null;

	public EOperationDataFetcher(EOperation operation, EObject rootObject) {
		this.operation = operation;
		this.rootObject = rootObject;
	}

	public EOperationDataFetcher(EOperation operation) {
		this.operation = operation;
	}

	@Override
	public Object get(DataFetchingEnvironment environment) {
		EObject context = rootObject; 
		if (environment.getSource() instanceof EObject) {
			context = (EObject) environment.getSource();
		}
		try {
			EList<Object> arguments = new BasicEList<>();
			for (EParameter parameter : operation.getEParameters()) {
				Object arg = environment.getArgument(parameter.getName());
				arguments.add(convertArgument(arg, parameter.getEType(), context));
			}
			Object value = context.eInvoke(operation, arguments);
			return (value == null && operation.getEType() == null ? context : value);
		} catch (Exception e) {
			return null;
		}
	}

	protected Object convertArgument(Object arg, EClassifier type, EObject context) {
		if(arg == null) {
			return null;
		}
		if (type.isInstance(arg)) {
			return arg;
		}
		if (type instanceof EDataType) {
			String argString = String.valueOf(arg);
			return EcoreUtil.createFromString((EDataType) type, argString);
		}
		return arg;
	}
}
