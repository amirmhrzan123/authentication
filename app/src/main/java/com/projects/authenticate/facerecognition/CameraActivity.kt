package com.projects.authenticate.facerecognition

import android.Manifest
import android.app.Fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.Camera.PreviewCallback
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.Image.Plane
import android.media.ImageReader
import android.os.*
import android.util.Size
import android.view.Surface
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.projects.authenticate.R
import com.projects.authenticate.facerecognition.env.ImageUtils

abstract class CameraActivity : AppCompatActivity(), ImageReader.OnImageAvailableListener,
    PreviewCallback, CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    protected var previewWidth = 0
    protected var previewHeight = 0
    val isDebug = false
    private var handler: Handler? = null
    private var handlerThread: HandlerThread? = null
    private var useCamera2API = false
    private var isProcessingFrame = false
    private val yuvBytes = arrayOfNulls<ByteArray>(3)
    private var rgbBytes: IntArray? = null
    protected var luminanceStride = 0
        private set
    private var postInferenceCallback: Runnable? = null
    private var imageConverter: Runnable? = null
    private var bottomSheetLayout: LinearLayout? = null
    private var gestureLayout: LinearLayout? = null
    private var sheetBehavior: BottomSheetBehavior<LinearLayout?>? = null
    protected var frameValueTextView: TextView? = null
    protected var cropValueTextView: TextView? = null
    protected var inferenceTimeTextView: TextView? = null
    protected var bottomSheetArrowImageView: ImageView? = null
    private var plusImageView: ImageView? = null
    private var minusImageView: ImageView? = null
    private var apiSwitchCompat: SwitchCompat? = null
    private var threadsTextView: TextView? = null
    private var btnSwitchCam: FloatingActionButton? = null
    protected var cameraFacing: Int? = null
        private set
    private var cameraId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        val intent = intent
        //useFacing = intent.getIntExtra(KEY_USE_FACING, CameraCharacteristics.LENS_FACING_FRONT);
        cameraFacing = intent.getIntExtra(KEY_USE_FACING, CameraCharacteristics.LENS_FACING_BACK)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.tfe_od_activity_camera)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        if (hasPermission()) {
            setFragment()
        } else {
            requestPermission()
        }
        threadsTextView = findViewById(R.id.threads)
        plusImageView = findViewById(R.id.plus)
        minusImageView = findViewById(R.id.minus)
        apiSwitchCompat = findViewById(R.id.api_info_switch)
        bottomSheetLayout = findViewById(R.id.bottom_sheet_layout)
        gestureLayout = findViewById(R.id.gesture_layout)
        sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout!!)
        bottomSheetArrowImageView = findViewById(R.id.bottom_sheet_arrow)
        btnSwitchCam = findViewById(R.id.fab_switchcam)
        val vto = gestureLayout!!.viewTreeObserver
        vto.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        gestureLayout!!.viewTreeObserver.removeGlobalOnLayoutListener(this)
                    } else {
                        gestureLayout!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                    //                int width = bottomSheetLayout.getMeasuredWidth();
                    val height = gestureLayout!!.getMeasuredHeight()
                    sheetBehavior!!.peekHeight = height
                }
            })
        sheetBehavior!!.isHideable = false
        sheetBehavior!!.setBottomSheetCallback(
            object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            bottomSheetArrowImageView!!.setImageResource(R.drawable.ic_down)
                        }
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            bottomSheetArrowImageView!!.setImageResource(R.drawable.ic_up)
                        }
                        BottomSheetBehavior.STATE_DRAGGING -> {
                        }
                        BottomSheetBehavior.STATE_SETTLING -> bottomSheetArrowImageView!!.setImageResource(
                            R.drawable.ic_up
                        )
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        frameValueTextView = findViewById(R.id.frame_info)
        cropValueTextView = findViewById(R.id.crop_info)
        inferenceTimeTextView = findViewById(R.id.inference_info)
        apiSwitchCompat!!.setOnCheckedChangeListener(this)
        plusImageView!!.setOnClickListener(this)
        minusImageView!!.setOnClickListener(this)
        btnSwitchCam!!.setOnClickListener(View.OnClickListener { onSwitchCamClick() })
    }

    private fun onSwitchCamClick() {
        switchCamera()
    }

    fun switchCamera() {
        val intent = intent
        if (cameraFacing == CameraCharacteristics.LENS_FACING_FRONT) {
            cameraFacing = CameraCharacteristics.LENS_FACING_BACK
        } else {
            cameraFacing = CameraCharacteristics.LENS_FACING_FRONT
        }
        intent.putExtra(KEY_USE_FACING, cameraFacing)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        restartWith(intent)
    }

    private fun restartWith(intent: Intent) {
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    protected fun getRgbBytes(): IntArray? {
        imageConverter!!.run()
        return rgbBytes
    }

    protected val luminance: ByteArray?
        protected get() = yuvBytes[0]

    /** Callback for android.hardware.Camera API  */
    override fun onPreviewFrame(bytes: ByteArray, camera: Camera) {
        if (isProcessingFrame) {
            return
        }
        try {
            // Initialize the storage bitmaps once when the resolution is known.
            if (rgbBytes == null) {
                val previewSize = camera.parameters.previewSize
                previewHeight = previewSize.height
                previewWidth = previewSize.width
                //rgbBytes = new int[previewWidth * previewHeight];
                //onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
                rgbBytes = IntArray(previewWidth * previewHeight)
                var rotation = 90
                if (cameraFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                    rotation = 270
                }
                onPreviewSizeChosen(Size(previewSize.width, previewSize.height), rotation)
            }
        } catch (e: Exception) {
            return
        }
        isProcessingFrame = true
        yuvBytes[0] = bytes
        luminanceStride = previewWidth
        imageConverter = Runnable {
            ImageUtils.convertYUV420SPToARGB8888(
                bytes,
                previewWidth,
                previewHeight,
                rgbBytes!!
            )
        }
        postInferenceCallback = Runnable {
            camera.addCallbackBuffer(bytes)
            isProcessingFrame = false
        }
        processImage()
    }

    /** Callback for Camera2 API  */
    override fun onImageAvailable(reader: ImageReader) {
        // We need wait until we have some size from onPreviewSizeChosen
        if (previewWidth == 0 || previewHeight == 0) {
            return
        }
        if (rgbBytes == null) {
            rgbBytes = IntArray(previewWidth * previewHeight)
        }
        try {
            val image = reader.acquireLatestImage() ?: return
            if (isProcessingFrame) {
                image.close()
                return
            }
            isProcessingFrame = true
            Trace.beginSection("imageAvailable")
            val planes = image.planes
            fillBytes(planes, yuvBytes)
            luminanceStride = planes[0].rowStride
            val uvRowStride = planes[1].rowStride
            val uvPixelStride = planes[1].pixelStride
            imageConverter = object : Runnable {
                override fun run() {
                    ImageUtils.convertYUV420ToARGB8888(
                        yuvBytes[0]!!,
                        yuvBytes[1]!!,
                        yuvBytes[2]!!,
                        previewWidth,
                        previewHeight,
                        luminanceStride,
                        uvRowStride,
                        uvPixelStride,
                        rgbBytes!!
                    )
                }
            }
            postInferenceCallback = Runnable {
                image.close()
                isProcessingFrame = false
            }
            processImage()
        } catch (e: Exception) {
            Trace.endSection()
            return
        }
        Trace.endSection()
    }

    @Synchronized
    public override fun onStart() {
        super.onStart()
    }

    @Synchronized
    public override fun onResume() {
        super.onResume()
        handlerThread = HandlerThread("inference")
        handlerThread!!.start()
        handler = Handler(handlerThread!!.looper)
    }

    @Synchronized
    public override fun onPause() {
        handlerThread!!.quitSafely()
        try {
            handlerThread!!.join()
            handlerThread = null
            handler = null
        } catch (e: InterruptedException) {
        }
        super.onPause()
    }

    @Synchronized
    public override fun onStop() {
        super.onStop()
    }

    @Synchronized
    public override fun onDestroy() {
        super.onDestroy()
    }

    @Synchronized
    protected fun runInBackground(r: Runnable?) {
        if (handler != null) {
            handler!!.post(r!!)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST) {
            if (allPermissionsGranted(grantResults)) {
                setFragment()
            } else {
                requestPermission()
            }
        }
    }

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
                Toast.makeText(
                    this@CameraActivity,
                    "Camera permission is required for this demo",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            requestPermissions(arrayOf(PERMISSION_CAMERA), PERMISSIONS_REQUEST)
        }
    }

    // Returns true if the device supports the required hardware level, or better.
    private fun isHardwareLevelSupported(
        characteristics: CameraCharacteristics, requiredLevel: Int
    ): Boolean {
        val deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)!!
        return if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
            requiredLevel == deviceLevel
        } else requiredLevel <= deviceLevel
        // deviceLevel is not LEGACY, can use numerical sort
    }

    private fun chooseCamera(): String? {
        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)
                val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    ?: continue

                // Fallback to camera1 API for internal cameras that don't have full support.
                // This should help with legacy situations where using the camera2 API causes
                // distorted or otherwise broken previews.
                //final int facing =
                //(facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
//        if (!facing.equals(useFacing)) {
//          continue;
//        }
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (cameraFacing != null && facing != null &&
                    facing != cameraFacing
                ) {
                    continue
                }
                useCamera2API = (facing == CameraCharacteristics.LENS_FACING_EXTERNAL
                        || isHardwareLevelSupported(
                    characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL
                ))
                return cameraId
            }
        } catch (e: CameraAccessException) {
        }
        return null
    }

    protected fun setFragment() {
        cameraId = chooseCamera()
        val fragment: androidx.fragment.app.Fragment
        if (useCamera2API) {
            val camera2Fragment = CameraConnectionFragment.newInstance(
                object : CameraConnectionFragment.ConnectionCallback {
                    override fun onPreviewSizeChosen(size: Size?, rotation: Int) {
                        previewHeight = size!!.height
                        previewWidth = size.width
                        this@CameraActivity.onPreviewSizeChosen(size, rotation)
                    }
                },
                this,
                layoutId,
                desiredPreviewFrameSize!!
            )
            camera2Fragment.setCamera(cameraId)
            fragment = camera2Fragment
        } else {
            val facing =
                if (cameraFacing == CameraCharacteristics.LENS_FACING_BACK) Camera.CameraInfo.CAMERA_FACING_BACK else Camera.CameraInfo.CAMERA_FACING_FRONT
            val frag = LegacyCameraConnectionFragment(
                this,
                layoutId,
                desiredPreviewFrameSize!!, facing
            )
            fragment = frag
        }
        supportFragmentManager.beginTransaction().replace(R.id.container,fragment).commit()
    }

    protected fun fillBytes(planes: Array<Plane>, yuvBytes: Array<ByteArray?>) {
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (i in planes.indices) {
            val buffer = planes[i].buffer
            if (yuvBytes[i] == null) {
                yuvBytes[i] = ByteArray(buffer.capacity())
            }
            buffer[yuvBytes[i]]
        }
    }

    protected fun readyForNextImage() {
        if (postInferenceCallback != null) {
            postInferenceCallback!!.run()
        }
    }

    protected val screenOrientation: Int
        protected get() = when (windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_270 -> 270
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_90 -> 90
            else -> 0
        }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        setUseNNAPI(isChecked)
        if (isChecked) apiSwitchCompat!!.text = "NNAPI" else apiSwitchCompat!!.text = "TFLITE"
    }

    override fun onClick(v: View) {
        if (v.id == R.id.plus) {
            val threads = threadsTextView!!.text.toString().trim { it <= ' ' }
            var numThreads = threads.toInt()
            if (numThreads >= 9) return
            numThreads++
            threadsTextView!!.text = numThreads.toString()
            setNumThreads(numThreads)
        } else if (v.id == R.id.minus) {
            val threads = threadsTextView!!.text.toString().trim { it <= ' ' }
            var numThreads = threads.toInt()
            if (numThreads == 1) {
                return
            }
            numThreads--
            threadsTextView!!.text = numThreads.toString()
            setNumThreads(numThreads)
        }
    }

    protected fun showFrameInfo(frameInfo: String?) {
        frameValueTextView!!.text = frameInfo
    }

    protected fun showCropInfo(cropInfo: String?) {
        cropValueTextView!!.text = cropInfo
    }

    protected fun showInference(inferenceTime: String?) {
        inferenceTimeTextView!!.text = inferenceTime
    }

    protected abstract fun processImage()
    protected abstract fun onPreviewSizeChosen(size: Size?, rotation: Int)
    protected abstract val layoutId: Int
    protected abstract val desiredPreviewFrameSize: Size?

    protected abstract fun setNumThreads(numThreads: Int)
    protected abstract fun setUseNNAPI(isChecked: Boolean)

    companion object {
        private const val PERMISSIONS_REQUEST = 1
        private const val PERMISSION_CAMERA = Manifest.permission.CAMERA
        private const val KEY_USE_FACING = "use_facing"
        private fun allPermissionsGranted(grantResults: IntArray): Boolean {
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }
    }
}
