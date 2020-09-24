/*
 * #%L
 * BigDataViewer core classes with minimal dependencies
 * %%
 * Copyright (C) 2012 - 2016 Tobias Pietzsch, Stephan Saalfeld, Stephan Preibisch,
 * Jean-Yves Tinevez, HongKee Moon, Johannes Schindelin, Curtis Rueden, John Bogovic
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package bdv.img.remote;

import azgracompress.compression.ImageDecompressor;
import bdv.img.cache.CacheArrayLoader;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileShortArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RemoteVolatileShortArrayLoader implements CacheArrayLoader<VolatileShortArray> {
    private final RemoteImageLoader imgLoader;

    private boolean requestCompressedData = false;
    private ImageDecompressor decompressor;


    public RemoteVolatileShortArrayLoader(final RemoteImageLoader imgLoader) {
        this.imgLoader = imgLoader;
    }

    private String constructRequestUrl(final String baseParam,
                                       final int timepoint,
                                       final int setup,
                                       final int level,
                                       final int[] dimensions,
                                       final long[] min) {
        final int index = imgLoader.getCellIndex(timepoint, setup, level, min);
        return String.format("%s?p=%s/%d/%d/%d/%d/%d/%d/%d/%d/%d/%d",
                             imgLoader.baseUrl, baseParam,
                             index, timepoint, setup, level,
                             dimensions[0], dimensions[1], dimensions[2],
                             min[0], min[1], min[2]);
    }


    @Override
    public VolatileShortArray loadArray(final int timepoint,
                                        final int setup,
                                        final int level,
                                        final int[] dimensions,
                                        final long[] min) throws InterruptedException {


        if (requestCompressedData) {
            return loadArrayFromCompressedDataStream(timepoint, setup, level, dimensions, min);
        }

        final short[] data = new short[dimensions[0] * dimensions[1] * dimensions[2]];
        try {
            final URL url = new URL(constructRequestUrl("cell", timepoint, setup, level, dimensions, min));

            final byte[] buf = new byte[data.length * 2];
            final InputStream urlStream = url.openStream();

            //noinspection StatementWithEmptyBody
            for (int i = 0, l = urlStream.read(buf, 0, buf.length);
                 l > 0;
                 i += l, l = urlStream.read(buf, i, buf.length - i))
                ;

            for (int i = 0, j = 0; i < data.length; ++i, j += 2)
                data[i] = (short) (((buf[j] & 0xff) << 8) | (buf[j + 1] & 0xff));

            urlStream.close();
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return new VolatileShortArray(data, true);
    }

    public VolatileShortArray loadArrayFromCompressedDataStream(final int timepoint,
                                                                final int setup,
                                                                final int level,
                                                                final int[] dimensions,
                                                                final long[] min) {

        short[] data = null;
        try {
            final URL url = new URL(constructRequestUrl("cell_qcmp", timepoint, setup, level, dimensions, min));

            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            final int contentLength = connection.getContentLength();

            final InputStream urlStream = connection.getInputStream();
            data = decompressor.decompressStream(urlStream, contentLength);

            urlStream.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return new VolatileShortArray(data, true);
    }

    @Override
    public int getBytesPerElement() {
        return 2;
    }

    public void setDataDecompressor(final ImageDecompressor imageDecompressor) {
        this.decompressor = imageDecompressor;
        requestCompressedData = true;
    }
}
