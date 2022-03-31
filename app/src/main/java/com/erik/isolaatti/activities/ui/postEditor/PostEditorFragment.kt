package com.erik.isolaatti.activities.ui.postEditor

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.erik.isolaatti.R
import com.erik.isolaatti.activities.PostEditorTheme

class PostEditorFragment : Fragment() {

    companion object {
        fun newInstance() = PostEditorFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.post_editor_main_fragment2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel

        activity?.findViewById<ImageButton>(R.id.setThemeButton)?.setOnClickListener {
            requireActivity().startActivity(Intent(context,PostEditorTheme::class.java))
        }
    }

}