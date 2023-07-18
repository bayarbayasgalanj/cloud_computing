package com.colworx.soccorapp.ui.sport


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.colworx.soccorapp.Constants
import com.colworx.soccorapp.api.AddGameParam
import com.colworx.soccorapp.api.ScoreParams
import com.colworx.soccorapp.databinding.FragmentSportBinding
import com.colworx.soccorapp.ui.ApiHelper
import com.colworx.soccorapp.ui.DialogHelper
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SportFragment : Fragment() {

    private var _binding: FragmentSportBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentSportBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (Constants.isAdmin.not()) {
            binding.btnDeleteAllGame.visibility = View.GONE
            binding.btnStartGame.visibility = View.GONE
            binding.btnStopGame.visibility = View.GONE
        }

        binding.btnStartGame.setOnClickListener {
            startGame()
        }

        binding.btnStopGame.setOnClickListener {
            stopGame()
        }

        binding.btnDeleteAllGame.setOnClickListener {
            deleteAllGame()
        }

        binding.btngetAllGames.setOnClickListener {
            getAllGames()
        }

        binding.btnGetLiveGame.setOnClickListener {
            getLiveGame()
        }


        binding.btntest.setOnClickListener {
            testApi()
        }

        binding.btnAddGame.setOnClickListener {
            addNewGame()
        }

        binding.btnSetScore.setOnClickListener {
            DialogHelper.showDialog2Input(this.requireActivity()) { a,b ->
                setScore(a, b)
            }
        }

        return root
    }

    private fun setScore(home: Int, visit: Int) {
        val call = Constants.api().setScore(Constants.token, ScoreParams(home, visit))
        ApiHelper.performApi(call, binding.result)
    }

    private fun testApi() {
        val call = Constants.api().testSportApi()
        ApiHelper.performApi(call, binding.result)
    }

    private fun startGame() {
        val call = Constants.api().startGame(Constants.token)

        ApiHelper.performApi(call, binding.result)
    }

    private fun getCtx() = this.requireActivity()

    private fun stopGame() {
        val call = Constants.api().stopGame(Constants.token)
        ApiHelper.performApi(call, binding.result)
    }

    private fun deleteAllGame() {
        val call = Constants.api().deleteAllGames(Constants.token)
        ApiHelper.performApi(call, binding.result)
    }


    private fun getAllGames() {
        val call = Constants.api().getAllGames(Constants.token)

        call!!.enqueue(object : Callback<ResponseBody?> {

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.code() == 200) {
                    var result = response.body()?.string() ?: "error"
                    binding.result.setText("success  = " + result)
                    if (result != "error") {
                        try {
                            val dict = JSONObject(result)
                            val dataDict = dict.getJSONArray("data")
                            if (dataDict.length() > 0) {
                                val obj = dataDict.getJSONObject(0)
                                Constants.gameId = obj.getString("gameId")
                                DialogHelper.showToast(
                                    getCtx(),
                                    "Game id saved \n " + Constants.gameId
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }
                } else {
                    binding.result.setText("failed")
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                binding.result.setText(t.localizedMessage ?: "- error")
            }
        })
    }

    private fun addNewGame() {
        val rnds = (1..100).random().toString()
        val call = Constants.api().addGame(
            Constants.token,
            AddGameParam("league $rnds", "team name $rnds", "visitor $rnds")
        )

        call!!.enqueue(object : Callback<ResponseBody?> {

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.code() == 200) {
                    var result = response.body()?.string() ?: "error"
                    binding.result.setText("success  = " + result)
                    if (result != "error") {
                        try {
                            val dict = JSONObject(result)
                            val dataDict = dict.getJSONObject("data")
                            Constants.gameId = dataDict.getString("gameId")
                            DialogHelper.showToast(getCtx(), "Game id saved \n " + Constants.gameId)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                } else {
                    binding.result.setText("failed")
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                binding.result.setText(t.localizedMessage ?: "- error")
            }
        })
    }

    private fun getLiveGame() {
        val call = Constants.api().getLiveGame(Constants.token)

        call!!.enqueue(object : Callback<ResponseBody?> {

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.code() == 200) {
                    var result = response.body()?.string() ?: "error"
                    binding.result.setText("success  = " + result)
                    if (result != "error") {
                        try {
                            val dict = JSONObject(result)
                            val dataDict = dict.getJSONObject("data")
                            Constants.gameId = dataDict.getString("gameId")
                            DialogHelper.showToast(getCtx(), "Game id saved \n " + Constants.gameId)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                } else {
                    binding.result.setText("failed")
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                binding.result.setText(t.localizedMessage ?: "- error")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}