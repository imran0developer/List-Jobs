package me.imran.listjobs.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.jan.supabase.SupabaseClient
import me.imran.listjobs.helper.getFromSharedPref

@Composable
fun HomeScreen(navController: NavHostController, sbClient: SupabaseClient, context: Context ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to List Jobs",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
            )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            navController.navigate("profile")
        }) {
            Text("Profile")
        }
        if (getFromSharedPref("recruiter", context)==true){
            Row {
                Button(onClick = {
                    navController.navigate("jobPost")
                }) {
                    Text("Add Job")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(onClick = { navController.navigate("posted") }) {
                    Text("View Posted Jobs")
                }
            }
        }

        JobListingScreen(sbClient = sbClient, title="Relevant Jobs")
    }
}

