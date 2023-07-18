package com.colworx.soccorapp.ui.setting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.colworx.soccorapp.Constants
import com.colworx.soccorapp.databinding.FragmentSettingBinding
import com.colworx.soccorapp.ui.DialogHelper

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.txtDomainurl.setText("Domain URL is =\n" + Constants.DOMAIN_URL)

        binding.txtAccesstoken.setText("AccessToken is \n" + Constants.token)

       binding.txtGameId.setText("GameId is \n" + Constants.gameId)

        binding.btnLogin.setOnClickListener {

            Amplify.Auth.signIn("soccerApp", "Password123",
                { result ->
                    if (result.isSignedIn) {
                        Log.i("AuthQuickstart", "Sign in succeeded")
                        DialogHelper.showToast(this.requireActivity()!!, "Sign in succeeded")
                    } else {
                        Log.i("AuthQuickstart", "Sign in not complete")
                        DialogHelper.showToast(this.requireActivity()!!, "Sign in not complete")
                    }
                },
                { Log.e("AuthQuickstart", "Failed to sign in", it)
                    DialogHelper.showToast(this.requireActivity()!!, "Failed to sign in")
                }
            )
        }

        binding.btnSignup.setOnClickListener {

            DialogHelper.showAlertWithEmailInput(this.requireContext()) {
                performSignupEmail(it)
            }


        }

        binding.btnConfirm.setOnClickListener {
            DialogHelper.showAlertWithOTPInput(this.requireActivity()!!) {
                validateOTP(it)
            }
        }
        return root
    }

    private fun performSignupEmail(email: String) {
        val options = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.email(), email)
            .build()
        Amplify.Auth.signUp("soccerApp", "Password123", options,
            { Log.i("AuthQuickStart", "Sign up succeeded: $it")
                DialogHelper.showToast(this.requireActivity()!!, "Sign up succeeded")
            },
            { Log.e ("AuthQuickStart", "Sign up failed", it)
                DialogHelper.showToast(this.requireActivity()!!, "Sign up failed")
            }
        )
    }

    private fun validateOTP(otp: String) {
        Amplify.Auth.confirmSignUp(
            "soccerApp", otp,
            { result ->
                if (result.isSignUpComplete) {
                    Log.i("AuthQuickstart", "Confirm signUp succeeded")
                    DialogHelper.showToast(this.requireActivity()!!, "Confirm signUp succeeded")
                } else {
                    Log.i("AuthQuickstart","Confirm sign up not complete")
                    DialogHelper.showToast(this.requireActivity()!!, "Confirm sign up not complete")
                }
            },
            { Log.e("AuthQuickstart", "Failed to confirm sign up", it)
                DialogHelper.showToast(this.requireActivity()!!, "Failed confirm signup")
            }
        )
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}