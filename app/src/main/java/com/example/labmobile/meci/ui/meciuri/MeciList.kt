package com.example.labmobile.meci.ui.meciuri

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labmobile.meci.data.Meci
import java.text.SimpleDateFormat
import java.util.Locale

typealias OnMeciFn = (id: String?) -> Unit

@Composable
fun MeciList(meciList: List<Meci>, onMeciClick: OnMeciFn, modifier: Modifier) {
    Log.d("MeciList", "recompose")
    LazyColumn (
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(meciList) { meci ->
            MeciDetail(meci, onMeciClick)
        }
    }
}

@Composable
fun MeciDetail(meci: Meci, onMeciClick: OnMeciFn) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Display Name
        Row {
            ClickableText(
                text = AnnotatedString(meci.name),
                style = TextStyle(
                    fontSize = 24.sp,
                ),
                onClick = { onMeciClick(meci._id) }
            )
        }

        // Display PretBilet
        Row(modifier = Modifier.padding(top = 8.dp)) {
            Text(
                text = "Price: ${meci.pretBilet} RON",
                style = TextStyle(
                    fontSize = 18.sp,
                )
            )
        }

        // Display Has Started
        Row(modifier = Modifier.padding(top = 8.dp)) {
            Text(
                text = "Has Started: ${if (meci.hasStarted) "Yes" else "No"}",
                style = TextStyle(
                    fontSize = 18.sp,
                )
            )
        }

        // Display Start Date
        Row(modifier = Modifier.padding(top = 8.dp)) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val formattedDate = dateFormat.format(meci.startDate)
            Text(
                text = "Start Date: $formattedDate",
                style = TextStyle(
                    fontSize = 18.sp,
                )
            )
        }

        // Button or other interaction can go here
        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = { onMeciClick(meci._id) }
        ) {
            Text("View Details")
        }
    }
}