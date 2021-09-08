package com.hahalolo.incognito.presentation.main.owner.update

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoOwnerUpdateProfileActBinding
import com.hahalolo.incognito.presentation.base.AbsIncActivity
import com.hahalolo.incognito.presentation.main.owner.update.dialog.*
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import com.halo.di.ActivityScoped
import kotlinx.android.synthetic.main.incognito_main_act.view.*
import javax.inject.Inject

@ActivityScoped
class IncognitoOwnerUpdateProfileAct @Inject constructor() : AbsIncActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    var binding by notNull<IncognitoOwnerUpdateProfileActBinding>()
    val viewModel: IncognitoOwnerUpdateProfileViewModel by viewModels { factory }
    var menuData: BottomSheetUpdateUserDate? = null

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_owner_update_profile_act)
    }

    override fun initializeLayout() {
        initAction()
    }

    private fun initAction() {
        binding.toolbar.setOnClickListener {
            finish()
        }
        binding.apply {
            newImage.setOnClickListener {
                val menuAvatar = IncognitoUpdateAvatarView(this@IncognitoOwnerUpdateProfileAct,
                    object : IncognitoUpdateAvatarInterface {
                        override fun onClickAvatar() {
                            navigateAvatarView()
                        }

                        override fun onClickUpdateAvatarView() {

                        }

                        override fun onClickCancel() {

                        }

                    })
                menuAvatar.show()
            }

            dateTv.setOnClickListener {
                it.run {
                    val menuDate = BottomSheetUpdateUserDate(this@IncognitoOwnerUpdateProfileAct,
                        object : UpdateUserDateInterface {
                            override fun onClickSave() {

                            }

                            override fun onClickCancel() {

                            }

                        })
                    menuDate.show()
                }
            }

            genderTv.setOnClickListener {
                it.run {
                    val menuGender = IncognitoUpdateGenderView(this@IncognitoOwnerUpdateProfileAct,
                        object : IncognitoUpdateGenderInterface {
                            override fun onClickMale() {

                            }

                            override fun onClickFemale() {
                            }

                            override fun onClickOther() {
                            }

                            override fun onClickSave() {
                            }

                            override fun onClickCancel() {
                            }

                        })
                    menuGender.show()
                }
            }

            nameTv.setOnClickListener {
                it.run {
                    val menuName = IncognitoUpdateUserView(this@IncognitoOwnerUpdateProfileAct,
                        object : IncognitoUpdateUserInterface {
                            override fun onClickSave() {
                            }

                            override fun onClickCancel() {
                            }

                        }, IncognitoUpdateType.EDIT_NAME)
                    menuName.show()
                }
            }

            phoneTv.setOnClickListener {
                it.run {
                    val menuPhone = IncognitoUpdateUserView(this@IncognitoOwnerUpdateProfileAct,
                        object : IncognitoUpdateUserInterface {
                            override fun onClickSave() {
                            }

                            override fun onClickCancel() {

                            }

                        }, IncognitoUpdateType.EDIT_PHONE)
                    menuPhone.show()
                }
            }

            emailTv.setOnClickListener {
                it.run {
                    val menuEmail = IncognitoUpdateUserView(this@IncognitoOwnerUpdateProfileAct,
                        object : IncognitoUpdateUserInterface {
                            override fun onClickSave() {
                            }

                            override fun onClickCancel() {
                            }

                        }, IncognitoUpdateType.EDIT_EMAIL)
                    menuEmail.show()
                }
            }

            addressTv.setOnClickListener {
                it.run {
                    val menuAddress = IncognitoUpdateUserView(this@IncognitoOwnerUpdateProfileAct,
                        object : IncognitoUpdateUserInterface {
                            override fun onClickSave() {
                            }

                            override fun onClickCancel() {
                            }

                        }, IncognitoUpdateType.EDIT_ADDRESS)
                    menuAddress.show()
                }
            }
        }
    }

    private fun navigateAvatarView(){

    }

    companion object {
        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, IncognitoOwnerUpdateProfileAct::class.java)
        }
    }
}