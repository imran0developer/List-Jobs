package me.imran.listjobs.screens;

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.imran.listjobs.models.Job
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.imran.listjobs.helper.Constants.JOBS_TABLE
import kotlinx.coroutines.withContext
import me.imran.listjobs.helper.Constants.TAG

import kotlin.Unit;

@Composable
fun JobListingScreen(sbClient: SupabaseClient, title: String) {

    val jobs  = remember { mutableStateOf<List<Job>>(listOf()) }

    fun loadJobs(){
        Log.d(TAG, "loadJobs: reloaded")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                jobs.value = sbClient.postgrest[JOBS_TABLE].select().decodeList<Job>()
            } catch (e: Exception) {
                Log.e(TAG, "Error during operation: ${e.message}", e)
            }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO){
            loadJobs()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            loadJobs()
        }) {
            Text("Refresh List")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(jobs.value.size) { jobIndex ->
                val job = jobs.value[jobIndex]
                JobItem(job = job)
            }
        }
    }
}

@Composable
fun JobItem(job: Job) {
    val applied = remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = {

            })
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = job.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (job.is_remote) "Remote" else "On-site",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "$${job.pay}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                job.location,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Button(
                modifier = Modifier.align(Alignment.End),
                enabled = !applied.value,
                onClick = {
                    applied.value =  true
                }) {
                Text(if (applied.value) "Applied" else "Apply")
            }
        }
    }
}