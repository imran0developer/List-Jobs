package me.imran.listjobs.screens;

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.imran.listjobs.helper.Constants.JOBS_TABLE
import me.imran.listjobs.helper.Constants.TAG
import me.imran.listjobs.helper.Constants.USERS_TABLE
import me.imran.listjobs.helper.getFromSharedPref
import me.imran.listjobs.models.DbJob
import me.imran.listjobs.models.Job

@Composable
fun JobPost(navController: NavHostController, sbClient: SupabaseClient, context: Context, jobId: String?=null) {
    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val isRemote = remember { mutableStateOf(false) }
    val pay = remember { mutableStateOf("") }
    val location = remember { mutableStateOf("") }

    val postedBy = getFromSharedPref("username", context)

//  for testing
//    val title = remember { mutableStateOf("Software Engineer") }
//    val description = remember { mutableStateOf("Develop and maintain software applications.") }
//    val isRemote = remember { mutableStateOf(true) }
//    val pay = remember { mutableStateOf("75000") }
//    val location = remember { mutableStateOf("New York, NY") }

        LaunchedEffect(jobId) {
            if (jobId != null) {
                val gotJob = sbClient.postgrest[JOBS_TABLE].select {
                    filter {
                        eq("id", Integer.parseInt(jobId))
                    }
                }.decodeSingle<DbJob>()

                title.value = gotJob.title
                description.value = gotJob.description
                isRemote.value = gotJob.is_remote
                pay.value = gotJob.pay.toString()
                location.value = gotJob.location
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Post a New Job", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Title Field
        TextField(
            value = title.value,
            onValueChange = { title.value = it },
            label = { Text("Job Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description Field
        TextField(
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text("Job Description") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Remote Checkbox
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isRemote.value,
                onCheckedChange = { isRemote.value = it }
            )
            Text("Is this job remote?")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Pay Field
        TextField(
            value = pay.value,
            onValueChange = { pay.value = it },
            label = { Text("Pay (numeric)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Location Field
        TextField(
            value = location.value,
            onValueChange = { location.value = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Post Job Button
        Button(onClick = {
            if (jobId == null){

            val newJob = Job(
                title = title.value,
                description = description.value,
                is_remote = isRemote.value,
                pay = pay.value.toDoubleOrNull() ?: 0.0,
                location = location.value,
                posted_by = postedBy.toString()
            )
            CoroutineScope(Dispatchers.IO).launch{
                sbClient.postgrest[JOBS_TABLE].insert(
                    newJob
                )
            }
            navController.navigate("home")

            }else{
                Log.d(TAG, "JobPost: ID: $jobId")
                val updatedJob = DbJob(
                    id = jobId?.toInt()!!, // Include ID for update
                    title = title.value,
                    description = description.value,
                    is_remote = isRemote.value,
                    pay = pay.value.toDoubleOrNull() ?: 0.0,
                    location = location.value,
                    posted_by = postedBy.toString()
                )

                Log.d(TAG, "JobPost: $updatedJob")

                CoroutineScope(Dispatchers.IO).launch {
                    // Update existing job
                    sbClient.postgrest[JOBS_TABLE].update(updatedJob) {
                        filter { eq("id", jobId!!) }
                    }
                }

                navController.navigate("home")

            }
        }) {
            Text(if (jobId!=null) "Update Job" else "Post Job")
        }
        }
    }