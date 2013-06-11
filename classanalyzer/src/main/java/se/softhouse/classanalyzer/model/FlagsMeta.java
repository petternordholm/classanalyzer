package se.softhouse.classanalyzer.model;

import java.util.List;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class FlagsMeta {

	private boolean isPublic;
	private boolean isPrivate;
	private boolean isProtected;
	private boolean isPackagePrivate;
	private boolean isStatic;
	private boolean isFinal;
	private boolean isAbstract;

	public void setModifiers(Set<ModifierMeta> modifiers) {
		isPublic = modifiers.contains(ModifierMeta.Public);
		isPrivate = modifiers.contains(ModifierMeta.Private);
		isProtected = modifiers.contains(ModifierMeta.Protected);		
		isPackagePrivate = !isPublic && !isPrivate && !isProtected;
		isStatic = modifiers.contains(ModifierMeta.Static);
		isFinal = modifiers.contains(ModifierMeta.Final);
		isAbstract = modifiers.contains(ModifierMeta.Abstract);
	}

	protected String modifiersToString() {
		List<String> modifiers = Lists.newArrayList();
		if(isAbstract)modifiers.add("abstract");
		if(isPrivate)modifiers.add("private");
		if(isProtected)modifiers.add("protected");
		if(isPublic)modifiers.add("public");
		if(isStatic)modifiers.add("static");
		if(isFinal)modifiers.add("final");
		return Joiner.on(' ').join(modifiers);
	}

	public boolean isPackagePrivate() {
		return isPackagePrivate;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public boolean isProtected() {
		return isProtected;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public boolean isFinal() {
		return isFinal;
	}

}
