package com.hdaes.ykzauto.workflow

sealed interface WorkflowState {
    data object Idle : WorkflowState
    data object Guarded : WorkflowState
    data class Failed(val reason: String) : WorkflowState
}
