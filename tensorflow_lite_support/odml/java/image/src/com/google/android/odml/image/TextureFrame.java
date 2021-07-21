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

import android.opengl.EGLContext;
import com.google.android.odml.image.opengl.GlSyncToken;

/** Information of a texture image. */
public class TextureFrame {

  /** OpenGL texture id. */
  private final int textureName;
  /** OpenGL context. */
  private final EGLContext eglContext;
  /** OpenGL context used in native side. */
  private final long nativeContext;

  private GlSyncToken consumerGlSyncToken;

  TextureFrame(int textureName, EGLContext eglContext, long nativeContext) {
    this.textureName = textureName;
    this.eglContext = eglContext;
    this.nativeContext = nativeContext;
  }

  /** Returns the texture name. */
  public int getTextureName() {
    return textureName;
  }

  /** Returns the opengl context. */
  public EGLContext getEglContext() {
    return eglContext;
  }

  /** Returns the opengl context used in native, or 0 if it is not set. */
  public long getNativeContext() {
    return nativeContext;
  }

  /** Gets {@link Internal} object which contains internal APIs. */
  public Internal getInternal() {
    return new Internal(this);
  }

  /**
   * Advanced API access for {@link TextureFrame}
   *
   * <p>These APIs are useful for other infrastructures, for example, maintaining the lifecycle in
   * mediapipe. However, an App developer should avoid using the following APIs.
   *
   * <p>APIs inside are treated as internal APIs which are subject to change.
   */
  public static final class Internal {
    private final TextureFrame textureFrame;

    private Internal(TextureFrame textureFrame) {
      this.textureFrame = textureFrame;
    }

    /**
     * Wait for all the consumers to finish work on this texture. This method call will block CPU.
     */
    public void waitForAllConsumers() {
      textureFrame.waitForAllConsumers();
    }

    /**
     * Adds a consumer {@link GlSyncToken} to this texture, where the {@link GlSyncToken} will be
     * used to track usage status of the consumer.
     *
     * @param syncToken syncToken added to this texture
     */
    public void addConsumerGlSyncToken(GlSyncToken syncToken) {
      textureFrame.addConsumerGlSyncToken(syncToken);
    }
  }

  private void waitForAllConsumers() {
    synchronized (this) {
      if (consumerGlSyncToken != null) {
        consumerGlSyncToken.waitOnCpu();
        consumerGlSyncToken.release();
        consumerGlSyncToken = null;
      }
    }
  }

  private void addConsumerGlSyncToken(GlSyncToken syncToken) {
    // TODO(b/194299914): Update it to support multiple syncTokens.
    synchronized (this) {
      if (consumerGlSyncToken != null) {
        consumerGlSyncToken.release();
        consumerGlSyncToken = null;
      }
      consumerGlSyncToken = syncToken;
    }
  }

  /** Callback to release this {@link TextureFrame}. */
  public interface ReleaseCallback {

    /**
     * Called when {@link TextureFrame} need to be released.
     *
     * @param textureFrame textureFrame need to be released
     */
    void release(TextureFrame textureFrame);
  }
}
