package cn.edu.scau.cmi.oop.watch;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author TESLA_CN
 *
 */
public class Watch {
	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;
	private final boolean recursive;
	private boolean trace = false;

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * 
	 */
	public Watch(Path dir, boolean recursive) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();
		this.recursive = recursive;

		if (recursive) {
			System.out.format("Scanning %s ...\n", dir);
			registerAll(dir);
			System.out.println("Done.");
		} else {
			register(dir);
		}
		this.trace = true;
	}

	/**
	 * Register specific path
	 */
	private void register(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		if (trace) {
			Path existing = keys.get(key);
			if (existing == null) {
				System.out.format("register: %s\n", dir);
			} else {
				if (!dir.equals(existing)) {
					callbackRename(dir, existing);
				}
			}
		}
		keys.put(key, dir);
	}// TODO Handle update event

	/*
	 * Created: /Users/TESLA_CN/workspace/aa/d/f/e/f/a/s/f/a/v/c/s/q/q/bsdf
	 * update: /Users/TESLA_CN/workspace/aa/d/f/e/f/a/s/f/a/v/c/s/q/q/asdf ->
	 * /Users/TESLA_CN/workspace/aa/d/f/e/f/a/s/f/a/v/c/s/q/q/bsdf Deleted:
	 * /Users/TESLA_CN/workspace/aa/d/f/e/f/a/s/f/a/v/c/s/q/q/asdf
	 */

	/**
	 * Register specific path and its child
	 */
	private void registerAll(final Path start) throws IOException {

		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	public void start() {
		for (;;) {
			// Waiting
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}
			Path path = keys.get(key);
			if (path == null) {
				continue;
			}
			for (WatchEvent<?> event : key.pollEvents()) {
				Kind<?> kind = event.kind();
				if (kind == OVERFLOW) {
					continue;
				}
				// Resolve path to file absolute path
				WatchEvent<Path> evt = cast(event);
				Path name = evt.context();
				Path child = path.resolve(name);

				// TODO Callback
				switch (event.kind().name()) {
				case "ENTRY_CREATE":
					callbackEntryCreated(child);
					break;
				case "ENTRY_MODIFY":
					callbackEntryModified(child);
					break;
				case "ENTRY_DELETE":
					callbackEntryDeleted(child);
					break;
				default:

				}
				// System.out.format(new SimpleDateFormat("yyyy-MM-dd
				// hh:mm:ss").format(new Date()) + " %s|%s\n",
				// event.kind().name(), child);

				// Register path recursively
				if (recursive && (kind == ENTRY_CREATE)) {
					try {
						if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
							registerAll(child);
						}
					} catch (IOException x) {
					}
				}
			}
			// Reset key
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);
				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}

	protected void callbackEntryCreated(Path target) {
		System.out.println("Created: " + target);
	}

	protected void callbackEntryModified(Path target) {
		System.out.println("Modified: " + target);
	}

	protected void callbackEntryDeleted(Path target) {
		System.out.println("Deleted: " + target);
	}

	protected void callbackRename(Path current, Path old) {

		System.out.format("update: %s -> %s\n", current, old);
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Path dir = Paths.get("/Users/TESLA_CN/workspace/T");
		new Watch(dir, true).start();
	}

}
