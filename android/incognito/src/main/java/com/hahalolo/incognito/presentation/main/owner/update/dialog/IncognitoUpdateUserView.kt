package com.hahalolo.incognito.presentation.main.owner.update.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.hahalolo.incognito.databinding.IncognitoUpdateUserViewBinding
import com.halo.widget.dialog.BottomSheetDialogBase

class IncognitoUpdateUserView constructor(
    context: Context,
    val listener: IncognitoUpdateUserInterface,
    val type: Int
) : BottomSheetDialogBase(context) {
    lateinit var binding: IncognitoUpdateUserViewBinding
    private var itemView: View? = null
    private val requestManager by lazy { Glide.with(this) }

    override fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = IncognitoUpdateUserViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setUpDialog(dialog: CustomDialog) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initAction()
    }

    private fun initView() {
        binding.apply {
            if(type == IncognitoUpdateType.EDIT_NAME){
                nameTv.text = "Tên hiển thị"
                statusTv.visibility = View.GONE
            } else if(type == IncognitoUpdateType.EDIT_PHONE){
                nameTv.text = "Số điện thoại"
                statusTv.visibility = View.VISIBLE
            } else if(type == IncognitoUpdateType.EDIT_EMAIL){
                nameTv.text = "Email"
                statusTv.visibility = View.VISIBLE
            } else if(type == IncognitoUpdateType.EDIT_ADDRESS){
                nameTv.text = "Địa chỉ"
                statusTv.visibility = View.VISIBLE
            }
        }
    }

    private fun initAction() {
       binding.apply {
           saveBtn.setOnClickListener {
               listener.onClickSave()
           }

           cancelBtn.setOnClickListener {
               dismiss()
           }
       }
    }
}

interface IncognitoUpdateUserInterface {
    fun onClickSave()
    fun onClickCancel()
}