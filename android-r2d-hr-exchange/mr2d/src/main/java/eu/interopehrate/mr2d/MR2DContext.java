/**
    Copyright 2021 Engineering S.p.A. (www.eng.it) - InteropEHRate PROJECT

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
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
import eu.interopehrate.mr2da.r2d.document.DocumentQueryGeneratorFactory;
import eu.interopehrate.mr2da.fhir.ConnectionFactory;
import eu.interopehrate.mr2da.r2d.resources.QueryGeneratorFactory;

/**
 *       Author: Engineering S.p.A. (www.eng.it)
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
            // FHIR Connection Factory init
            ConnectionFactory.initialize();
            // R2D Query Generator init
            QueryGeneratorFactory.initialize();
            // Document Query Generator init
            DocumentQueryGeneratorFactory.initialize();
            // Initializing NCP Node list
            // TODO: should be removed it is deprecated
            XmlResourceParser parser = getContext().getResources().getXml(R.xml.ncps);
            NCPRegistry.loadConfiguration(parser);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Fatal error while loading MR2DContext", e);
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
