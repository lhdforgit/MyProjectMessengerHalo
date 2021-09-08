package com.hahalolo.incognito.presentation.main.owner.update.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hahalolo.incognito.databinding.IncognitoUpdateDateViewBinding
import com.halo.widget.datepicker.DatePickerPopupBuilder
import com.halo.widget.dialog.BottomSheetDialogBase

class BottomSheetUpdateUserDate constructor(
    context: Context,
    listener: UpdateUserDateInterface
) : BottomSheetDialogBase(context) {
    lateinit var binding: IncognitoUpdateDateViewBinding

    override fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = IncognitoUpdateDateViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setUpDialog(dialog: CustomDialog) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()
    }

    private fun initAction() {
        binding.apply {
            dayTv.setOnClickListener {
                showDayPicker()
            }
            monthTv.setOnClickListener {
                showMonthPicker()
            }
            yearTv.setOnClickListener {
                showYearPicker()
            }
            cancelTv.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun showYearPicker() {
        DatePickerPopupBuilder()
            .context(context)
            .callback { view, year, monthOfYear, dayOfMonth ->
                binding.yearTv.text = year.toString()
            }
            .showDaySpinner(false)
            .showMonthSpinner(false)
            .showYearSpinner(true)
            .setViewAnchor(binding.yearTv)
            .showDropDown()
    }

    private fun showMonthPicker() {
        DatePickerPopupBuilder()
            .context(context)
            .callback { view, year, monthOfYear, dayOfMonth ->
                binding.monthTv.text = (monthOfYear + 1).toString()
            }
            .showDaySpinner(false)
            .showMonthSpinner(true)
            .showYearSpinner(false)
            .setViewAnchor(binding.monthTv)
            .showDropDown()
    }

    private fun showDayPicker() {
        DatePickerPopupBuilder()
            .context(context)
            .callback { view, year, monthOfYear, dayOfMonth ->
                binding.dayTv.text = dayOfMonth.toString()
            }
            .defaultDate(1996, 2, 20)
            .showDaySpinner(true)
            .showMonthSpinner(false)
            .showYearSpinner(false)
            .setViewAnchor(binding.dayTv)
            .showDropDown()
    }

    fun showDialog() {
        show()
    }
}

interface UpdateUserDateInterface {
    fun onClickSave()
    fun onClickCancel()
}