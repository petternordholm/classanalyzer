package se.softhouse.classanalyzer.model;

import java.util.ArrayList;
import java.util.List;


import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Contains metadata information regarding a parsed class.
 */
public class ClassMeta extends FlagsMeta {

	private String className;
	private List<MethodMeta> methods = new ArrayList<MethodMeta>();
	private List<ConstructorMeta> constructors = new ArrayList<ConstructorMeta>();
	private List<VariableMeta> members = new ArrayList<VariableMeta>();
	private boolean isInterface = false;
	
	public ClassMeta(String className) {
		this.className = className; 
	}

	public String getClassName() {
		return className;
	}

	public void addMethod(MethodMeta methodMeta) {
		methods.add(methodMeta);
	}

	public void addConstructor(ConstructorMeta methodMeta) {
		constructors.add(methodMeta);
	}
	
	public List<ConstructorMeta> getConstructors() {
		return constructors;
	}
	
	public List<MethodMeta> getMethods() {
		return methods;
	}
	

	
	public String toString() {
		return Joiner.on("\n").join(Lists.newArrayList(
				modifiersToString() + " " + (isInterface ? "interface": " class") + " " + className + " {",
				Joiner.on("\n").join(members),
				Joiner.on("\n").join(constructors),
				Joiner.on("\n").join(methods),
				"}"
				));
	}

	public void setIsInterface(boolean isInterface) {
		this.isInterface  = isInterface;		
	}

	public void addMember(VariableMeta variable) {
		members.add(variable);		
	}
}
