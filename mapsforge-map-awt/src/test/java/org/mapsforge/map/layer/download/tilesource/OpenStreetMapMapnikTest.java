/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 * Copyright 2014 Ludwig M Brinckmann
 * Copyright 2018 devemux86
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
package org.mapsforge.map.layer.download.tilesource;

import org.junit.Assert;
import org.junit.Test;
import org.mapsforge.core.model.Tile;

import java.net.MalformedURLException;
import java.net.URL;

public class OpenStreetMapMapnikTest {
    @Test
    public void getParallelRequestsLimitTest() {
        TileSource tileSource = OpenStreetMapMapnik.INSTANCE;
        Assert.assertTrue(tileSource.getParallelRequestsLimit() > 0);
    }

    @Test
    public void getTileUrlTest() throws MalformedURLException {
        TileSource tileSource = OpenStreetMapMapnik.INSTANCE;

        URL tileUrl = tileSource.getTileUrl(new Tile(0, 1, (byte) 2, 256));
        Assert.assertTrue(tileUrl.toExternalForm().endsWith("tile.openstreetmap.org:443/2/0/1.png"));
    }

    @Test
    public void getZoomLevelTest() {
        TileSource tileSource = OpenStreetMapMapnik.INSTANCE;
        Assert.assertTrue(tileSource.getZoomLevelMin() < tileSource.getZoomLevelMax());
    }
}
