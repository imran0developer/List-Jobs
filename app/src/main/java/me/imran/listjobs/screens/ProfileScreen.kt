package me.imran.listjobs.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import me.imran.listjobs.helper.clearFromSharedPref
import me.imran.listjobs.helper.clearSharedPref


@Composable
fun ProfileScreen(navController: NavHostController, context: Context ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            clearSharedPref(context)
            navController.navigate("auth")
        }) {
            Text("Log out")
        }
    }
}