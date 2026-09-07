package edu.pcu.connect.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.pcu.connect.data.AppViewModel

@Composable
fun CampusFeedScreen(
    viewModel: AppViewModel,
    canCompose: Boolean,
    onCompose: () -> Unit,
    onOpenNotifications: () -> Unit
) {
    CampusFeedScreen(
        posts = viewModel.feedPosts,
        onLike = viewModel::onLikePost,
        onShare = viewModel::onSharePost,
        onVote = viewModel::onVotePoll,
        onAddComment = viewModel::onAddComment,
        modifier = Modifier.fillMaxSize()
    )
    
    // Note: The original MainScaffold.kt call also passed onCompose and onOpenNotifications, 
    // but the CampusFeedScreen implementation from root doesn't have a TopAppBar or FAB.
    // For now, I'll keep the signature expected by MainScaffold.kt but the UI might need 
    // further adjustment if those actions were supposed to be on this screen.
}

@Composable
fun CampusFeedScreen(
    posts: List<CampusFeedPost>,
    onLike: (String) -> Unit,
    onShare: (String) -> Unit,
    onVote: (postId: String, optionId: String) -> Unit,
    onAddComment: (postId: String, text: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sorted = remember(posts) { posts.sortedByDescending { it.pinned } }
    LazyColumn(
        modifier = modifier.fillMaxSize().background(Color(0xFFF3F4F8)),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(sorted, key = { it.id }) { post ->
            when (post) {
                is CampusFeedPost.Announcement -> AnnouncementCard(
                    post = post,
                    onLike = { onLike(post.id) },
                    onShare = { onShare(post.id) },
                    onAddComment = { text -> onAddComment(post.id, text) }
                )
                is CampusFeedPost.Poll -> PollCard(
                    post = post,
                    onLike = { onLike(post.id) },
                    onShare = { onShare(post.id) },
                    onVote = { optionId -> onVote(post.id, optionId) },
                    onAddComment = { text -> onAddComment(post.id, text) }
                )
            }
        }
    }
}

@Composable
private fun PostHeader(
    authorName: String,
    authorRole: String,
    postedAt: String,
    pinned: Boolean,
    primaryColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(primaryColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                authorName.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(authorName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                if (pinned) {
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        Icons.Filled.PushPin,
                        contentDescription = "Pinned",
                        tint = primaryColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Text(
                "$authorRole · $postedAt",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ReactionBar(
    likeCount: Int,
    likedByMe: Boolean,
    commentCount: Int,
    shareCount: Int,
    onLike: () -> Unit,
    onCommentClick: () -> Unit,
    onShare: () -> Unit,
    primaryColor: Color
) {
    Column {
        if (likeCount > 0 || commentCount > 0) {
            Row(
                Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    if (likeCount > 0) "👍 $likeCount" else "",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    if (commentCount > 0) "$commentCount comments" +
                        if (shareCount > 0) " · $shareCount shares" else "" else
                        if (shareCount > 0) "$shareCount shares" else "",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            HorizontalDivider(color = Color(0xFFE5E7EB))
        }
        Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
            ActionButton(
                icon = if (likedByMe) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                label = "Like",
                tint = if (likedByMe) primaryColor else Color.Gray,
                onClick = onLike,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                icon = Icons.Outlined.ChatBubbleOutline,
                label = "Comment",
                tint = Color.Gray,
                onClick = onCommentClick,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                icon = Icons.Outlined.Share,
                label = "Share",
                tint = Color.Gray,
                onClick = onShare,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, color = tint, fontSize = 13.sp)
    }
}

@Composable
private fun CommentSection(
    comments: List<FeedComment>,
    onAddComment: (String) -> Unit,
    primaryColor: Color
) {
    var expanded by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf("") }

    Column(Modifier.padding(top = 6.dp)) {
        if (comments.isNotEmpty()) {
            val toShow = if (expanded) comments else comments.takeLast(1)
            if (!expanded && comments.size > 1) {
                Text(
                    "View all ${comments.size} comments",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.clickable { expanded = true }.padding(vertical = 4.dp)
                )
            }
            toShow.forEach { comment ->
                Row(Modifier.padding(vertical = 3.dp)) {
                    Box(
                        Modifier.size(26.dp).clip(CircleShape).background(Color(0xFFD9DEEF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(comment.authorName.take(1), fontSize = 11.sp, color = primaryColor)
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(
                        Modifier
                            .background(Color(0xFFF0F1F5), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(comment.authorName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(comment.text, fontSize = 13.sp)
                    }
                }
            }
        }
        Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                placeholder = { Text("Write a comment...", fontSize = 13.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )
            IconButton(
                onClick = {
                    if (draft.isNotBlank()) {
                        onAddComment(draft.trim())
                        draft = ""
                    }
                }
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Send", tint = primaryColor)
            }
        }
    }
}

@Composable
fun AnnouncementCard(
    post: CampusFeedPost.Announcement,
    onLike: () -> Unit,
    onShare: () -> Unit,
    onAddComment: (String) -> Unit,
    primaryColor: Color = Color(0xFF27419A)
) {
    var showComments by remember { mutableStateOf(false) }
    ElevatedCard(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(14.dp)) {
            PostHeader(post.authorName, post.authorRole, post.postedAt, post.pinned, primaryColor)
            Spacer(Modifier.height(10.dp))
            Text(post.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(4.dp))
            Text(post.body, fontSize = 14.sp, color = Color(0xFF3A3A3A))
            ReactionBar(
                likeCount = post.likeCount,
                likedByMe = post.likedByMe,
                commentCount = post.comments.size,
                shareCount = post.shareCount,
                onLike = onLike,
                onCommentClick = { showComments = !showComments },
                onShare = onShare,
                primaryColor = primaryColor
            )
            if (showComments) {
                CommentSection(post.comments, onAddComment, primaryColor)
            }
        }
    }
}

@Composable
fun PollCard(
    post: CampusFeedPost.Poll,
    onLike: () -> Unit,
    onShare: () -> Unit,
    onVote: (optionId: String) -> Unit,
    onAddComment: (String) -> Unit,
    primaryColor: Color = Color(0xFF27419A)
) {
    var showComments by remember { mutableStateOf(false) }
    val hasVoted = post.votedOptionId != null

    ElevatedCard(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(14.dp)) {
            PostHeader(post.authorName, post.authorRole, post.postedAt, post.pinned, primaryColor)
            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Poll, contentDescription = null, tint = primaryColor, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("POLL", fontSize = 11.sp, color = primaryColor, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Text(post.question, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            post.context?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, fontSize = 13.sp, color = Color(0xFF3A3A3A))
            }

            Spacer(Modifier.height(10.dp))
            post.options.forEach { option ->
                PollOptionRow(
                    option = option,
                    totalVotes = post.totalVotes,
                    isSelected = option.id == post.votedOptionId,
                    hasVoted = hasVoted,
                    onClick = { onVote(option.id) },
                    primaryColor = primaryColor
                )
                Spacer(Modifier.height(6.dp))
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${post.totalVotes} votes", fontSize = 12.sp, color = Color.Gray)
                post.closesLabel?.let {
                    Text(it, fontSize = 12.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            ReactionBar(
                likeCount = post.likeCount,
                likedByMe = post.likedByMe,
                commentCount = post.comments.size,
                shareCount = post.shareCount,
                onLike = onLike,
                onCommentClick = { showComments = !showComments },
                onShare = onShare,
                primaryColor = primaryColor
            )
            if (showComments) {
                CommentSection(post.comments, onAddComment, primaryColor)
            }
        }
    }
}

@Composable
private fun PollOptionRow(
    option: PollOption,
    totalVotes: Int,
    isSelected: Boolean,
    hasVoted: Boolean,
    onClick: () -> Unit,
    primaryColor: Color
) {
    val percent = if (totalVotes == 0) 0f else option.voteCount.toFloat() / totalVotes.toFloat()

    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) primaryColor.copy(alpha = 0.08f) else Color(0xFFF5F6FA))
            .clickable(enabled = true, onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        if (hasVoted) {
            Box(
                Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(percent.coerceIn(0f, 1f))
                        .background(primaryColor.copy(alpha = 0.15f))
                )
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSelected) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = "Your vote",
                        tint = primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                }
                Text(option.text, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
            }
            if (hasVoted) {
                Text("${(percent * 100).toInt()}%", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
