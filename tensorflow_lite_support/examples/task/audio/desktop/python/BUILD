package(
    default_visibility = [
        "//tensorflow_lite_support:users",
    ],
    licenses = ["notice"],  # Apache 2.0
)

# bazel run \
# tensorflow_lite_support/examples/task/audio/desktop/python:audio_classifier_demo \
# -- \
# --model_path=/path/to/model.tflite \
# --audio="What a waste of my time."
py_binary(
    name = "audio_classifier_demo",
    srcs = ["audio_classifier_demo.py"],
    data = ["//tensorflow_lite_support/examples/task/audio/desktop:audio_classifier_demo"],
    deps = [
        "@absl_py//absl:app",
        "@absl_py//absl/flags",
    ],
)
