package com.projects.authenticate.facerecognition

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.nio.ByteBuffer

// Utility class for FaceNet model
class FaceNetModel( private var context : Context) {

    // Input image size for FaceNet model.
    private val imgSize = 160

    // Output embedding size
    val embeddingDim = 128

    private var interpreter : Interpreter
    private val imageTensorProcessor = ImageProcessor.Builder()
        .add( ResizeOp( imgSize , imgSize , ResizeOp.ResizeMethod.BILINEAR ) )
        .add( CastOp( DataType.FLOAT32 ) )
        .build()

    init {
        // Initialize TFLiteInterpreter
        val interpreterOptions = Interpreter.Options().apply {
            setNumThreads( 4 )
        }
        interpreter = Interpreter(FileUtil.loadMappedFile(context, "model.tflite") , interpreterOptions )
    }


    // Gets an face embedding using FaceNet, use the `crop` rect.
    fun getFaceEmbedding(image : Bitmap, crop : Rect) : FloatArray {
        return runFaceNet( convertBitmapToBuffer( cropRectFromBitmap( image , crop )))[0]
    }


    // Gets an face embedding using FaceNet, assuming the image contains a cropped face
    fun getCroppedFaceEmbedding( image : Bitmap ) : FloatArray {
        return runFaceNet( convertBitmapToBuffer( image ) )[0]
    }


    // Run the FaceNet model.
    private fun runFaceNet(inputs: Any): Array<FloatArray> {
        val t1 = System.currentTimeMillis()
        val faceNetModelOutputs = Array( 1 ){ FloatArray( embeddingDim ) }
        interpreter.run( inputs, faceNetModelOutputs )
        Log.i( "Performance" , "FaceNet Inference Speed in ms : ${System.currentTimeMillis() - t1}")
        return faceNetModelOutputs
    }


    // Resize the given bitmap and convert it to a ByteBuffer
    private fun convertBitmapToBuffer( image : Bitmap) : ByteBuffer {
        return imageTensorProcessor.process( TensorImage.fromBitmap( image ) ).buffer
    }


    // Crop the given bitmap with the given rect.
    private fun cropRectFromBitmap(source: Bitmap, rect: Rect ): Bitmap {
        var width = rect.width()
        var height = rect.height()
        if ( (rect.left + width) > source.width ){
            width = source.width - rect.left
        }
        if ( (rect.top + height ) > source.height ){
            height = source.height - rect.top
        }
        val croppedBitmap = Bitmap.createBitmap( source , rect.left , rect.top , width , height )
        // Uncomment the below line if you want to save the input image.
        // BitmapUtils.saveBitmap( context , croppedBitmap , "source" )
        return croppedBitmap
    }
}