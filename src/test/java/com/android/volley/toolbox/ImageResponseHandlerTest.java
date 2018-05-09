package com.android.volley.toolbox;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowBitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ImageResponseHandlerTest {
    @Test
    public void parseNetworkResponse_resizing() throws Exception {
        // This is a horrible hack but Robolectric doesn't have a way to provide
        // width and height hints for decodeByteArray. It works because the byte array
        // "file:fake" is ASCII encodable and thus the name in Robolectric's fake
        // bitmap creator survives as-is, and provideWidthAndHeightHints puts
        // "file:" + name in its lookaside map. I write all this because it will
        // probably break mysteriously at some point and I feel terrible about your
        // having to debug it.
        byte[] jpegBytes = "file:fake".getBytes(StandardCharsets.UTF_8);
        ShadowBitmapFactory.provideWidthAndHeightHints("fake", 1024, 500);
        NetworkResponse jpeg = new NetworkResponse(jpegBytes);

        // Scale the image uniformly (maintain the image's aspect ratio) so that
        // both dimensions (width and height) of the image will be equal to or
        // less than the corresponding dimension of the view.
        ImageView.ScaleType scalteType = ImageView.ScaleType.CENTER_INSIDE;

        // Exact sizes
        verifyResize(jpeg, 512, 250, scalteType, 512, 250); // exactly half
        verifyResize(jpeg, 511, 249, scalteType, 509, 249); // just under half
        verifyResize(jpeg, 1080, 500, scalteType, 1024, 500); // larger
        verifyResize(jpeg, 500, 500, scalteType, 500, 244); // keep same ratio

        // Specify only width, preserve aspect ratio
        verifyResize(jpeg, 512, 0, scalteType, 512, 250);
        verifyResize(jpeg, 800, 0, scalteType, 800, 390);
        verifyResize(jpeg, 1024, 0, scalteType, 1024, 500);

        // Specify only height, preserve aspect ratio
        verifyResize(jpeg, 0, 250, scalteType, 512, 250);
        verifyResize(jpeg, 0, 391, scalteType, 800, 391);
        verifyResize(jpeg, 0, 500, scalteType, 1024, 500);

        // No resize
        verifyResize(jpeg, 0, 0, scalteType, 1024, 500);

        // Scale the image uniformly (maintain the image's aspect ratio) so that
        // both dimensions (width and height) of the image will be equal to or
        // larger than the corresponding dimension of the view.
        scalteType = ImageView.ScaleType.CENTER_CROP;

        // Exact sizes
        verifyResize(jpeg, 512, 250, scalteType, 512, 250);
        verifyResize(jpeg, 511, 249, scalteType, 511, 249);
        verifyResize(jpeg, 1080, 500, scalteType, 1024, 500);
        verifyResize(jpeg, 500, 500, scalteType, 1024, 500);

        // Specify only width
        verifyResize(jpeg, 512, 0, scalteType, 512, 250);
        verifyResize(jpeg, 800, 0, scalteType, 800, 390);
        verifyResize(jpeg, 1024, 0, scalteType, 1024, 500);

        // Specify only height
        verifyResize(jpeg, 0, 250, scalteType, 512, 250);
        verifyResize(jpeg, 0, 391, scalteType, 800, 391);
        verifyResize(jpeg, 0, 500, scalteType, 1024, 500);

        // No resize
        verifyResize(jpeg, 0, 0, scalteType, 1024, 500);

        // Scale in X and Y independently, so that src matches dst exactly. This
        // may change the aspect ratio of the src.
        scalteType = ImageView.ScaleType.FIT_XY;

        // Exact sizes
        verifyResize(jpeg, 512, 250, scalteType, 512, 250);
        verifyResize(jpeg, 511, 249, scalteType, 511, 249);
        verifyResize(jpeg, 1080, 500, scalteType, 1024, 500);
        verifyResize(jpeg, 500, 500, scalteType, 500, 500);

        // Specify only width
        verifyResize(jpeg, 512, 0, scalteType, 512, 500);
        verifyResize(jpeg, 800, 0, scalteType, 800, 500);
        verifyResize(jpeg, 1024, 0, scalteType, 1024, 500);

        // Specify only height
        verifyResize(jpeg, 0, 250, scalteType, 1024, 250);
        verifyResize(jpeg, 0, 391, scalteType, 1024, 391);
        verifyResize(jpeg, 0, 500, scalteType, 1024, 500);

        // No resize
        verifyResize(jpeg, 0, 0, scalteType, 1024, 500);
    }

    private void verifyResize(
            NetworkResponse networkResponse,
            int maxWidth,
            int maxHeight,
            ImageView.ScaleType scaleType,
            int expectedWidth,
            int expectedHeight) {
        ImageResponseHandler responseHandler = new ImageResponseHandler(null, maxWidth, maxHeight, scaleType, "", Bitmap.Config.RGB_565);
        Response<Bitmap> response = responseHandler.parseNetworkResponse(networkResponse);
        assertNotNull(response);
        assertTrue(response.isSuccess());
        Bitmap bitmap = response.result;
        assertNotNull(bitmap);
        assertEquals(expectedWidth, bitmap.getWidth());
        assertEquals(expectedHeight, bitmap.getHeight());
    }

    @Test
    public void findBestSampleSize() {
        // desired == actual == 1
        assertEquals(1, ImageResponseHandler.findBestSampleSize(100, 150, 100, 150));

        // exactly half == 2
        assertEquals(2, ImageResponseHandler.findBestSampleSize(280, 160, 140, 80));

        // just over half == 1
        assertEquals(1, ImageResponseHandler.findBestSampleSize(1000, 800, 501, 401));

        // just under 1/4 == 4
        assertEquals(4, ImageResponseHandler.findBestSampleSize(100, 200, 24, 50));
    }

    private static byte[] readInputStream(InputStream in) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int count;
        while ((count = in.read(buffer)) != -1) {
            bytes.write(buffer, 0, count);
        }
        in.close();
        return bytes.toByteArray();
    }
}
