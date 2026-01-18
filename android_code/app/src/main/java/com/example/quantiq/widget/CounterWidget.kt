package com.example.quantiq.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.Button
import androidx.glance.currentState
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.quantiq.ui.theme.QuantiqTheme

// Keys for storing widget state
object WidgetKeys {
    val counterId = intPreferencesKey("counter_id")
    val counterTitle = stringPreferencesKey("counter_title")
    val counterValue = intPreferencesKey("counter_value")
}

class CounterWidget : GlanceAppWidget() {
    
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
            val title = prefs[WidgetKeys.counterTitle] ?: "Select Counter"
            val value = prefs[WidgetKeys.counterValue] ?: 0
            
            QuantiqTheme {
                WidgetContent(title, value)
            }
        }
    }

    @Composable
    fun WidgetContent(title: String, value: Int) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(day = android.graphics.Color.WHITE, night = android.graphics.Color.DKGRAY))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = TextStyle(color = ColorProvider(android.graphics.Color.BLACK)))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    text = "-",
                    onClick = actionRunCallback<WidgetAction>(
                        actionParametersOf(WidgetAction.ActionKey to WidgetAction.DECREMENT)
                    )
                )
                Spacer(modifier = GlanceModifier.width(16.dp))
                Text(
                    text = value.toString(),
                    style = TextStyle(fontSize = 32.sp)
                )
                Spacer(modifier = GlanceModifier.width(16.dp))
                Button(
                    text = "+",
                    onClick = actionRunCallback<WidgetAction>(
                        actionParametersOf(WidgetAction.ActionKey to WidgetAction.INCREMENT)
                    )
                )
            }
        }
    }
}

class CounterWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CounterWidget()
}

class WidgetAction : ActionCallback {
    companion object {
        const val INCREMENT = "increment"
        const val DECREMENT = "decrement"
        val ActionKey = ActionParameters.Key<String>("action")
    }

    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val action = parameters[ActionKey]
        
        // 1. Get current Widget State
        // 2. Update Room Database via Repository
        // 3. Update Widget State with new value
        // 4. Trigger update
        
        updateAppWidgetState(context, glanceId) { prefs ->
             // Logic to update shared prefs or sync with Room would go here
             // For this skeleton, we just mock the update locally in prefs
             val current = prefs[WidgetKeys.counterValue] ?: 0
             if (action == INCREMENT) {
                 prefs[WidgetKeys.counterValue] = current + 1
             } else {
                 prefs[WidgetKeys.counterValue] = current - 1
             }
        }
        CounterWidget().update(context, glanceId)
    }
}
