package strategy;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingDeque;

import watch.ChangeItem;
import watch.ChangeKind;
import watch.Watch;

public class IndependentWatch extends Watch implements Runnable {

	private LinkedBlockingDeque<ChangeItem> changeQueue;

	public IndependentWatch(String targetRoot, boolean recursive) throws IOException {
		this(Paths.get(targetRoot), recursive);
	}

	public IndependentWatch(Path targetRoot, boolean recursive) throws IOException {
		super(targetRoot, recursive);
		changeQueue = new LinkedBlockingDeque<>();
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
	 * @return the changeQueue
	 */
	public LinkedBlockingDeque<ChangeItem> getChangeQueue() {
		return changeQueue;
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
		IndependentWatch watch = new IndependentWatch(Paths.get("/Users/TESLA_CN/workspace"), true);
		new Thread(watch).start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		start();
	}

}
