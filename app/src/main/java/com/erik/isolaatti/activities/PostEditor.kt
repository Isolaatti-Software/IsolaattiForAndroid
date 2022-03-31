package com.erik.isolaatti.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.erik.isolaatti.R

import com.erik.isolaatti.activities.ui.postEditor.PostEditorFragment

class PostEditor : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_editor_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PostEditorFragment.newInstance())
                .commitNow()
        }
    }
}