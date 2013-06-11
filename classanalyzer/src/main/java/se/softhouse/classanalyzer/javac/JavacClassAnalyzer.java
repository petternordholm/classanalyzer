package se.softhouse.classanalyzer.javac;

import static java.util.Collections.singleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import se.softhouse.classanalyzer.model.ClassAnalyzer;
import se.softhouse.classanalyzer.model.ClassMeta;
import se.softhouse.classanalyzer.model.ConstructorMeta;
import se.softhouse.classanalyzer.model.MethodMeta;
import se.softhouse.classanalyzer.model.ModifierMeta;
import se.softhouse.classanalyzer.model.VariableMeta;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol.ClassSymbol;

/**
 * Implementation of ClassAnalyzer api using Javac TreeApi.
 */
@SuppressWarnings("restriction")
public class JavacClassAnalyzer implements ClassAnalyzer {

	private static class NullDiagnosticListener implements DiagnosticListener<JavaFileObject>{

		public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
		}		
	}
	
	private static class CodeAnalyzerTreeVisitor extends TreePathScanner<Object, Trees> {
		
		public Map<String, ClassMeta> classes = new HashMap<String, ClassMeta>();
		private ClassMeta currentClass = null;
		private Set<String> imports = Sets.newHashSet();
		
		public void newCompilationUnit() {
			imports.clear();
		}
				
		@Override
		public Object visitErroneous(ErroneousTree erroneousTree, Trees trees) {
			return super.visitErroneous(erroneousTree, trees);
		}
				
		@Override
		public Object visitImport(ImportTree importTree, Trees trees) {
			imports.add(importTree.getQualifiedIdentifier().toString());
			return super.visitImport(importTree, trees);
		}
		@Override
		public Object visitClass(ClassTree classTree, Trees trees) {
			currentClass  = new ClassMeta(getFQClassName(trees));
			classes.put(currentClass.getClassName(), currentClass);
			
			Set<Modifier> flags = classTree.getModifiers().getFlags();
			currentClass.setModifiers(flagsToModifierMeta(flags));
			
			Kind kind = classTree.getKind();
			currentClass.setIsInterface(kind==Kind.INTERFACE);
			
			return super.visitClass(classTree, trees);
		}
		
		private String getFQClassName(Trees trees) {
			TreePath current = getCurrentPath();
			String path = "";
			Element element = trees.getElement(current);
			if (element != null) {
				path = ((ClassSymbol) element).getQualifiedName().toString();
			} else {
				throw new IllegalStateException("Incomplete class " + current.getCompilationUnit().getSourceFile().getName());
			}
				
			return path;
		}
		
		@Override
		public Object visitVariable(VariableTree variableTree, Trees trees) {
			// We only care about members in the class.
			if(trees.getElement(getCurrentPath().getParentPath()) instanceof ClassSymbol) {
				VariableMeta variable = new VariableMeta(variableTree.getName().toString(),getFQType(variableTree.getType()));
				variable.setModifiers(flagsToModifierMeta(variableTree.getModifiers().getFlags()));
				currentClass.addMember(variable);
			}
			return super.visitVariable(variableTree, trees);
		}
		
		@Override
		public Object visitMethod(MethodTree methodTree, Trees trees) {
			String methodName = methodTree.getName().toString();
			MethodMeta methodMeta;
			if(methodName.equals("<init>")) {
				ConstructorMeta constructorMeta = new ConstructorMeta();
				currentClass.addConstructor(constructorMeta);
				methodMeta = constructorMeta;
			}
			else {
				methodMeta = new MethodMeta(methodName);
				currentClass.addMethod(methodMeta);
			}
			
			methodMeta.setReturnType(getFQType(methodTree.getReturnType()));
			
			Set<Modifier> flags = methodTree.getModifiers().getFlags();
			methodMeta.setModifiers(flagsToModifierMeta(flags));
			
			for(VariableTree parameter : methodTree.getParameters()) {
				VariableMeta variable = new VariableMeta(parameter.getName().toString(),getFQType(parameter.getType()));
				methodMeta.addParameter(variable);
				
			}
			return super.visitMethod(methodTree, trees);
		}

		private Set<ModifierMeta> flagsToModifierMeta(Set<Modifier> flags) {
			Set<ModifierMeta> modifiers = Sets.newHashSet();
			if(flags.contains(Modifier.PRIVATE))modifiers.add(ModifierMeta.Private);
			if(flags.contains(Modifier.PUBLIC))modifiers.add(ModifierMeta.Public);
			if(flags.contains(Modifier.PROTECTED))modifiers.add(ModifierMeta.Protected);
			if(flags.contains(Modifier.STATIC))modifiers.add(ModifierMeta.Static);
			if(flags.contains(Modifier.FINAL))modifiers.add(ModifierMeta.Final);
			if(flags.contains(Modifier.ABSTRACT))modifiers.add(ModifierMeta.Abstract);
			return modifiers;
		}

		private String getFQType(Tree type) {
			if(type == null) return "void";
			String returnName = type.toString();
			Pattern p = Pattern.compile("([^,<>]+)");
			Matcher m = p.matcher(returnName);
			StringBuffer fqName = new StringBuffer();
			while(m.find()) {
				m.appendReplacement(fqName, getFQName(m.group(1)));
			}
			m.appendTail(fqName);
			return fqName.toString();
		}
		
		private String getFQName(final String typeName) {
			return FluentIterable.from(imports).firstMatch(new Predicate<String>() {
				public boolean apply(String importName) {
					return importName.endsWith("."+typeName);
				};
			} ).or(typeName);
		}
	}
	

	@SupportedSourceVersion(SourceVersion.RELEASE_7)
	@SupportedAnnotationTypes("*")
	private static class CodeAnalyzerProcessor extends AbstractProcessor {

		private final CodeAnalyzerTreeVisitor visitor = new CodeAnalyzerTreeVisitor();
		private Trees trees;

		@Override
		public void init(ProcessingEnvironment pe) {
			super.init(pe);
			trees = Trees.instance(pe);
		}

		@Override
		public boolean process(Set<? extends TypeElement> annotations,
				RoundEnvironment roundEnv) {
			for (Element e : roundEnv.getRootElements()) {
				TreePath tp = trees.getPath(e);
				if(tp == null)continue;
				CompilationUnitTree compilationUnit = tp.getCompilationUnit();
				// invoke the scanner
				visitor.newCompilationUnit();
				visitor.scan(compilationUnit, trees);
			}
			return true;    // handled, don't invoke other processors
		}
	}
			
	public List<ClassMeta> analyze(final String javaCode) {
		
		//Get an instance of java compiler
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		//Get a new instance of the standard file manager implementation
		StandardJavaFileManager fileManager = compiler.
				getStandardFileManager(null, null, null);

		JavaFileObject fileObject = new JavacSourceCodeObject(javaCode);
		
		// Create the compilation task
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, new NullDiagnosticListener(), null, null, ImmutableList.of(fileObject));

		// Set the annotation processor to the compiler task
		CodeAnalyzerProcessor processor = new CodeAnalyzerProcessor();
		task.setProcessors(singleton(processor));

		// Perform the compilation task.
		task.call();
		return Lists.newArrayList(processor.visitor.classes.values());				
	}

}
