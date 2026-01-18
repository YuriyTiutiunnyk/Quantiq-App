package com.example.quantiq.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
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
import androidx.glance.material3.ColorProviders
import com.example.quantiq.ui.theme.QuantiqTheme

// Simple Widget Skeleton using Jetpack Glance
class CounterWidget : GlanceAppWidget() {
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // In real app, fetch data from DataStore/Room here
        val mockValue = 42
        val mockTitle = "Pushups"

        provideContent {
            QuantiqTheme {
                WidgetContent(mockTitle, mockValue)
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
                    onClick = actionRunCallback<DecrementAction>()
                )
                Spacer(modifier = GlanceModifier.width(16.dp))
                Text(
                    text = value.toString(),
                    style = TextStyle(fontSize = 32.sp)
                )
                Spacer(modifier = GlanceModifier.width(16.dp))
                Button(
                    text = "+",
                    onClick = actionRunCallback<IncrementAction>()
                )
            }
        }
    }
}

class CounterWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CounterWidget()
}

class IncrementAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        // Update database + refresh widget
    }
}

class DecrementAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        // Update database + refresh widget
    }
}
