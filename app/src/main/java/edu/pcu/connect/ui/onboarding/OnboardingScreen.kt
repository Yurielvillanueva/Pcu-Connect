package edu.pcu.connect.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.pcu.connect.model.Role
import edu.pcu.connect.model.User

@Composable
fun OnboardingScreen(
    user: User,
    onComplete: () -> Unit,
) {
    var program by remember { mutableStateOf(user.program) }
    var emergencyContact by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }
    var acceptedConduct by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set up your profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text(
                "Welcome, ${user.fullName}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                "Signed in as ${user.role.label} - confirm the details below before we take you in.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
            )

            Text("Program / Department", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = program,
                onValueChange = { program = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 16.dp),
            )

            Text("Emergency contact", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = emergencyContact,
                onValueChange = { emergencyContact = it },
                placeholder = { Text("Name and phone number") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 20.dp),
            )

            AgreementRow(
                checked = acceptedTerms,
                onCheckedChange = { acceptedTerms = it },
                text = "I accept the Terms and Conditions and Privacy Policy.",
            )
            AgreementRow(
                checked = acceptedConduct,
                onCheckedChange = { acceptedConduct = it },
                text = "I accept the Campus Digital Code of Conduct.",
            )

            if (user.role != Role.STUDENT) {
                Text(
                    "Because you're signed in as ${user.role.label}, you'll also get access to the " +
                        "Admin Portal for publishing announcements, moderating chats, and reviewing " +
                        "Student Services requests.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                )
            }

            Button(
                onClick = onComplete,
                enabled = acceptedTerms && acceptedConduct,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 16.dp),
            ) {
                Text("Continue to PCU-Connect")
            }
        }
    }
}

@Composable
private fun AgreementRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}
