package com.project.wifikeymaster.Fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.wifikeymaster.ModelClasses.SavedDetailsModelClass
import com.project.wifikeymaster.Adaptors.SavedPasswordAdaptor
import com.project.wifikeymaster.Interface.SavedPasswordInterface
import com.project.wifikeymaster.databinding.FragmentSavePasswordBinding
import io.paperdb.Paper
import java.util.concurrent.Executors

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SavePasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SavePasswordFragment : Fragment() ,SavedPasswordInterface{

    lateinit var binding:FragmentSavePasswordBinding
    var mExecutorService = Executors.newSingleThreadExecutor()
    var mhandler = Handler(Looper.getMainLooper())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSavePasswordBinding.inflate(layoutInflater,container,false)

        mExecutorService.execute{
            binding.progressBar.visibility=View.VISIBLE
            //saved passwords list from paper database
            var savedPassList= Paper.book().read("saved_password_db",ArrayList<SavedDetailsModelClass>())

            mhandler.post{
                binding.progressBar.visibility=View.GONE
                if(savedPassList!!.size.equals(0)) {
                    binding.nofileIV.visibility=View.VISIBLE
                    binding.filesRV.visibility=View.GONE
                }
                else {
                    //setting adaptor for saved passwords and wifi info
                    binding.nofileIV.visibility=View.GONE
                    binding.filesRV.layoutManager = LinearLayoutManager(requireContext())
                    binding.filesRV.visibility = View.VISIBLE
                    var adaptor = SavedPasswordAdaptor(requireActivity(), savedPassList!!,this)
                    binding.filesRV.adapter = adaptor
                }
            }
        }




        // Inflate the layout for this fragment
        return binding.root
    }

    override fun noFileImageView(b: Boolean) {
        if (b){
            binding.nofileIV.visibility=View.VISIBLE
            binding.filesRV.visibility=View.GONE
        }
    }



}