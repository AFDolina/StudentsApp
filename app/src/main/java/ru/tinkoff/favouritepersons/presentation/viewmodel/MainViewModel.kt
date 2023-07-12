package ru.tinkoff.favouritepersons.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.tinkoff.favouritepersons.data.PersonListRepositoryImpl
import ru.tinkoff.favouritepersons.data.network.PersonsMapper.personToPersonItem
import ru.tinkoff.favouritepersons.data.network.RetrofitService
import ru.tinkoff.favouritepersons.data.network.getBaseUrlForService
import ru.tinkoff.favouritepersons.domain.AddPersonItemUseCase
import ru.tinkoff.favouritepersons.domain.DeletePersonItemUseCase
import ru.tinkoff.favouritepersons.domain.GetPersonItemFromNetworkUseCase
import ru.tinkoff.favouritepersons.domain.GetPersonListUseCase
import ru.tinkoff.favouritepersons.domain.PersonItem
import ru.tinkoff.favouritepersons.presentation.PersonFields
import ru.tinkoff.favouritepersons.room.PersonDataBase

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val personList : LiveData<List<PersonItem>> get() = personListMediator

    private var personListMediator : MediatorLiveData<List<PersonItem>>
    private var personListSource : LiveData<List<PersonItem>>

    val currentSortMethod = MutableLiveData(PersonFields.NO_METHOD)
    val isLoading = MutableLiveData<Boolean>()
    val usersLoadError = MutableLiveData<String>()

    private val getPersonListUseCase: GetPersonListUseCase
    private val deletePersonItemUseCase: DeletePersonItemUseCase
    private val addPersonItemUseCase: AddPersonItemUseCase
    private val getPersonItemFromNetworkUseCase: GetPersonItemFromNetworkUseCase
    private var job: Job

    init {
        job  = Job()
        val randomUserApiService = RetrofitService.getInstance(getBaseUrlForService(application))
        val dao = PersonDataBase.getDBClient(application).personsDao()
        val repository = PersonListRepositoryImpl.getInstance(dao, randomUserApiService)

        getPersonListUseCase = GetPersonListUseCase(repository)
        deletePersonItemUseCase = DeletePersonItemUseCase(repository)
        addPersonItemUseCase = AddPersonItemUseCase(repository)
        getPersonItemFromNetworkUseCase = GetPersonItemFromNetworkUseCase(repository)

        personListMediator = MediatorLiveData<List<PersonItem>>()
        personListSource = getPersonListUseCase.getPersonList().asLiveData()
        personListMediator.addSource(personListSource) {
            personListMediator.postValue(it.sortByDescending(currentSortMethod.value ?: PersonFields.NO_METHOD))
        }
        personListMediator.addSource(currentSortMethod) { method ->
            personListMediator.value?.let { personList ->
                personListMediator.postValue(personList.sortByDescending(method))
            }
        }
    }

    private val handler = CoroutineExceptionHandler { response, exception ->
        usersLoadError.postValue("response.toString()")
        isLoading.postValue( false)
    }

    fun getRandomPers() {
        isLoading.value = true
        val uiScope = CoroutineScope(Dispatchers.IO + job + handler)

        uiScope.launch {
            val response = getPersonItemFromNetworkUseCase.getPersonItem()
            if (response.isSuccessful) {
                addPersonItem(personToPersonItem(response.body()?.results?.first()).first() )
                usersLoadError.postValue("")
                isLoading.postValue( false)
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }


    fun deletePersonItem(personItem: PersonItem) = viewModelScope.launch(Dispatchers.IO) {
        deletePersonItemUseCase.deletePersonItem(personItem)
    }
    fun addPersonItem(personItem: PersonItem) = viewModelScope.launch(Dispatchers.IO) {
        addPersonItemUseCase.addPersonItem(personItem)
    }

    fun setSortMethod(method : PersonFields) {
        currentSortMethod.value = method
    }

    private fun List<PersonItem>.sortByDescending(sortingMethod: PersonFields): List<PersonItem> {
        return (this as MutableList<PersonItem>).apply {
            when (sortingMethod) {
                PersonFields.BY_SURNAME -> sortByDescending { it.surname }
                PersonFields.BY_RATING -> sortByDescending { it.rating }
                PersonFields.BY_AGE -> sortByDescending { it.age }
                PersonFields.NO_METHOD -> sortByDescending { it.id }
            }
        }.toList()
    }
}