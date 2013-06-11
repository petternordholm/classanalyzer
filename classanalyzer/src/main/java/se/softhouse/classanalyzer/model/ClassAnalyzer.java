package se.softhouse.classanalyzer.model;

import java.util.List;

/**
 * Interface for java class analyser.
 */
public interface ClassAnalyzer {
	
	/**
	 * Analyzes the given java code and returns a list with classes. The classes returned will be in order of occurrence in the given file.
	 * @param javaCode the javacode to analyze
	 * @return list of classes found 
	 */
	public List<ClassMeta> analyze(String javaCode);
}
