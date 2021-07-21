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

/**
 * Utility for extracting {@link TextureFrame} from {@link MlImage}.
 *
 * <p>Currently it only supports {@link MlImage} with {@link MlImage#STORAGE_TYPE_TEXTURE},
 * otherwise {@link IllegalArgumentException} will be thrown.
 */
public final class TextureImageExtractor {

  /**
   * Extracts a {@link TextureFrame} from an {@link MlImage}.
   *
   * <p>Notice: Properties of the {@code image} like rotation will not take effects.
   *
   * @param image the image to extract {@link TextureFrame} from.
   * @return the {@link TextureFrame} stored in {@link MlImage}
   * @throws IllegalArgumentException when the extraction requires unsupported format or data type
   *     conversions.
   */
  public static TextureFrame extract(MlImage image) {
    ImageContainer container = image.getContainer();
    switch (container.getImageProperties().getStorageType()) {
      case MlImage.STORAGE_TYPE_TEXTURE:
        TextureImageContainer textureImageContainer = (TextureImageContainer) container;
        return textureImageContainer.getTextureFrame();
      default:
        throw new IllegalArgumentException(
            "Extracting TextureFrame from an MlImage created by objects other than texture is not"
                + " supported");
    }
  }

  private TextureImageExtractor() {}
}
