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
import android.opengl.GLES20;
import android.os.Handler;
import com.google.android.odml.image.MlImage.ImageFormat;

class TextureImageContainer implements ImageContainer {
  private final TextureFrame textureFrame;
  private final Handler handler;
  private final TextureFrame.ReleaseCallback releaseCallback;
  private final ImageProperties imageProperties;

  public TextureImageContainer(
      int textureName,
      EGLContext eglContext,
      long nativeContext,
      @ImageFormat int imageFormat,
      Handler handler,
      TextureFrame.ReleaseCallback releaseCallback) {
    textureFrame = new TextureFrame(textureName, eglContext, nativeContext);

    imageProperties =
        ImageProperties.builder()
            .setImageFormat(imageFormat)
            .setStorageType(MlImage.STORAGE_TYPE_TEXTURE)
            .build();
    this.handler = handler;
    this.releaseCallback = releaseCallback;
  }

  public TextureFrame getTextureFrame() {
    return textureFrame;
  }

  @Override
  public ImageProperties getImageProperties() {
    return imageProperties;
  }

  @Override
  public void close() {
    if (releaseCallback != null) {
      releaseCallback.release(textureFrame);
    } else {
      handler.post(() -> GLES20.glDeleteTextures(1, new int[] {textureFrame.getTextureName()}, 0));
    }
  }
}
