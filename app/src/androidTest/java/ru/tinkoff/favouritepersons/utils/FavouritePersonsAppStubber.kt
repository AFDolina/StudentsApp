package ru.tinkoff.favouritepersons.utils

import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo

class FavouritePersonsAppStubber {
    private val url = "/api/"

    fun stubForApi(mockPath: String, age: String, studentInfo: StudentInfo) {
        val gender = if (studentInfo.gender == "М") "male" else "female"
        stubFor(
            get(
                urlEqualTo(url)
            )
                .willReturn(
                    ok(
                        fileToString(mockPath)
                            .replace("\"STUDENT_AGE\"", age)
                            .replace("STUDENT_NAME", studentInfo.name)
                            .replace("STUDENT_SURNAME", studentInfo.surname)
                            .replace("STUDENT_GENDER", gender)
                            .replace("STUDENT_BDATE", studentInfo.birthdate)
                    )
                )
        )
    }
}
