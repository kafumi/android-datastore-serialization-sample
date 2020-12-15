package dev.kafumi.datastoreserialization

import android.app.Application
import androidx.datastore.createDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = application.createDataStore(
        fileName = "my_data.json",
        serializer = MyDataSerializer(),
    )

    private val _myData = MutableStateFlow<MyData?>(null)
    val myData = _myData.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.data.collect {
                _myData.emit(it)
            }
        }
    }

    fun updateMyData(newData: MyData) {
        viewModelScope.launch {
            dataStore.updateData { newData }
        }
    }
}