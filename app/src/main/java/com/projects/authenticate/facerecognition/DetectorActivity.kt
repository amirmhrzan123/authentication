package com.projects.authenticate.facerecognition

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.*
import android.hardware.camera2.CameraCharacteristics
import android.media.ImageReader
import android.os.Bundle
import android.os.SystemClock
import android.util.Size
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.projects.authenticate.R
import com.projects.authenticate.facerecognition.customview.OverlayView
import com.projects.authenticate.facerecognition.env.BorderedText
import com.projects.authenticate.facerecognition.env.ImageUtils
import com.projects.authenticate.facerecognition.tracking.MultiBoxTracker
import java.io.IOException
import java.util.*

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
