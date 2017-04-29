package cn.edu.scau.cmi.oop.watch;

import java.nio.file.Path;

/**
 * 
 * @author TESLA_CN
 *
 */
public class ChangeItem {

	private ChangeKind type;

	private Path path;

	private Path previous;

	public ChangeItem(ChangeKind type, Path path) {
		this(type, path, null);
	}

	public ChangeItem(ChangeKind type, Path path, Path previous) {
		this.type = type;
		this.path = path;
		this.previous = previous;

	}

	/**
	 * @return the previous
	 */
	public Path getPrevious() {
		return previous;
	}

	/**
	 * @return the type
	 */
	public ChangeKind getType() {
		return type;
	}

	/**
	 * @return the path
	 */
	public Path getPath() {
		return path;
	}

}
