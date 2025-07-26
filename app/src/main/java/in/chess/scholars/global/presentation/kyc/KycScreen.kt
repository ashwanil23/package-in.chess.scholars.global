package `in`.chess.scholars.global.presentation.kyc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import `in`.chess.scholars.global.domain.model.KycStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Data class to hold the status information, fixing the destructuring error.
private data class StatusInfo(val icon: ImageVector, val title: String, val message: String, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycScreen(
    navController: NavController,
    viewModel: KycViewModel // Assuming this ViewModel is provided
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KYC Verification", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1a1a2e),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF16213e)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF16213e),
                            Color(0xFF0f3460)
                        )
                    )
                )
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.error != null -> Text(uiState.error!!, modifier = Modifier.align(Alignment.Center))
                else -> KycContent(uiState, viewModel, navController)
            }
        }
    }
}

@Composable
private fun KycContent(
    uiState: KycUiState,
    viewModel: KycViewModel,
    navController: NavController
) {
    var panNumber by remember { mutableStateOf(uiState.userData?.panNumber ?: "") }
    var aadharNumber by remember { mutableStateOf(uiState.userData?.aadharNumber ?: "") }

    if (uiState.submissionSuccess) {
        SubmissionSuccessDialog {
            navController.popBackStack()
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            StatusCard(uiState.userData?.kycStatus ?: KycStatus.NOT_STARTED)
        }

        if (uiState.userData?.kycStatus == KycStatus.NOT_STARTED || uiState.userData?.kycStatus == KycStatus.REJECTED) {
            item {
                KycForm(
                    panNumber = panNumber,
                    onPanChange = { panNumber = it },
                    aadharNumber = aadharNumber,
                    onAadharChange = { aadharNumber = it },
                    isLoading = uiState.isLoading,
                    onSubmit = { viewModel.submitKyc(panNumber, aadharNumber) }
                )
            }
        }

        item {
            WhyKycCard()
        }
    }
}

@Composable
private fun StatusCard(status: KycStatus) {
    val (icon, title, message, color) = when (status) {
        KycStatus.NOT_STARTED -> StatusInfo(Icons.Default.Info, "Complete Your KYC", "Verify your identity to enable withdrawals.", Color(0xFFFF9800))
        KycStatus.IN_PROGRESS -> StatusInfo(Icons.Default.HourglassEmpty, "Verification In Progress", "Your documents are being reviewed (24-48 hours).", Color(0xFF2196F3))
        KycStatus.VERIFIED -> StatusInfo(Icons.Default.CheckCircle, "KYC Verified", "Your account is fully verified.", Color(0xFF4CAF50))
        KycStatus.REJECTED -> StatusInfo(Icons.Default.Error, "Verification Failed", "Please check your details and resubmit.", Color(0xFFFF5252))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(message, color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun KycForm(
    panNumber: String,
    onPanChange: (String) -> Unit,
    aadharNumber: String,
    onAadharChange: (String) -> Unit,
    isLoading: Boolean,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2a2a4e))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Identity Verification", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = panNumber,
                onValueChange = { if (it.length <= 10) onPanChange(it.uppercase()) },
                label = { Text("PAN Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = aadharNumber,
                onValueChange = { if (it.length <= 12 && it.all(Char::isDigit)) onAadharChange(it) },
                label = { Text("Aadhar Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = AadharVisualTransformation()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading && panNumber.length == 10 && aadharNumber.length == 12
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                else Text("Submit for Verification")
            }
        }
    }
}

@Composable
private fun WhyKycCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2a2a4e))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Why is KYC Required?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            Text("• To comply with government regulations.", color = Color.Gray, fontSize = 14.sp)
            Text("• To ensure a secure platform for all users.", color = Color.Gray, fontSize = 14.sp)
            Text("• To enable secure withdrawals of your winnings.", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
private fun SubmissionSuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Submission Successful") },
        text = { Text("Your KYC documents have been submitted for review. You will be notified once the process is complete.") },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("OK") }
        }
    )
}

private class AadharVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 12) text.text.substring(0..11) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 4 == 3 && i < 11) out += " "
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset + 1
                if (offset <= 11) return offset + 2
                return 14
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 4) return offset
                if (offset <= 9) return offset - 1
                if (offset <= 14) return offset - 2
                return 12
            }
        }
        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}
