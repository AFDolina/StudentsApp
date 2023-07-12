package ru.tinkoff.favouritepersons.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import ru.tinkoff.favouritepersons.R
import ru.tinkoff.favouritepersons.domain.Gender
import ru.tinkoff.favouritepersons.presentation.PersonErrorMessages
import ru.tinkoff.favouritepersons.presentation.viewmodel.PersonItemViewModel
import java.lang.RuntimeException

class PersonItemActivity : AppCompatActivity() {

    private lateinit var tilName: TextInputLayout
    private lateinit var twTitleScreen: TextView
    private lateinit var tilSurname: TextInputLayout
    private lateinit var tilGender: TextInputLayout
    private lateinit var tilBirthdate: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPhone: TextInputLayout
    private lateinit var tilAddress: TextInputLayout
    private lateinit var tilImage: TextInputLayout
    private lateinit var tilScore: TextInputLayout
    private lateinit var submitButton: Button

    private lateinit var viewModelPersonItem : PersonItemViewModel

    private var screenMode = ""
    private var personItemId = -1

    private fun initViews() {
        tilName = findViewById(R.id.til_name)
        twTitleScreen = findViewById(R.id.tw_person_screen_title)
        tilSurname = findViewById(R.id.til_surname)
        tilGender = findViewById(R.id.til_gender)
        tilBirthdate = findViewById(R.id.til_birthdate)
        tilEmail = findViewById(R.id.til_email)
        tilPhone = findViewById(R.id.til_phone)
        tilAddress = findViewById(R.id.til_address)
        tilImage = findViewById(R.id.til_image_link)
        tilScore = findViewById(R.id.til_score)
        submitButton = findViewById(R.id.submit_button)
    }

    inner class PersonFieldTextWatcher(val field : PersonErrorMessages) : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModelPersonItem.clearValidationOnField(field)
                //setErrorStatusToField(field, true)
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private fun implementTextWatchers(){
        tilName.editText?.addTextChangedListener(PersonFieldTextWatcher(PersonErrorMessages.NAME))
        tilSurname.editText?.addTextChangedListener(PersonFieldTextWatcher(PersonErrorMessages.SURNAME))
        tilGender.editText?.addTextChangedListener(PersonFieldTextWatcher(PersonErrorMessages.GENDER))
        tilBirthdate.editText?.addTextChangedListener(PersonFieldTextWatcher(PersonErrorMessages.BIRTHDATE))
        tilEmail.editText?.addTextChangedListener(PersonFieldTextWatcher(PersonErrorMessages.EMAIL))
        tilPhone.editText?.addTextChangedListener(PersonFieldTextWatcher(PersonErrorMessages.PHONE))
        tilAddress.editText?.addTextChangedListener(PersonFieldTextWatcher(PersonErrorMessages.ADDRESS))
        tilScore.editText?.addTextChangedListener(PersonFieldTextWatcher(PersonErrorMessages.RATING))
        tilImage.editText?.addTextChangedListener(PersonFieldTextWatcher(PersonErrorMessages.IMAGE_LINK))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.person_item_activity)
        parseIntent()
        viewModelPersonItem = ViewModelProvider(this)[PersonItemViewModel::class.java]
        initViews()
        implementTextWatchers()
        observeLiveData()

        when (screenMode) {
            MODE_EDIT -> launchEditMode()
            MODE_ADD -> launchAddMode()
        }
    }

    private fun observeLiveData(){
        viewModelPersonItem.errorMap.observe(this) {

            tilName.error = if(it[PersonErrorMessages.NAME] == false) PersonErrorMessages.NAME.errorMessage else null
            tilSurname.error = if(it[PersonErrorMessages.SURNAME] == false) PersonErrorMessages.SURNAME.errorMessage else null
            tilBirthdate.error = if(it[PersonErrorMessages.BIRTHDATE] == false) PersonErrorMessages.BIRTHDATE.errorMessage else null
            tilGender.error = if(it[PersonErrorMessages.GENDER] == false) PersonErrorMessages.GENDER.errorMessage else null
            tilEmail.error = if(it[PersonErrorMessages.EMAIL] == false) PersonErrorMessages.EMAIL.errorMessage else null
            tilPhone.error = if(it[PersonErrorMessages.PHONE] == false) PersonErrorMessages.PHONE.errorMessage else null
            tilAddress.error = if(it[PersonErrorMessages.ADDRESS] == false) PersonErrorMessages.ADDRESS.errorMessage else null
            tilImage.error = if(it[PersonErrorMessages.IMAGE_LINK] == false) PersonErrorMessages.IMAGE_LINK.errorMessage else null
            tilScore.error = if(it[PersonErrorMessages.RATING] == false) PersonErrorMessages.RATING.errorMessage else null
        }

        viewModelPersonItem.shouldCloseScreen.observe(this){
            finish()
        }
    }

    private fun launchEditMode() {
        twTitleScreen.setText(R.string.person_layout_titile_edit)
        viewModelPersonItem.getPersonItem(personItemId)
        viewModelPersonItem.personItem.observe(this) {
            tilName.editText?.setText(it.name)
            tilSurname.editText?.setText(it.surname)
            tilGender.editText?.setText( if(it.gender == Gender.FEMALE) "Ж" else "М")
            tilBirthdate.editText?.setText(it.birthDate)
            tilEmail.editText?.setText(it.email)
            tilPhone.editText?.setText(it.phone)
            tilAddress.editText?.setText(it.address)
            tilImage.editText?.setText(it.imageLink)
            tilScore.editText?.setText(it.rating.toString())
        }
        submitButton.setOnClickListener {
            viewModelPersonItem.editPersonItem(
                name = tilName.editText?.text.toString(),
                surname = tilSurname.editText?.text.toString(),
                birthDate = tilBirthdate.editText?.text.toString(),
                gender = tilGender.editText?.text.toString(),
                email = tilEmail.editText?.text.toString(),
                phone = tilPhone.editText?.text.toString(),
                address = tilAddress.editText?.text.toString(),
                rating = tilScore.editText?.text.toString(),
                imageLink = tilImage.editText?.text.toString()
            )
        }
    }

    fun prefillFormTestData(){
        tilName.editText?.setText("Alex")
        tilSurname.editText?.setText("Maks")
        tilBirthdate.editText?.setText("1990-04-29")
        tilEmail.editText?.setText("a.g@r.tp")
        tilPhone.editText?.setText("891919191")
        tilAddress.editText?.setText("Ul pushkina dom kolotushkina")
        tilImage.editText?.setText("https://cs8.pikabu.ru/post_img/2017/10/22/8/1508674727183869840.jpg")
        tilScore.editText?.setText("99")
    }

    private fun launchAddMode() {
        twTitleScreen.setText(R.string.person_layout_titile_add)
//        prefillFormTestData()

        submitButton.setOnClickListener {
            viewModelPersonItem.addPersonItem(
                name = tilName.editText?.text.toString(),
                surname = tilSurname.editText?.text.toString(),
                birthDate = tilBirthdate.editText?.text.toString(),
                gender = tilGender.editText?.text.toString(),
                email = tilEmail.editText?.text.toString(),
                phone = tilPhone.editText?.text.toString(),
                address = tilAddress.editText?.text.toString(),
                rating = tilScore.editText?.text.toString(),
                imageLink = tilImage.editText?.text.toString()
            )
        }
    }

    private fun parseIntent() {
        if (!intent.hasExtra(EXTRA_SCREEN_MODE))
            throw RuntimeException("Param SCREEN_MODE is absent")
        val mode = intent.getStringExtra(EXTRA_SCREEN_MODE)
        if (mode != MODE_ADD && mode != MODE_EDIT)
            throw RuntimeException("Unknown screen mode $mode")
        screenMode = mode

        if (screenMode == MODE_EDIT )
            if (!intent.hasExtra(EXTRA_PERSON_ITEM_ID))
                throw RuntimeException("Param EXTRA_PERSON_ITEM_ID is absent")
            personItemId = intent.getIntExtra(EXTRA_PERSON_ITEM_ID, -1)

    }

    companion object {

        private const val EXTRA_SCREEN_MODE = "extra_mode"
        private const val EXTRA_PERSON_ITEM_ID = "extra_person_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"

        fun newIntentAddItem(context: Context): Intent {
            val intent = Intent(context, PersonItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_ADD)
            return intent
        }

        fun newIntentEditItem(context: Context, shopItemId: Int): Intent {
            val intent = Intent(context, PersonItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_EDIT)
            intent.putExtra(EXTRA_PERSON_ITEM_ID, shopItemId)
            return intent
        }
    }
}