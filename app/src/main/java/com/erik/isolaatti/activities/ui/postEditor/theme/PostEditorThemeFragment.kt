package com.erik.isolaatti.activities.ui.postEditor.theme

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.erik.isolaatti.R

class PostEditorThemeFragment : Fragment() {

    companion object {
        fun newInstance() = PostEditorThemeFragment()
    }

    private lateinit var viewModel: PostEditorThemeMainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.post_editor_theme_main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PostEditorThemeMainViewModel::class.java)
        // TODO: Use the ViewModel
    }

}