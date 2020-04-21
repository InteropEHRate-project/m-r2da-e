package eu.interopehrate.mr2d;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import eu.interopehrate.mr2d.ncp.NCPRegistry;
import eu.interopehrate.mr2d.R;

/**
 *       Author: Engineering Ingegneria Informatica
 *      Project: InteropEHRate - www.interopehrate.eu
 *
 *  Description: MR2DE Content Provider used to retrtieve context information
 *               from hosting app. Context information are used to access
 *               "res" folder.
 */
public final class MR2DContext extends ContentProvider {

    private static MR2DContext INSTANCE;

    public static Context getMR2DContext() {
        return INSTANCE.getContext();
    }

    public static Resources getMR2DResources() {
        return INSTANCE.getContext().getResources();
    }

    @Override
    public boolean onCreate() {
        INSTANCE = this;

        // Initialization procedures must be performed here
        try {
            Log.d(getClass().getName(), "Initializing MR2D library...");
            XmlResourceParser parser = getContext().getResources().getXml(R.xml.ncps);
            NCPRegistry.loadConfiguration(parser);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Fatal error while loading NCP descriptors", e);
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
