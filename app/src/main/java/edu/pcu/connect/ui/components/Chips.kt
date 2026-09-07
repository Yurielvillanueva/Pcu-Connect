package edu.pcu.connect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.pcu.connect.model.AnnouncementCategory
import edu.pcu.connect.model.RequestStatus
import edu.pcu.connect.theme.StatusAcademic
import edu.pcu.connect.theme.StatusEmergency
import edu.pcu.connect.theme.StatusEvent
import edu.pcu.connect.theme.StatusService

@Composable
fun CategoryChip(category: AnnouncementCategory, modifier: Modifier = Modifier) {
    val color = when (category) {
        AnnouncementCategory.EMERGENCY -> StatusEmergency
        AnnouncementCategory.ACADEMIC -> StatusAcademic
        AnnouncementCategory.EVENT -> StatusEvent
        AnnouncementCategory.STUDENT_SERVICE -> StatusService
    }
    TagPill(text = category.label.uppercase(), color = color, modifier = modifier)
}

@Composable
fun StatusPill(status: RequestStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        RequestStatus.RECEIVED -> StatusService
        RequestStatus.UNDER_REVIEW -> StatusAcademic
        RequestStatus.NEEDS_INFO -> StatusEmergency
        RequestStatus.APPROVED -> StatusEvent
        RequestStatus.COMPLETED -> MaterialTheme.colorScheme.primary
    }
    TagPill(text = status.label, color = color, modifier = modifier)
}

@Composable
private fun TagPill(text: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .background(color.copy(alpha = 0.12f), shape = RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}
