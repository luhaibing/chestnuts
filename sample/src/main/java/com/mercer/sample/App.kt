package com.mercer.sample

import android.app.Application
import com.mercer.paper.PaperBasket

/**
 * author:  Mercer
 * date:    2024/6/15
 * desc:
 *
 */
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        PaperBasket.registry(Sample1Dialog,Sample1Dialog.Producer)
        PaperBasket.registry(Sample2Dialog,Sample2Dialog.Producer)
        PaperBasket.registry(Sample3Dialog,Sample3Dialog.Producer)
    }
}