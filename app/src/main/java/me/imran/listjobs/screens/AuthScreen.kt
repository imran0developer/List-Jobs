package me.imran.listjobs.screens;

import android.content.Context
import androidx.compose.ui.graphics.Color
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.imran.listjobs.helper.Constants.TAG
import me.imran.listjobs.helper.Constants.USERS_TABLE
import me.imran.listjobs.helper.saveInSharedPref
import me.imran.listjobs.models.User

@Composable
fun AuthScreen(sbClient: SupabaseClient,context: Context,onNavigate: () -> Unit) {
    var username = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var name = remember { mutableStateOf("") } // For sign-up
    var isRecruiter = remember { mutableStateOf(false) } // For recruiter
    var isSignUp = remember { mutableStateOf(true) } // Toggle between login and signup
    var invalid = remember { mutableStateOf(false) } // for invalid input

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = if (isSignUp.value) "Sign Up" else "Login", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))
        if (invalid.value){
            Text(text="Invalid username or password", color = Color.Red)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isSignUp.value) {
            TextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text(text="Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isRecruiter.value,
                    onCheckedChange = { isRecruiter.value = it }
                )
                Text("Are you a recruiter?")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        TextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))


        Button(onClick = {
            if (isSignUp.value) {
                // Handle sign-up
                val newUser = User(
                    name = name.value,
                    username = username.value,
                    password = password.value,
                    recruiter = isRecruiter.value,
                )

                // Supabase client to create a new user
                CoroutineScope(Dispatchers.IO).launch{
                    sbClient.postgrest[USERS_TABLE].insert(
                        newUser
                    )
                }
                saveInSharedPref("isLogin", true, context)
                saveInSharedPref("name", newUser.name, context)
                saveInSharedPref("username", newUser.username, context)
                saveInSharedPref("password", newUser.password, context)
                saveInSharedPref("recruiter", newUser.recruiter, context)

                onNavigate()

            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = sbClient.postgrest.from(USERS_TABLE).select(){
                            filter {
                                and {
                                    (User::username eq username.value)
                                    (User::password eq password.value)
                                }
                            }
                        }.decodeList<User>()

                        // Check if any users were found
                        if (response.isEmpty()) {
                            // No user found with the specified credentials
                            Log.d("UserCheck", "Invalid username or password.")
                            invalid.value = true
                        } else {
                            // User found
                            val user = response.first()
                            Log.d("UserCheck", "User found: ${user.username}")

                            saveInSharedPref("isLogin", true, context)
                            saveInSharedPref("name", user.name, context)
                            saveInSharedPref("username", user.username, context)
                            saveInSharedPref("password", user.password, context)
                            saveInSharedPref("recruiter", user.recruiter, context)

                        }

                    } catch (e: Exception) {
                        // Handle any other exceptions
                        Log.e("QueryError", e.message ?: "Unknown error")
                    }
                }
                if (!invalid.value){

                    onNavigate()
                }
            }
        }) {
            Text(if (isSignUp.value) "Sign Up" else "Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { isSignUp.value = !isSignUp.value }) {
            Text(if (isSignUp.value) "Already have an account? Login" else "Don't have an account? Sign Up")
        }

    }
}
