package com.projects.authenticate.facerecognition

class Logger {

    companion object {

        fun log(message: String) {
            FaceDetectionActivity.setMessage(FaceDetectionActivity.logTextView.text.toString() + "\n" + ">> $message")
            // To scroll to the last message
            // See this SO answer -> https://stackoverflow.com/a/37806544/10878733
            while (FaceDetectionActivity.logTextView.canScrollVertically(1)) {
                FaceDetectionActivity.logTextView.scrollBy(0, 10);
            }
        }

    }

}
