package ru.tinkoff.favouritepersons

import com.kaspersky.components.alluresupport.interceptors.step.AllureMapperStepInterceptor
import com.kaspersky.kaspresso.interceptors.watcher.testcase.impl.views.DumpViewsInterceptor
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.params.FlakySafetyParams
import ru.tinkoff.favouritepersons.room.PersonDataBase

val kaspressoBuilder = Kaspresso.Builder.simple(
    customize = {
        flakySafetyParams = FlakySafetyParams.custom(timeoutMs = 10_000, intervalMs = 100)
    }

).apply {
        testRunWatcherInterceptors.addAll(listOf(DumpViewsInterceptor(viewHierarchyDumper)))
        stepWatcherInterceptors.addAll(listOf(AllureMapperStepInterceptor()))
    }