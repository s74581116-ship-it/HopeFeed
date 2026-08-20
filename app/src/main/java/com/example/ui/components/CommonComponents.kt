package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DonationStatus
import com.example.data.model.DonorType
import com.example.data.model.StorageRequirement

@Composable
fun DonorTypeBadge(donorType: DonorType, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (donorType) {
        DonorType.RESTAURANT -> Color(0xFFDDE6D2) to Color(0xFF386A20)
        DonorType.BAKERY -> Color(0xFFEFE7D0) to Color(0xFF6E5316)
        DonorType.GROCERY -> Color(0xFFB8F397) to Color(0xFF042100)
        DonorType.CELEBRATION_EVENT -> Color(0xFFE9DDD0) to Color(0xFF6B3A1C)
        DonorType.CATERER -> Color(0xFFDFE9DC) to Color(0xFF2C5618)
        DonorType.COMMUNITY -> Color(0xFFE3EDE1) to Color(0xFF395C2E)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = donorType.iconLabel, fontSize = 12.sp)
        Text(
            text = donorType.displayName,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatusBadge(status: DonationStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor, icon) = when (status) {
        DonationStatus.AVAILABLE -> Triple(Color(0xFFB8F397), Color(0xFF042100), Icons.Default.CheckCircle)
        DonationStatus.CLAIMED -> Triple(Color(0xFFDDE6D2), Color(0xFF386A20), Icons.Default.Schedule)
        DonationStatus.IN_TRANSIT -> Triple(Color(0xFFD7E8CC), Color(0xFF265013), Icons.Default.DeliveryDining)
        DonationStatus.DELIVERED -> Triple(Color(0xFFCBE4B9), Color(0xFF1E430B), Icons.Default.DoneAll)
        DonationStatus.EXPIRED -> Triple(Color(0xFFEDE0D5), Color(0xFF7E3922), Icons.Default.Cancel)
    }

    Surface(
        modifier = modifier.clip(RoundedCornerShape(14.dp)),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = status.displayName,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = status.displayName,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StoragePill(storageRequirement: StorageRequirement, modifier: Modifier = Modifier) {
    val (icon, color) = when (storageRequirement) {
        StorageRequirement.HOT_HOLD -> Icons.Default.Whatshot to Color(0xFF9E4812)
        StorageRequirement.REFRIGERATED -> Icons.Default.AcUnit to Color(0xFF2D5E6B)
        StorageRequirement.FROZEN -> Icons.Default.SevereCold to Color(0xFF32547C)
        StorageRequirement.AMBIENT -> Icons.Default.Inventory2 to Color(0xFF386A20)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = storageRequirement.displayName,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = storageRequirement.displayName,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun MetricStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
