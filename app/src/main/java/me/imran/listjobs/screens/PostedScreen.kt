package me.imran.listjobs.screens


import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.imran.listjobs.helper.Constants.JOBS_TABLE
import me.imran.listjobs.helper.Constants.TAG
import me.imran.listjobs.helper.getFromSharedPref
import me.imran.listjobs.models.DbJob
import me.imran.listjobs.models.Job

@Composable
fun PostedScreen(navController: NavHostController, sbClient: SupabaseClient, context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
         Button(onClick = {
                navController.navigate("jobPost")
            }) {
                Text("Add Job")
            }
        }

        val jobs  = remember { mutableStateOf<List<DbJob>>(listOf()) }

        fun loadJobs(){
            Log.d(TAG, "loadJobs: reloaded")
            CoroutineScope(Dispatchers.IO).launch {
                try {

            jobs.value = sbClient.postgrest[JOBS_TABLE].select(){
                filter {
                    eq("posted_by", getFromSharedPref("username",context).toString())
                }
            }.decodeList<DbJob>()


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
            Text(text = "Jobs posted by you", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                loadJobs()
            }) {
                Text("Refresh")
            }
            Spacer(modifier = Modifier.height(16.dp))


            LazyColumn {
                items(jobs.value.size) { jobIndex ->
                    val job = jobs.value[jobIndex]
                    PostedJobItem(
                        job = job, sbClient = sbClient,
                    ){
                        navController.navigate("jobPost/${job.id}")
                    }
                }
            }
        }    }
}


@Composable
fun PostedJobItem(job: DbJob, sbClient: SupabaseClient, onUpdate:()->Unit) {
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
                colors = ButtonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                modifier = Modifier.align(Alignment.End),
                onClick = {
                    onUpdate()
                }) {
                Text("Update")
            }

            Button(
                colors = ButtonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                modifier = Modifier.align(Alignment.End),
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch{
                        sbClient.postgrest[JOBS_TABLE].delete {
                            filter {
                                and {
                                    eq("title", job.title)
                                    eq("posted_by", job.posted_by)
                                    eq("pay", job.pay)
                                }
                            }
                        }
                    }

                }) {
                Text("Delete")
            }
        }
    }
}