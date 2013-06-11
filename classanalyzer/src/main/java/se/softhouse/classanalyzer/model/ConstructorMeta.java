package se.softhouse.classanalyzer.model;

public class ConstructorMeta extends MethodMeta {

	public ConstructorMeta() {
		super("<constructor>");
	}
	
	@Override
	public boolean isConstructor() {
		return true;
	}
}
