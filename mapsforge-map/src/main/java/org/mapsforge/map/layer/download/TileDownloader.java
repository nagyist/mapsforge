/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 * Copyright 2014 Ludwig M Brinckmann
 * Copyright 2016 devemux86
 * Copyright 2018 iPSAlex
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mapsforge.map.layer.download;

import org.mapsforge.core.graphics.CorruptedInputStreamException;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.TileBitmap;
import org.mapsforge.core.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

class TileDownloader {
    private static InputStream getInputStream(URLConnection urlConnection) throws IOException {
        InputStream inputStream = urlConnection.getInputStream();
        if ("gzip".equals(urlConnection.getContentEncoding())) {
            return new GZIPInputStream(inputStream);
        }
        return inputStream;
    }

    private final DownloadJob downloadJob;
    private final GraphicFactory graphicFactory;

    TileDownloader(DownloadJob downloadJob, GraphicFactory graphicFactory) {
        if (downloadJob == null) {
            throw new IllegalArgumentException("downloadJob must not be null");
        } else if (graphicFactory == null) {
            throw new IllegalArgumentException("graphicFactory must not be null");
        }

        this.downloadJob = downloadJob;
        this.graphicFactory = graphicFactory;
    }

    TileBitmap downloadImage() throws IOException {
        URL url = this.downloadJob.tileSource.getTileUrl(this.downloadJob.tile);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setConnectTimeout(this.downloadJob.tileSource.getTimeoutConnect());
        urlConnection.setReadTimeout(this.downloadJob.tileSource.getTimeoutRead());
        if (this.downloadJob.tileSource.getUserAgent() != null) {
            urlConnection.setRequestProperty("User-Agent", this.downloadJob.tileSource.getUserAgent());
        }
        if (this.downloadJob.tileSource.getReferer() != null) {
            urlConnection.setRequestProperty("Referer", this.downloadJob.tileSource.getReferer());
        }
        if (this.downloadJob.tileSource.getAuthorization() != null) {
            urlConnection.setRequestProperty("Authorization", this.downloadJob.tileSource.getAuthorization());
        }
        if (urlConnection instanceof HttpURLConnection) {
            ((HttpURLConnection) urlConnection).setInstanceFollowRedirects(this.downloadJob.tileSource.isFollowRedirects());
        }
        InputStream inputStream = getInputStream(urlConnection);

        try {
            TileBitmap result = this.graphicFactory.createTileBitmap(inputStream, this.downloadJob.tile.tileSize,
                    this.downloadJob.hasAlpha);
            result.setExpiration(urlConnection.getExpiration());
            return result;
        } catch (CorruptedInputStreamException e) {
            // the creation of the tile bit map can fail, at least on Android,
            // when the connection is slow or busy, returning null here ensures that
            // the tile will be downloaded again
            return null;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
