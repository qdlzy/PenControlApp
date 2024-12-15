package com.example.pencontrolapp.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.EditText
import com.example.pencontrolapp.MainActivity
import com.example.pencontrolapp.databinding.DialogCustomKeysBinding
import com.example.pencontrolapp.utils.getPrefString
import com.example.pencontrolapp.utils.putPrefString
import com.example.pencontrolapp.R

class CustomKeyDialog(context: Context) : Dialog(context) {

    private val binding = DialogCustomKeysBinding.inflate(layoutInflater)

    override fun onStart() {
        super.onStart()
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9f).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    init {
        setContentView(binding.root)
        binding.etPrev.setText(context.getPrefString("customPrevKey"))
        binding.etNext.setText(context.getPrefString("customNextKey"))

        binding.tvReset.setOnClickListener {
            binding.etPrev.setText("")
            binding.etNext.setText("")
        }

        binding.tvOk.setOnClickListener {
            val prevKeysStr = binding.etPrev.text?.toString()?.trim().orEmpty()
            val nextKeysStr = binding.etNext.text?.toString()?.trim().orEmpty()

            // 将字符串用逗号分隔解析成列表
            val prevKeys = prevKeysStr.split(",").filter { it.isNotBlank() }
            val nextKeys = nextKeysStr.split(",").filter { it.isNotBlank() }

            // 检查是否存在相同的键值
            val commonKeys = prevKeys.intersect(nextKeys.toSet())
            if (commonKeys.isNotEmpty()) {
                // 存在相同的按键值，弹出提示对话框
                AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.error))
                    .setMessage(context.getString(R.string.duplicate_keys_error))
                    .setPositiveButton(context.getString(R.string.ok), null)
                    .show()
                return@setOnClickListener
            }

            // 如果检查通过，则保存并关闭对话框
            context.putPrefString("customPrevKey", prevKeysStr)
            context.putPrefString("customNextKey", nextKeysStr)
            dismiss()
        }
    }

    override fun show() {
        super.show()
        MainActivity.isCustomDialogShowing = true
        binding.etPrev.requestFocus()
    }

    override fun dismiss() {
        super.dismiss()
        MainActivity.isCustomDialogShowing = false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_DEL) {
            val focusedView = currentFocus
            if (focusedView is EditText) {
                val editableText = focusedView.editableText
                if (editableText.isEmpty() || editableText.endsWith(",")) {
                    editableText.append(keyCode.toString())
                } else {
                    editableText.append(",").append(keyCode.toString())
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
