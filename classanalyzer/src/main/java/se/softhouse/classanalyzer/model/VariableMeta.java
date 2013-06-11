package se.softhouse.classanalyzer.model;


public class VariableMeta extends FlagsMeta {

	private String variableName;
	private String variableType;

	public VariableMeta(String variableName, String variableType) {
		this.variableName = variableName;
		this.variableType = variableType;
	}
	
	public String getVariableName() {
		return variableName;
	}
	
	public String getVariableType() {
		return variableType;
	}
	
	@Override
	public String toString() {
		return modifiersToString() + " " +variableType+":"+variableName ;
	}

}
