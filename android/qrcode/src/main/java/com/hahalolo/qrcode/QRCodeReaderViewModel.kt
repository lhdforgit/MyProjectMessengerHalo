package com.hahalolo.qrcode

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.Barcode
import java.util.*
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 5/19/21
 * com.hahalolo.qrcode
 */
class QRCodeReaderViewModel
@Inject constructor(
) : ViewModel() {

    val workflowState = MutableLiveData<WorkflowState>()
    val objectToSearch = MutableLiveData<DetectedObjectInfo>()
    val detectedBarcode = MutableLiveData<Barcode>()

    private val objectIdsToSearch = HashSet<Int>()

    var isCameraLive = false
        private set

    private var confirmedObject: DetectedObjectInfo? = null

    /**
     * State set of the application workflow.
     */
    enum class WorkflowState {
        NOT_STARTED,
        DETECTING,
        DETECTED,
        CONFIRMING,
        CONFIRMED,
        SEARCHING,
        SEARCHED
    }

    @MainThread
    fun setWorkflowState(workflowState: WorkflowState) {
        if (workflowState != WorkflowState.CONFIRMED &&
            workflowState != WorkflowState.SEARCHING &&
            workflowState != WorkflowState.SEARCHED
        ) {
            confirmedObject = null
        }
        this.workflowState.value = workflowState
    }

    fun markCameraLive() {
        isCameraLive = true
        objectIdsToSearch.clear()
    }

    fun markCameraFrozen() {
        isCameraLive = false
    }
}

