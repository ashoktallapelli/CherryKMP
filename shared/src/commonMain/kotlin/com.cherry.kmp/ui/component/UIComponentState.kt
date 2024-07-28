package com.cherry.kmp.ui.component

sealed class UIComponentState {
    data object Show : UIComponentState()
    data object Hide : UIComponentState()
}