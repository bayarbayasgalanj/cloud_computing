package com.colworx.soccorapp.ui.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.colworx.soccorapp.Constants
import com.colworx.soccorapp.api.*
import com.colworx.soccorapp.databinding.FragmentMemberBinding
import com.colworx.soccorapp.ui.ApiHelper

class MemberFragment : Fragment() {

    private var _binding: FragmentMemberBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMemberBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.edtMemberName.setText("Test member1")

        binding.btnGetLikes.setOnClickListener {
            getLikes()
        }

        binding.btnGetComment.setOnClickListener {
            getComments()
        }

        binding.btnPostComment.setOnClickListener {
            addComment()
        }

        binding.btnAddLike.setOnCheckedChangeListener { compoundButton, b ->
            addLikes()
        }

        binding.btntest.setOnClickListener {
            testApi()
        }

        return root
    }

    private fun testApi() {
        val call = Constants.api().testMemberApi()
        ApiHelper.performApi(call, binding.result)
    }

    private fun addComment() {
        val memberName = binding.edtMemberName.text.toString()
        val call = Constants.api().addComments(Constants.token, CommentModel("test comment", memberName))
        ApiHelper.performApi(call, binding.result)
    }

    private fun addLikes() {
        val memberName = binding.edtMemberName.text.toString()
        val call = Constants.api().addVotes(Constants.token, VotesModel(binding.btnAddLike.isChecked, memberName))
        ApiHelper.performApi(call, binding.result)
    }

    private fun getLikes() {
        val call = Constants.api().getVotes(Constants.gameId)
        ApiHelper.performApi(call, binding.result)
    }


    private fun getComments() {
        val call = Constants.api().getComments(Constants.gameId)
        ApiHelper.performApi(call, binding.result)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}