package com.io.file;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import java.io.File;

public interface PathStrategy {
    /**
     * Return a {@link Uri} that represents the given {@link File}.
     */
    Uri getUriForFile(File file);

    /**
     * Return a {@link File} that represents the given {@link Uri}.
     */
    File getFileForUri(Uri uri);

    class PathStrategyBuilder {
        private Context context;

        public PathStrategyBuilder(Context context) {
            this.context = context;
        }

        public PathStrategy buid() {
            if (Build.VERSION.SDK_INT > 23)
                return new PathStrategy24(context);
            return new PathStrategy23(context);
        }
    }

}
