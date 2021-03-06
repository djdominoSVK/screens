package com.cleverlance.mobile.android.screens.dialog.flow

import com.cleverlance.mobile.android.screens.dialog.android.DialogScreen
import com.cleverlance.mobile.android.screens.dialog.android.NoDialogScreen
import com.cleverlance.mobile.android.screens.dialog.android.ScreenDispatcher
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.disposables.Disposable
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(JUnitPlatform::class)
class OneSlotScreenFlowSpek : SubjectSpek<OneSlotScreenFlow<DialogScreen>>({
    subject {
        OneSlotScreenFlow(NoDialogScreen())
    }

    it("show dialog view when dialog screen set") {
        val dismissView = mock<Disposable>()
        val dispatcher = mock<ScreenDispatcher<DialogScreen>> {
            whenever(it.show(any())).thenReturn(dismissView)
        }
        subject.subscribe(dispatcher)

        reset(dispatcher)

        val screen: DialogScreen = mock()
        subject.show(screen)

        verify(dispatcher).show(screen)
    }

    it("hide dialog view when dialog screen dismissed") {
        val dismissView = mock<Disposable>()
        val dispatcher = mock<ScreenDispatcher<DialogScreen>> {
            whenever(it.show(any())).thenReturn(dismissView)
        }
        subject.subscribe(dispatcher)

        reset(dispatcher)

        val screen = mock(DialogScreen::class.java)
        val dismissScreen = subject.show(screen)

        dismissScreen.dispose()

        verify(dismissView).dispose()
    }

    it("dispatcher should hide screen on dispatcher unsubscribe") {
        val dismissScreenView = mock<Disposable>()
        val dispatcher = mock<ScreenDispatcher<DialogScreen>> {
            whenever(it.show(any())).thenReturn(dismissScreenView)
        }

        val unsubscribeDispatcher = subject.subscribe(dispatcher)

        reset(dispatcher)

        subject.show(mock())

        unsubscribeDispatcher.dispose()

        verify(dismissScreenView).dispose()
    }

    it("dialog view visibility switching") {
        val dialogScreenDispatcher1 = mock<ScreenDispatcher<DialogScreen>>()
        val subscription1 = subject.subscribe(dialogScreenDispatcher1)

        val dismissScreenView1 = mock<Disposable>()
        whenever(dialogScreenDispatcher1.show(any())).thenReturn(dismissScreenView1)

        val screen = mock(DialogScreen::class.java)
        subject.show(screen)

        verify<ScreenDispatcher<DialogScreen>>(dialogScreenDispatcher1).show(screen)

        // unbind one view and attach new one e.g. screen rotation
        subscription1.dispose()
        verify(dismissScreenView1).dispose()

        val dismissScreenView2 = mock<Disposable>()
        val dialogScreenDispatcher2 = mock<ScreenDispatcher<DialogScreen>> {
            whenever(it.show(any())).thenReturn(dismissScreenView2)
        }

        val subscription2 = subject.subscribe(dialogScreenDispatcher2)
        verify<ScreenDispatcher<DialogScreen>>(dialogScreenDispatcher2).show(screen)

        subject.hide(screen)

        verify(dismissScreenView2).dispose()

        subscription2.dispose()
    }
})