package org.gegorer.icecream;

import android.net.Uri;

public final class ProviderHelper {
    public static final String AUTHORITY = "org.gegorer.icecream";
    public static final String FAMI_TABLE = "fami";
    public static final Uri FAMI_URI = Uri.parse("content://" + AUTHORITY + "/" + FAMI_TABLE);

    public static final int NAME_IDX = 1;
    public static final int ADDR_IDX = 2;
};
