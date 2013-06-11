package se.softhouse.classanalyzer.javac;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;

import com.google.common.base.Throwables;

@SuppressWarnings("restriction")
public class JavacSourceCodeObject implements JavaFileObject {

	private String content;

	JavacSourceCodeObject(String content) {
		this.content = content;
	}
	
	public URI toUri() {
		try {
			return URI.create("file://"+File.createTempFile("javac", ".java").getCanonicalPath());
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	public String getName() {
		return "name";
	}

	public InputStream openInputStream() throws IOException {
		return new ByteArrayInputStream(new byte[0]);
	}

	public OutputStream openOutputStream() throws IOException {
		return new ByteArrayOutputStream();
	}

	public Reader openReader(boolean ignoreEncodingErrors)
			throws IOException {
		return null;
	}

	public CharSequence getCharContent(boolean ignoreEncodingErrors)
			throws IOException {
		return content;
	}

	public Writer openWriter() throws IOException {
		return null;
	}

	public long getLastModified() {
		return 0;
	}

	public boolean delete() {
		return false;
	}

	public Kind getKind() {
		return Kind.SOURCE;
	}

	public boolean isNameCompatible(String simpleName, Kind kind) {
		return true;
	}

	public NestingKind getNestingKind() {
		return null;
	}

	public Modifier getAccessLevel() {
		return null;
	}

}
