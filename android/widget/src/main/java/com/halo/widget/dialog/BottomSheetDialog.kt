package com.halo.widget.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding

//abstract class CompanionSuperclass<VDB : >


//interface CompanionSuperclass<T> {
//
//}

//open class MainGenericClass<T> {
//    companion object : CompanionSuperclass()
//}


//interface Factory<T> {
//    fun create(): T
//}
//
//class MyClass {
//    companion object : Factory<MyClass> {
//        override fun create(): MyClass = MyClass()
//    }
//}
//val f: Factory<MyClass> = MyClass


class HaloBottomSheetDialog<VDB : ViewDataBinding>(context: Context) :
    BottomSheetDialogBase(context) {
    var binding: VDB? = null

    override fun setUpDialog(dialog: CustomDialog) {
    }

    override fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        return null//binding?.root
    }
}