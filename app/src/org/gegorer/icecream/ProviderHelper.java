package org.gegorer.icecream;

import android.net.Uri;

public final class ProviderHelper {
    public static final String AUTHORITY = "org.gegorer.icecream";
    public static final String FAMI_TABLE = "fami";
    public static final Uri FAMI_URI = Uri.parse("content://" + AUTHORITY + "/" + FAMI_TABLE);

    public static final int NAME_IDX = 1;
    public static final int ADDR_IDX = 2;
    public static final String INFO_TABLE_NAME = "info";
    public static final String SHOPS_TABLE_NAME = "shops";
    public static final String INFO_FIELD_VERSION = "version";
    public static final String SHOPS_FIELD_SERID = "SERID";
    public static final String SHOPS_FIELD_PX = "px";
    public static final String SHOPS_FIELD_PY = "py";
    public static final String SHOPS_FIELD_ADDR = "addr";
    public static final String SHOPS_FIELD_NAME = "NAME";
    public static final String SHOPS_FIELD_ICECREAM = "icecream";
};
