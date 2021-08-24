package com.projects.authenticate.facerecognition

import android.graphics.Rect

data class Prediction( var bbox : Rect, var label : String, val score:Double = 1.0)