package edu.pcu.connect.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.pcu.connect.R
import edu.pcu.connect.model.User
import edu.pcu.connect.util.generateQrImageBitmap

@Composable
fun ProfileScreen(user: User, onSignOut: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("E-Profile & Digital ID") },
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DigitalIdCard(user)

            Spacer(20)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Profile details", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                    ProfileRow("Email", user.email)
                    ProfileRow("Program / Department", user.program)
                    ProfileRow("Role", user.role.label)
                    ProfileRow("Validity", user.validity)
                }
            }

            Spacer(20)

            OutlinedButton(
                onClick = onSignOut,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text("Sign out")
            }
        }
    }
}

@Composable
private fun DigitalIdCard(user: User) {
    val qr = remember(user.universityNumber) {
        generateQrImageBitmap("PCU-CONNECT|${user.universityNumber}|${user.role.label}")
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.pcu_logo),
                    contentDescription = "PCU seal",
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White, RoundedCornerShape(18.dp))
                        .padding(2.dp),
                )
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text("PCU-Connect Digital ID", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text(user.validity, color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodyMedium)
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(8.dp),
                ) {
                    Image(
                        bitmap = qr,
                        contentDescription = "Digital ID QR code",
                        modifier = Modifier.size(96.dp),
                    )
                }
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(user.fullName, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Text(user.universityNumber, color = Color.White.copy(alpha = 0.9f))
                    Text(user.program, color = Color.White.copy(alpha = 0.9f))
                    Text(user.role.label, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun Spacer(heightDp: Int) {
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(heightDp.dp))
}
