package me.imran.listjobs

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.imran.listjobs.helper.Constants.SUPABASE_KEY
import me.imran.listjobs.helper.Constants.SUPABASE_URL
import me.imran.listjobs.helper.Constants.TAG
import me.imran.listjobs.models.Job
import me.imran.listjobs.models.Test
import me.imran.listjobs.ui.theme.ListJobsTheme


import androidx.compose.material3.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.jan.supabase.SupabaseClient
import me.imran.listjobs.helper.getFromSharedPref
import me.imran.listjobs.screens.AuthScreen
import me.imran.listjobs.screens.HomeScreen
import me.imran.listjobs.screens.JobListingScreen
import me.imran.listjobs.screens.JobPost
import me.imran.listjobs.screens.PostedScreen
import me.imran.listjobs.screens.ProfileScreen

fun supabaseInit(API_KEY: String, BASE_URL: String): SupabaseClient {
    val supabase = createSupabaseClient(
        supabaseUrl = BASE_URL,
        supabaseKey = API_KEY,
    ) {
        install(Postgrest)
    }
    return supabase
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListJobsTheme {
                val API_KEY = getString(R.string.API_KEY)
                val BASE_URL = getString(R.string.BASE_URL)

                Log.d(TAG, "API Key: ${getString(R.string.API_KEY)}")
                Log.d(TAG, "BASE URL: ${getString(R.string.BASE_URL)}")

                MainScreen(this@MainActivity, API_KEY, BASE_URL)

            }
        }
    }
}


@Composable
fun MainScreen(context: Context, API_KEY: String, BASE_URL: String) {
    val isLoggedIn = getFromSharedPref("isLogin", context) as? Boolean ?: false


    val supabaseClient = supabaseInit(API_KEY, BASE_URL)
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = if (isLoggedIn) "home" else "auth" ) {
        composable("home") { HomeScreen(navController=navController, sbClient = supabaseClient, context = context)}
        composable("profile") { ProfileScreen(navController=navController, context = context) }
        composable("auth") { AuthScreen(sbClient = supabaseClient, context = context, onNavigate = { navController.navigate("home") }) }
        composable("jobs") { JobListingScreen(sbClient = supabaseClient, title="Job Listings")}
        composable("jobPost") { JobPost(navController=navController, sbClient=supabaseClient, context = context) }

        composable("jobPost/{jobId}") { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId")
            Log.d(TAG, "MainScreen: $jobId")

            JobPost(navController, supabaseClient, context, jobId)
        }
        composable("posted") { PostedScreen(navController=navController, sbClient=supabaseClient, context = context) }
    }
}
