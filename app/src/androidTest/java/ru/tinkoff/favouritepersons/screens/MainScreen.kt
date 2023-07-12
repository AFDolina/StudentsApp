package ru.tinkoff.favouritepersons.screens

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.snackbar.SnackbarContentLayout
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.switch.KSwitch
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher
import ru.tinkoff.favouritepersons.R


class MainScreen : BaseScreen() {
    val addPerson = KButton { withId(R.id.fab_add_person) }
    val addPersonByNetwork = KButton { withId(R.id.fab_add_person_by_network) }
    val addPersonManually = KButton { withId(R.id.fab_add_person_manually) }
    val actionItemSort = KButton { withId(R.id.action_item_sort) }
    val snackbarText = KTextView {
        isInstanceOf(AppCompatTextView::class.java)
        withParent { isInstanceOf(SnackbarContentLayout::class.java) }
    }
    val rb_default = KSwitch { withId(R.id.bsd_rb_default) }
    val rb_age = KSwitch { withId(R.id.bsd_rb_age) }
    val rb_rating = KSwitch { withId(R.id.bsd_rb_rating) }
    val rb_name = KSwitch { withId(R.id.bsd_rb_name) }
    val textNoPersons = KTextView { withId(R.id.tw_no_persons) }

    val studentCard = KRecyclerView(
        { withId(R.id.rv_person_list) },
        itemTypeBuilder = {
            itemType(::CardView)
        })

    class CardView(parent: Matcher<View>) : KRecyclerItem<CardView>(parent) {
        val avatar = KTextView(parent) { withId(R.id.person_avatar) }
        val name = KTextView(parent) { withId(R.id.person_name) }
        val privateInfo = KTextView(parent) { withId(R.id.person_private_info) }
        val email = KTextView(parent) { withId(R.id.person_email) }
        val phone = KButton(parent) { withId(R.id.person_phone) }
        val address = KButton(parent) { withId(R.id.person_address) }
        val rating = KButton(parent) { withId(R.id.person_rating) }
    }

    fun deleteStudentCard() {
        studentCard { swipeLeft() }
    }

    companion object {
        inline operator fun invoke(crossinline block: MainScreen.() -> Unit) =
            MainScreen().block()
    }
}