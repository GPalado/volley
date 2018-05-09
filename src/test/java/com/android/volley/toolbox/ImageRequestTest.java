/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.toolbox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.android.volley.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ImageRequestTest {

    @Test
    public void publicMethods() throws Exception {
        // Catch-all test to find API-breaking changes.
        assertNotNull(
                ImageRequest.class.getConstructor(
                        String.class,
                        Response.Listener.class,
                        int.class,
                        int.class,
                        Bitmap.Config.class,
                        Response.ErrorListener.class));
        assertNotNull(
                ImageRequest.class.getConstructor(
                        String.class,
                        Response.Listener.class,
                        int.class,
                        int.class,
                        ImageView.ScaleType.class,
                        Bitmap.Config.class,
                        Response.ErrorListener.class));
        assertEquals(ImageRequest.DEFAULT_IMAGE_TIMEOUT_MS, 1000);
        assertEquals(ImageRequest.DEFAULT_IMAGE_MAX_RETRIES, 2);
        assertEquals(ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT, 2f, 0);
    }
}
