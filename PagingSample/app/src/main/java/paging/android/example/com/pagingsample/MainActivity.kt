/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package paging.android.example.com.pagingsample

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.KeyEvent
import kotlinx.android.synthetic.main.activity_main.*
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.inputmethod.EditorInfo



class MainActivity : AppCompatActivity() {
    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(CheeseViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = CheeseAdapter()
        cheeseList.adapter = adapter
        viewModel.allCheeses.observe(this, Observer {
            adapter.setList(it)
        })
        initAddListener()
        initSwipeToDelete()
    }

    private fun initSwipeToDelete() {
        object : ItemTouchHelper(object : Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int =
                    makeMovementFlags(0, LEFT or RIGHT)

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                (viewHolder as? CheeseViewHolder)?.cheese?.let {
                    viewModel.remove(it)
                }
            }
        }) {}.attachToRecyclerView(cheeseList)
    }

    private fun initAddListener() {
        fun addCheese() {
            val newCheese = inputText.text.trim()
            if (newCheese.isNotEmpty()) {
                viewModel.insert(newCheese)
                inputText.setText("")
            }
        }
        addButton.setOnClickListener {
            addCheese()
        }
        inputText.setOnEditorActionListener({ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                addCheese()
            }
            false
        })
        inputText.setOnKeyListener({ _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                addCheese()
            }
            false
        })
    }
}
