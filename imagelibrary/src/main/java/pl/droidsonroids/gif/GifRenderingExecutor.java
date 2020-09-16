package pl.droidsonroids.gif;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Default executor for rendering tasks - {@link ScheduledThreadPoolExecutor}
 * with 1 worker thread and {@link DiscardPolicy}.
 */
final class GifRenderingExecutor extends ScheduledThreadPoolExecutor {

	// Lazy initialization via inner-class holder
	private static final class InstanceHolder {
		private static final GifRenderingExecutor INSTANCE = new GifRenderingExecutor();
	}

	static GifRenderingExecutor getInstance() {
		return InstanceHolder.INSTANCE;
	}

	private GifRenderingExecutor() {
		super(1, new DiscardPolicy());
	}
}
