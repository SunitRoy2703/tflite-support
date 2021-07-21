/* Copyright 2021 Google LLC. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.google.android.odml.image;

import android.graphics.Rect;
import android.opengl.EGLContext;
import android.os.Handler;
import com.google.android.odml.image.MlImage.ImageFormat;
import com.google.android.odml.image.TextureFrame.ReleaseCallback;

/**
 * Builds {@link MlImage} from OpenGL texture.
 *
 * <p>The openGL texture is identified by {@code textureName} and {@link android.opengl.EGLContext}.
 * It should already contain image content when you pass it to {@link TextureMlImageBuilder}. Once
 * openGL texture is passed in, to keep data integrity you shouldn't modify image content in it.
 *
 * <p>Use {@link TextureImageExtractor} to get openGL texture you passed in.
 */
public class TextureMlImageBuilder {

  // Mandatory fields.
  private final int textureName;
  private final EGLContext eglContext;
  private final int width;
  private final int height;
  @ImageFormat private final int imageFormat;

  // Optional fields.
  private int rotation;
  private Rect roi;
  private Handler handler = new Handler();
  private long timestamp;
  private long nativeContext;
  private TextureFrame.ReleaseCallback releaseCallback;

  /**
   * Creates the builder with necessary information of the OpenGL texture.
   *
   * <p>Also calls {@link #setRotation(int)}, {@link #setNativeContext(long)} or {@link
   * #setHandler(Handler)} to set the optional properties. If not set, the values will be set with
   * default:
   *
   * <ul>
   *   <li>rotation: 0
   *   <li>nativeContext: 0
   *   <li>handler: default handler where {@link MlImage} is used
   * </ul>
   *
   * @param textureName texture name of this OpenGL texture
   * @param eglContext OpenGL context where texture is created
   * @param width width of the image stored in OpenGL texture
   * @param height height of the image stored in OpenGL texture
   * @param imageFormat format of the image stored in OpenGL texture
   */
  public TextureMlImageBuilder(
      int textureName, EGLContext eglContext, int width, int height, @ImageFormat int imageFormat) {
    this.textureName = textureName;
    this.eglContext = eglContext;
    this.width = width;
    this.height = height;
    this.imageFormat = imageFormat;
    rotation = 0;
    roi = new Rect(0, 0, width, height);
    timestamp = 0;
  }

  /**
   * Sets value for {@link MlImage#getRotation()}.
   *
   * @throws IllegalArgumentException if the rotation value is not 0, 90, 180 or 270.
   */
  public TextureMlImageBuilder setRotation(int rotation) {
    MlImage.validateRotation(rotation);
    this.rotation = rotation;
    return this;
  }

  /** Sets value for {@link MlImage#getRoi()}. */
  TextureMlImageBuilder setRoi(Rect roi) {
    this.roi = roi;
    return this;
  }

  /** Sets value for {@link MlImage#getTimestamp()}. */
  TextureMlImageBuilder setTimestamp(long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Sets value for {@link TextureFrame#getNativeContext()}. If not set, the default value will be
   * 0.
   */
  public TextureMlImageBuilder setNativeContext(long nativeContext) {
    this.nativeContext = nativeContext;
    return this;
  }

  /**
   * Sets handler that can be used to execute OpenGL operation for this texture. It should represent
   * a thread where the {@link EGLContext} is already current. If not set, it will use the thread
   * where {@link MlImage} is used.
   */
  public TextureMlImageBuilder setHandler(Handler handler) {
    this.handler = handler;
    return this;
  }

  /**
   * Sets the callback to release the internal storage. Normally when {@link MlImage#close()} is
   * called, internal storage in {@link MlImage} will be released. However if this callback has been
   * set, instead of releasing the texture, this callback will be invoked to let developer maintain
   * this texture (e.g. release it or reuse it).
   */
  public TextureMlImageBuilder setReleaseCallback(ReleaseCallback releaseCallback) {
    this.releaseCallback = releaseCallback;
    return this;
  }

  /** Builds an {@link MlImage} instance. */
  public MlImage build() {
    return new MlImage(
        new TextureImageContainer(
            textureName, eglContext, nativeContext, imageFormat, handler, releaseCallback),
        rotation,
        roi,
        timestamp,
        width,
        height);
  }
}
