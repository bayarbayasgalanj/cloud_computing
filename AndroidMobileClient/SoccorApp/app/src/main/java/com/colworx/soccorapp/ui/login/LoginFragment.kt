package com.colworx.soccorapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.colworx.soccorapp.api.AuthRequestParam
import com.colworx.soccorapp.Constants
import com.colworx.soccorapp.api.ResponseAuth
import com.colworx.soccorapp.databinding.FragmentLoginBinding
import com.colworx.soccorapp.ui.ApiHelper
import retrofit2.*

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root


        binding.btnLogin.setOnClickListener {
            testLoginAuth()
        }

        binding.switch1.setOnCheckedChangeListener { compoundButton, b ->
            Constants.isAdmin = binding.switch1.isChecked
        }
        
        binding.btnValidateAdmin.setOnClickListener { 
            testValidateIsAdmin()
        }

        binding.btnValidateToken.setOnClickListener {
            testValidateToken()
        }

        return root
    }


    private fun testLoginAuth() {

        var user: String = if (Constants.isAdmin) "useradmin@miu.edu" else "user@miu.edu"

        val call = Constants.api().authLogin(AuthRequestParam(user, "password"))

        call!!.enqueue(object : Callback<ResponseAuth?> {

            override fun onResponse(call: Call<ResponseAuth?>, response: Response<ResponseAuth?>) {

                    var res = response.body()?.email ?: "error"
                    res += "\n\n"
                    res += response.body()?.accessToken ?: "error"
                    Constants.token = response.body()?.accessToken ?: "error"
                    binding.result.setText(res)
            }

            override fun onFailure(call: Call<ResponseAuth?>, t: Throwable) {
                binding.result.setText(t.localizedMessage ?: "- error")
            }

        })
    }

    private fun testValidateIsAdmin() {
        val call = Constants.api().isAdmin(Constants.token)
        ApiHelper.performApi(call, binding.result)
    }



    private fun testValidateToken() {
        val call = Constants.api().validateToken(Constants.token)
        ApiHelper.performApi(call, binding.result)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}