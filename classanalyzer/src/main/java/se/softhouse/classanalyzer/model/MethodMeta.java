package se.softhouse.classanalyzer.model;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class MethodMeta extends FlagsMeta {

	private String methodName;
	private String returnType;
	private List<VariableMeta> parameters = Lists.newArrayList();
	
	public MethodMeta(String methodName) {
		this.methodName = methodName;
	}

	public boolean isConstructor() {
		return false;
	}
	
	@Override
	public String toString() {
		return modifiersToString() + " " + returnType+":"+ methodName+"(" + Joiner.on(",").join(parameters)+")";
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getMethodName() {
		return methodName;
	}
	
	public List<VariableMeta> getParameters() {
		return parameters;
	}
	
	public void addParameter(VariableMeta variable) {
		this.parameters.add(variable);
	}
	
	public String getReturnType() {
		return returnType;
	}
}
