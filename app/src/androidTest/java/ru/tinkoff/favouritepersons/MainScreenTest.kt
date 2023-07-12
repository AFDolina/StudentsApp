package ru.tinkoff.favouritepersons

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.rules.RuleChain
import ru.tinkoff.favouritepersons.presentation.activities.MainActivity
import ru.tinkoff.favouritepersons.room.PersonDataBase
import ru.tinkoff.favouritepersons.rules.LocalhostPreferenceRule
import ru.tinkoff.favouritepersons.screens.EditStudentInfo
import ru.tinkoff.favouritepersons.screens.MainScreen
import ru.tinkoff.favouritepersons.utils.FavouritePersonsAppStubber
import ru.tinkoff.favouritepersons.utils.StudentInfo
import ru.tinkoff.favouritepersons.utils.fileToString
import java.time.LocalDate

class MainScreenTest : TestCase(kaspressoBuilder) {

    @Before
    fun createDb() {
        db = PersonDataBase.getDBClient(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @After
    fun clearDB() {
        db.personsDao().clearTable()
    }

    @get: Rule
    val ruleChain: RuleChain = RuleChain.outerRule(LocalhostPreferenceRule())
        .around(WireMockRule(5000))
        .around(ActivityScenarioRule(MainActivity::class.java))

    // Кейс 1. Проверка скрытия сообщения об отсутствии студентов
    @Test
    fun textNoPersonsIsNotDisplayedTest() {
        stubber.stubForApi("mock/get_person_by_network.json", studentAge.toString(), studentInfo)
        with(MainScreen()) {
            textNoPersons.isDisplayed()
            addPerson.click()
            addPersonByNetwork.click()
            textNoPersons.isNotDisplayed()
        }
    }

    // Кейс 2. Проверка удаления студента
    @Test
    fun deleteStudentTest() {
        stubber.stubForApi("mock/get_person_by_network.json", studentAge.toString(), studentInfo)
        with(MainScreen()) {
            addPerson.click()
            addPersonByNetwork.click()
            addPersonByNetwork.click()
            addPersonByNetwork.click()
            studentCard {
                hasSize(3)
            }
            deleteStudentCard()
            studentCard {
                hasSize(2)
            }
        }
    }

    // Кейс 3. Проверка выбора по умолчанию в окне сортировки
    @Test
    fun checkDefaultValueTest() {
        with(MainScreen()) {
            actionItemSort.click()
            rb_default.isChecked()
        }
    }

    // Кейс 4. Проверка сортировки по возрасту
    @Test
    fun checkSortByAgeTest() {
        stubFor(
            get(urlEqualTo("/api/"))
                .inScenario("checkSortByAgeTest")
                .whenScenarioStateIs(STARTED)
                .willSetStateTo("Step 2 - Add Person")
                .willReturn(
                    ok(
                        fileToString("mock/get_person_by_network.json")
                            .replace("\"STUDENT_AGE\"", "7")
                            .replace("STUDENT_NAME", studentInfo.name)
                            .replace("STUDENT_SURNAME", studentInfo.surname)
                            .replace("STUDENT_GENDER", "M")
                            .replace("STUDENT_BDATE", studentInfo.birthdate)
                    )
                )
        )
        stubFor(
            get(urlEqualTo("/api/"))
                .inScenario("checkSortByAgeTest")
                .whenScenarioStateIs("Step 2 - Add Person")
                .willSetStateTo("Step 3 - Add Person")
                .willReturn(
                    ok(
                        fileToString("mock/get_person_by_network.json")
                            .replace("\"STUDENT_AGE\"", "88")
                            .replace("STUDENT_NAME", studentInfo.name)
                            .replace("STUDENT_SURNAME", studentInfo.surname)
                            .replace("STUDENT_GENDER", "M")
                            .replace("STUDENT_BDATE", studentInfo.birthdate)
                    )
                )
        )
        stubFor(
            get(urlEqualTo("/api/"))
                .inScenario("checkSortByAgeTest")
                .whenScenarioStateIs("Step 3 - Add Person")
                .willReturn(
                    ok(
                        fileToString("mock/get_person_by_network.json")
                            .replace("\"STUDENT_AGE\"", "22")
                            .replace("STUDENT_NAME", studentInfo.name)
                            .replace("STUDENT_SURNAME", studentInfo.surname)
                            .replace("STUDENT_GENDER", "M")
                            .replace("STUDENT_BDATE", studentInfo.birthdate)
                    )
                )
        )
        with(MainScreen()) {
            addPerson.click()
            addPersonByNetwork.click()
            addPersonByNetwork.click()
            addPersonByNetwork.click()
            actionItemSort.click()
            rb_age.click()
            studentCard {
                firstChild<MainScreen.CardView> {
                    privateInfo.hasText("Male, 88")
                }
                childAt<MainScreen.CardView>(1) {
                    privateInfo.hasText("Male, 22")
                }
                childAt<MainScreen.CardView>(2) {
                    privateInfo.hasText("Male, 7")
                }

            }

        }
    }

    // Кейс 5. Проверка открытия второго экрана с данными пользователя
    @Test
    fun checkStudentInfoTest() {
        stubber.stubForApi("mock/get_person_by_network.json", studentAge.toString(), studentInfo)
        with(MainScreen()) {
            addPerson.click()
            addPersonByNetwork.click()
            studentCard {
                firstChild<MainScreen.CardView> {
                    click()
                }
            }
        }
        with(EditStudentInfo()) {
            checkStudentInfoFields(
                studentInfo.name,
                studentInfo.surname,
                studentInfo.gender,
                studentInfo.birthdate
            )
        }
    }

    // Кейс 6. Проверка редактирования студента
    @Test
    fun editStudentInfoTest() {
        stubber.stubForApi("mock/get_person_by_network.json", studentAge.toString(), studentInfo)
        with(MainScreen()) {
            addPerson.click()
            addPersonByNetwork.click()
            studentCard {
                firstChild<MainScreen.CardView> {
                    click()
                }
            }
        }
        with(EditStudentInfo()) {
            nameField.replaceText(NEW_STUDENT_NAME)
            submitButton.click()
        }
        with(MainScreen()) {
            studentCard {
                firstChild<MainScreen.CardView> {
                    name {
                        hasText("$NEW_STUDENT_NAME ${studentInfo.surname}")
                    }
                }
            }
        }
    }

    // Кейс 7. Проверка добавления студента
    @Test
    fun addNewStudentTest() {
        with(MainScreen()) {
            addPerson.click()
            addPersonManually.click()
        }
        with(EditStudentInfo()) {
            fillStudentInfo(studentInfo)
        }
        with(MainScreen()) {
            studentCard {
                firstChild<MainScreen.CardView> {
                    name.hasText("${studentInfo.name} ${studentInfo.surname}")
                    privateInfo.hasText("Male, ${studentAge}")
                    email.hasText(studentInfo.email)
                    phone.hasText(studentInfo.phone)
                    address.hasText(studentInfo.address)
                    rating.hasText(studentInfo.score)
                }
            }
        }
    }

    // Кейс 8. Проверка отображения сообщения об ошибке
    @Test
    fun checkGenderFieldErrorTest() {
        with(MainScreen()) {
            addPerson.click()
            addPersonManually.click()
        }
        with(EditStudentInfo()) {
            submitButton.click()
            genderFieldError.hasText("Поле должно быть заполнено буквами М или Ж")
        }
    }

    // Кейс 9. Проверка скрытия сообщения об ошибке при вводе данных в поле
    @Test
    fun checkHidingGenderFieldErrorTest() {
        with(MainScreen()) {
            addPerson.click()
            addPersonManually.click()
        }
        with(EditStudentInfo()) {
            genderField.replaceText("3")
            submitButton.click()
            genderFieldError.hasText("Поле должно быть заполнено буквами М или Ж")
            genderField.replaceText("")
            genderFieldError.isNotDisplayed()
        }
    }

    // Кейс 10. Проверка отображения сообщения об ошибке интернет-соединения
    @Test
    fun checkNoInternetConnectionErrorTest() {
        stubFor(
            get(urlEqualTo("/api/"))
                .willReturn(
                    aResponse()
                        .withStatus(503)
                )
        )
        with(MainScreen()) {
            addPerson.click()
            addPersonByNetwork.click()
            snackbarText.hasText("Internet error! Check your connection")
        }
    }

    private companion object {
        private val stubber = FavouritePersonsAppStubber()
        private lateinit var db: PersonDataBase
        private val studentAge = (14..99).random()
        private val studentInfo = StudentInfo(
            name = "Ivan",
            surname = "Fedorov",
            gender = "М",
            birthdate = LocalDate.now().minusYears(studentAge.toLong()).toString(),
            email = "IvanFedorov@gmail.com",
            phone = "78789978",
            address = "Москва, Ленина 1",
            image = "https://ru.wikipedia.org/wiki/Грут_%28Кинематографическая_вселенная_Marvel%29",
            score = "87"
        )
        const val NEW_STUDENT_NAME = "NEWAnastasiia"
    }
}