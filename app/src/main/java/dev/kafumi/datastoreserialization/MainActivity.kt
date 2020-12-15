package dev.kafumi.datastoreserialization

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dev.kafumi.datastoreserialization.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spinnerAdapter = ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            MyData.MyEnum.values().map { it.name }
        )
        binding.spinner.adapter = spinnerAdapter

        binding.saveButton.setOnClickListener {
            val myBooleanValue = binding.switchWidget.isChecked
            val myStringValue = binding.editText.text.toString()
            val myEnumValue = MyData.MyEnum.values()[binding.spinner.selectedItemPosition]
            viewModel.updateMyData(MyData(myBooleanValue, myStringValue, myEnumValue))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.myData.collect { data ->
                if (data != null) {
                    binding.switchWidget.isChecked = data.myBooleanValue
                    binding.editText.setText(data.myStringValue)
                    binding.spinner.setSelection(data.myEnumValue.ordinal)
                }
            }
        }
    }
}