package strategy;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingDeque;

import modules.ServerUtil;
import modules.LocalUtil;
import watch.ChangeItem;
import watch.ChangeKind;
import watch.Watch;

/**
 * @author TESLA_CN
 */
public class RealTimeSync extends Watch implements Runnable {

	private LinkedBlockingDeque<ChangeItem> changeQueue;

	private ServerUtil host;

	private Path targetRoot;

	public static final long DEFAULT_SLEEP_MILLIS = 30 * 1000; // Check the
																// queue of
	// change items every 30
	// seconds
	private long sleepMillis;

	private Thread running;

	private Runnable checkout;

	public RealTimeSync(String targetRoot, boolean recursive, ServerUtil host) throws IOException {
		this(Paths.get(targetRoot), recursive, host, DEFAULT_SLEEP_MILLIS);
	}

	public RealTimeSync(Path targetRoot, boolean recursive, ServerUtil host) throws IOException {
		this(targetRoot, recursive, host, DEFAULT_SLEEP_MILLIS);
	}

	public RealTimeSync(String targetRoot, boolean recursive, ServerUtil host, long sleepMillis) throws IOException {
		this(Paths.get(targetRoot), recursive, host, sleepMillis);
	}

	public RealTimeSync(Path targetRoot, boolean recursive, ServerUtil host, long sleepMillis) throws IOException {
		super(targetRoot, recursive);
		this.host = host;
		this.targetRoot = targetRoot;
		this.sleepMillis = sleepMillis;
		changeQueue = new LinkedBlockingDeque<>();

		checkout = () -> {
			try {
				synchronizeWholeDirectory();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			System.out.println("Synchronize Thread started");
			for (;;) {
				System.out.println(new Date().toString() + "Synchronizing changes...");
				while (!changeQueue.isEmpty()) {
					ChangeItem item = changeQueue.pollFirst();
					ChangeKind kind = item.getType();

					switch (kind) {
					case CREATE:
						try {
							host.upload(item.getPath().toString(), item.getPath().toString());
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					case MODIFY:
						try {
							System.out.println(item.getPath().toString() + item.getPath().toFile().isDirectory());
							if (!item.getPath().toFile().isDirectory()) {
								host.upload(item.getPath().toString(), item.getPath().toString());
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						break;
					case DELETE:
						try {
							host.deleteFile(item.getPath().toString());
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					case RENAME:
						try {
							host.rename(item.getPrevious().toString(), item.getPath().toString());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;

					}
				}

				try {
					Thread.sleep(this.sleepMillis);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.err.println("Thread interrupted");
					break;
				}
			}
			System.err.println("Thread checkout ended");
		};
		running = new Thread(checkout);
		running.start();
	}

	public void restart() {
		running.interrupt();
		running = new Thread(checkout);
		running.start();
		System.out.println("Thread restarted");
	}

	public void synchronizeWholeDirectory() throws IOException {
		System.out.println("Initial sychronizing...");
		Files.walkFileTree(targetRoot, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				host.createDir(dir.toString());
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				host.upload(file.toString(), file.toString());
				return FileVisitResult.CONTINUE;
			}
		});
	}

	@Override
	protected void callbackEntryCreated(Path target) {
		super.callbackEntryCreated(target);
		changeQueue.offerLast(new ChangeItem(ChangeKind.CREATE, target));
	}

	@Override
	protected void callbackEntryModified(Path target) {
		super.callbackEntryModified(target);
		changeQueue.offerLast(new ChangeItem(ChangeKind.MODIFY, target));
	}

	@Override
	protected void callbackEntryDeleted(Path target) {
		super.callbackEntryDeleted(target);
		ChangeItem previous = null;
		try {
			previous = changeQueue.getLast();
		} catch (NoSuchElementException e) {
			return;
		}
		if (previous.getType().equals(ChangeKind.RENAME) && previous.getPrevious().equals(target)) {

		}
		changeQueue.offerLast(new ChangeItem(ChangeKind.DELETE, target));
	}

	/**
	 * @return the sleep millis
	 */
	public long getSleepMillis() {
		return sleepMillis;
	}

	/**
	 * @param sleepMillis
	 *            the sleep millis to set
	 */
	public void setSleepMillis(long sleepMillis) {
		this.sleepMillis = sleepMillis;
		System.out.println("Set sleep millis: " + sleepMillis);
	}

	@Override
	protected void callbackRename(Path current, Path old) {
		super.callbackRename(current, old);
		ChangeItem previous = changeQueue.getLast();
		if (previous.getType() == ChangeKind.CREATE && previous.getPath().equals(current)) {
			changeQueue.pollLast();
		}
		changeQueue.offerLast(new ChangeItem(ChangeKind.RENAME, current, old));
	}

	public static void main(String[] args) throws IOException {
		RealTimeSync watch = new RealTimeSync(Paths.get("D:\\STUDY\\JAVA\\abc"), true
				, new LocalUtil("D:\\11111"));
		new Thread(watch).start();
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		watch.setSleepMillis(5000);
		watch.restart();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		start();
	}
}
