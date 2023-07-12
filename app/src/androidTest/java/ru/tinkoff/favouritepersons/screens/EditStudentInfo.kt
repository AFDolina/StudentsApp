package ru.tinkoff.favouritepersons.screens

import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import ru.tinkoff.favouritepersons.R
import ru.tinkoff.favouritepersons.utils.StudentInfo

class EditStudentInfo : BaseScreen() {
    val nameField = KEditText { withId(R.id.et_name) }
    val surnameField = KEditText { withId(R.id.et_surname) }
    val genderField = KEditText { withId(R.id.et_gender) }
    val birthdateField = KEditText { withId(R.id.et_birthdate) }
    val emailField = KEditText { withId(R.id.et_email) }
    val phoneField = KEditText { withId(R.id.et_phone) }
    val addressField = KEditText { withId(R.id.et_address) }
    val imageLinkField = KEditText { withId(R.id.et_image) }
    val scoreField = KEditText { withId(R.id.et_score) }
    val genderFieldError = KTextView {
        isInstanceOf(AppCompatTextView::class.java)
        isDescendantOfA { withId(R.id.til_gender) }
        withParent { isInstanceOf(FrameLayout::class.java) }
    }
    val submitButton = KButton { withId(R.id.submit_button) }

    fun fillStudentInfo(studentInfo: StudentInfo) {
        nameField.replaceText(studentInfo.name)
        surnameField.replaceText(studentInfo.surname)
        genderField.replaceText(studentInfo.gender)
        birthdateField.replaceText(studentInfo.birthdate)
        emailField.replaceText(studentInfo.email)
        phoneField.replaceText(studentInfo.phone)
        addressField.replaceText(studentInfo.address)
        imageLinkField.replaceText(studentInfo.image)
        scoreField.replaceText(studentInfo.score)
        submitButton.click()
    }

    fun checkStudentInfoFields(
        name: String,
        surname: String,
        gender: String,
        birthdate: String
    ) {
        nameField.hasText(name)
        surnameField.hasText(surname)
        genderField.hasText(gender)
        birthdateField.hasText(birthdate)
    }

    companion object {
        inline operator fun invoke(crossinline block: EditStudentInfo.() -> Unit) =
            EditStudentInfo().block()
    }
}
