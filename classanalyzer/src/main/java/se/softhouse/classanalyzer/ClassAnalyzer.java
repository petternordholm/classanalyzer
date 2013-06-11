package se.softhouse.classanalyzer;

import java.io.File;
import java.util.List;

import se.softhouse.classanalyzer.javac.JavacClassAnalyzer;
import se.softhouse.classanalyzer.model.ClassMeta;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;


public class ClassAnalyzer 
{
	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			throw new IllegalArgumentException("Exactly one argument required: the root directory to search for source codes");
		}
		JavacClassAnalyzer analyzer = new JavacClassAnalyzer();
		File file = new File(args[0]);
		Preconditions.checkArgument(file.isFile(), "Argument %s must be a file", args[0]);
		List<ClassMeta> analyze = analyzer.analyze(Joiner.on("\n").join(Files.readLines(file,Charsets.UTF_8)));
		System.out.println(Joiner.on("\n").join(analyze));
	}
}
