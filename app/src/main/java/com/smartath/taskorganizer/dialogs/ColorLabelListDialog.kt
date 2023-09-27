package com.smartath.taskorganizer.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartath.taskorganizer.adapters.ColorLabelListItemAdapter
import com.smartath.taskorganizer.databinding.DialogColorSelectionBinding

abstract class ColorLabelListDialog(context: Context,
                                    private var colors: ArrayList<String>,
                                    private val title: String = "",
                                    private val selectedColor: String = ""): Dialog(context) {

    private var adapter: ColorLabelListItemAdapter? = null
    private var binding: DialogColorSelectionBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogColorSelectionBinding.inflate(layoutInflater)
        setContentView(binding?.root!!)

        setCancelable(true)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView(){
        binding?.titleTv?.text = title
        binding?.colorsRv?.layoutManager = LinearLayoutManager(context)
        adapter = ColorLabelListItemAdapter(context, colors, selectedColor)

        binding?.colorsRv?.adapter = adapter

        adapter!!.onItemClickListener = object : ColorLabelListItemAdapter.OnItemClickListener{
            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }
        }
    }

    protected abstract fun onItemSelected(color: String)
}