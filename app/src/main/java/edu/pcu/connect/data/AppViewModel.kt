package edu.pcu.connect.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import edu.pcu.connect.model.AnnouncementCategory
import edu.pcu.connect.model.AnnouncementComment
import edu.pcu.connect.model.AnnouncementPoll
import edu.pcu.connect.model.AppNotification
import edu.pcu.connect.model.ChatMessage
import edu.pcu.connect.model.CourseTeam
import edu.pcu.connect.model.DeadlineItem
import edu.pcu.connect.model.NotificationLevel
import edu.pcu.connect.model.PollOption
import edu.pcu.connect.model.RequestStatus
import edu.pcu.connect.model.Role
import edu.pcu.connect.model.ScheduleItem
import edu.pcu.connect.model.ServiceOffice
import edu.pcu.connect.model.ServiceRequest
import edu.pcu.connect.model.User
import edu.pcu.connect.ui.feed.CampusFeedPost
import edu.pcu.connect.ui.feed.FeedComment
import edu.pcu.connect.ui.feed.addComment
import edu.pcu.connect.ui.feed.incrementShare
import edu.pcu.connect.ui.feed.sampleCampusFeedPosts
import edu.pcu.connect.ui.feed.toggleLike
import edu.pcu.connect.ui.feed.vote
import kotlin.random.Random

/** Result of a sign-in attempt, mirroring the brief's verification step. */
sealed class SignInResult {
    data object NoMatch : SignInResult()
    data class Verified(val user: User) : SignInResult()
}

class AppViewModel : ViewModel() {

    // ----- Session -----
    var currentUser by mutableStateOf<User?>(null)
        private set

    var onboardingComplete by mutableStateOf(false)
        private set

    fun signIn(email: String, password: String, chosenRole: Role): SignInResult {
        val validDomain = email.trim().endsWith("@pcu.edu", ignoreCase = true)
        val validPassword = password.length >= 6
        if (!validDomain || !validPassword) return SignInResult.NoMatch

        val handle = email.substringBefore("@").replace(".", " ").trim()
        val displayName = if (handle.isNotBlank()) {
            handle.split(" ").joinToString(" ") { part ->
                part.replaceFirstChar { it.uppercaseChar() }
            }
        } else "PCU User"

        val user = User(
            fullName = displayName,
            email = email.trim(),
            universityNumber = "PCU-" + (100000 + Random.nextInt(899999)),
            role = chosenRole,
            program = if (chosenRole == Role.STUDENT) "BSIT - 3rd Year" else "Office of the Registrar",
            yearOrPosition = chosenRole.label,
            validity = "S.Y. 2026-2027",
        )
        currentUser = user
        return SignInResult.Verified(user)
    }

    fun completeOnboarding() {
        onboardingComplete = true
    }

    fun signOut() {
        currentUser = null
        onboardingComplete = false
    }

    // ----- Campus Feed -----
    val feedPosts = mutableStateListOf<CampusFeedPost>()

    init {
        feedPosts.addAll(sampleCampusFeedPosts())
    }

    fun onLikePost(postId: String) {
        val index = feedPosts.indexOfFirst { it.id == postId }
        if (index >= 0) {
            feedPosts[index] = listOf(feedPosts[index]).toggleLike(postId).first()
        }
    }

    fun onSharePost(postId: String) {
        val index = feedPosts.indexOfFirst { it.id == postId }
        if (index >= 0) {
            feedPosts[index] = listOf(feedPosts[index]).incrementShare(postId).first()
        }
    }

    fun onVotePoll(postId: String, optionId: String) {
        val index = feedPosts.indexOfFirst { it.id == postId }
        if (index >= 0) {
            feedPosts[index] = listOf(feedPosts[index]).vote(postId, optionId).first()
        }
    }

    fun onAddComment(postId: String, text: String) {
        val index = feedPosts.indexOfFirst { it.id == postId }
        if (index >= 0) {
            val comment = FeedComment(
                id = "c-${System.currentTimeMillis()}",
                authorName = currentUser?.fullName ?: "PCU User",
                text = text,
                postedAt = "just now"
            )
            feedPosts[index] = listOf(feedPosts[index]).addComment(postId, comment).first()
        }
    }

    fun postAnnouncement(
        office: String,
        title: String,
        body: String,
        category: AnnouncementCategory,
        audience: String,
    ) {
        val newPost = CampusFeedPost.Announcement(
            id = "ann-${System.currentTimeMillis()}",
            authorName = office,
            authorRole = "Admin",
            postedAt = "Just now",
            title = title,
            body = body
        )
        feedPosts.add(0, newPost)

        pushNotification(
            title = if (category == AnnouncementCategory.EMERGENCY) "Emergency: $title" else title,
            body = "Posted by $office",
            level = when (category) {
                AnnouncementCategory.EMERGENCY -> NotificationLevel.EMERGENCY
                else -> NotificationLevel.IMPORTANT
            },
        )
    }

    // ----- Chat & Teams -----
    val teams = mutableStateListOf(
        CourseTeam("t1", "BSIT 2A - IT 203", "Database Systems", "Prof. R. Santos", unreadCount = 3),
        CourseTeam("t2", "BSIT 2A - IT 210", "Systems Integration", "Prof. L. Cruz"),
        CourseTeam("t3", "BSIT 2A - GE 5", "Life and Works of Rizal", "Prof. M. Reyes", unreadCount = 1),
    )

    val messagesByTeam: Map<String, androidx.compose.runtime.snapshots.SnapshotStateList<ChatMessage>> = mapOf(
        "t1" to mutableStateListOf(
            ChatMessage("m1", "t1", "Prof. R. Santos", true, "Reminder: normalization seatwork is due Friday.", "9:02 AM", pinned = true),
            ChatMessage("m2", "t1", "Angela T.", false, "Is the seatwork individual or by group?", "9:05 AM"),
            ChatMessage("m3", "t1", "Prof. R. Santos", true, "Individual, please.", "9:06 AM"),
        ),
        "t2" to mutableStateListOf(
            ChatMessage("m4", "t2", "Prof. L. Cruz", true, "Moved Thursday's session to the lab.", "8:00 AM"),
        ),
        "t3" to mutableStateListOf(
            ChatMessage("m5", "t3", "Prof. M. Reyes", true, "Reflection paper guide is in the shared drive.", "Yesterday"),
        ),
    )

    fun sendMessage(teamId: String, sender: String, isStaff: Boolean, body: String) {
        val list = messagesByTeam[teamId] ?: return
        list.add(
            ChatMessage(
                id = "m${System.currentTimeMillis()}", teamId = teamId,
                sender = sender, senderIsStaff = isStaff, body = body, time = "Just now",
            ),
        )
    }

    fun deleteMessage(teamId: String, messageId: String) {
        messagesByTeam[teamId]?.removeAll { it.id == messageId }
    }

    fun togglePin(teamId: String, messageId: String) {
        val list = messagesByTeam[teamId] ?: return
        val idx = list.indexOfFirst { it.id == messageId }
        if (idx >= 0) list[idx] = list[idx].copy(pinned = !list[idx].pinned)
    }

    // ----- Academic Dashboard -----
    val schedule = listOf(
        ScheduleItem("IT 203", "Database Systems", "8:00 - 9:30 AM", "Rm 301", "Prof. R. Santos", "Today"),
        ScheduleItem("IT 210", "Systems Integration", "10:00 - 11:30 AM", "Lab 2", "Prof. L. Cruz", "Today"),
        ScheduleItem("GE 5", "Life and Works of Rizal", "1:00 - 2:30 PM", "Rm 214", "Prof. M. Reyes", "Today"),
    )

    val deadlines = listOf(
        DeadlineItem("Normalization seatwork", "IT 203", "Fri, Sept 11"),
        DeadlineItem("Systems proposal draft", "IT 210", "Mon, Sept 14"),
        DeadlineItem("Reflection paper", "GE 5", "Wed, Sept 16"),
    )

    // ----- Student Services Hub -----
    val offices = listOf(
        ServiceOffice("registrar", "Registrar", "Enrollment concerns, document requests, correction of records"),
        ServiceOffice("library", "Library", "Borrowing status, research assistance, book requests"),
        ServiceOffice("discipline", "Discipline Office", "Incident reports, appointment requests"),
        ServiceOffice("guidance", "Guidance and Counseling", "Consultations and referrals"),
        ServiceOffice("it", "IT Support", "Password resets, account concerns, technical issues"),
        ServiceOffice("finance", "Finance Office", "Billing, payment plans, receipts"),
    )

    val serviceRequests = mutableStateListOf(
        ServiceRequest(
            trackingNumber = "REQ-10021", office = "Registrar", subject = "Correction of records",
            details = "My middle name is misspelled on my COR.", requestedBy = "Student",
            status = RequestStatus.UNDER_REVIEW, submittedAt = "Sept 3",
        ),
    )

    fun submitServiceRequest(office: String, subject: String, details: String, requestedBy: String): String {
        val tracking = "REQ-${10022 + serviceRequests.size}"
        serviceRequests.add(
            0,
            ServiceRequest(tracking, office, subject, details, requestedBy, RequestStatus.RECEIVED, "Just now"),
        )
        pushNotification(
            title = "Request $tracking received",
            body = "$office has received your request.",
            level = NotificationLevel.GENERAL,
        )
        return tracking
    }

    fun updateRequestStatus(trackingNumber: String, status: RequestStatus) {
        val idx = serviceRequests.indexOfFirst { it.trackingNumber == trackingNumber }
        if (idx < 0) return
        serviceRequests[idx] = serviceRequests[idx].copy(status = status)
        pushNotification(
            title = "Update on $trackingNumber",
            body = status.label,
            level = NotificationLevel.IMPORTANT,
        )
    }

    // ----- Notifications -----
    val notifications = mutableStateListOf(
        AppNotification("n1", "Welcome to PCU-Connect", "Your digital ID is now active.", NotificationLevel.GENERAL, "Yesterday", read = true),
    )

    private fun pushNotification(title: String, body: String, level: NotificationLevel) {
        notifications.add(0, AppNotification("n${notifications.size + 1}", title, body, level, "Just now"))
    }

    fun markNotificationRead(id: String) {
        val idx = notifications.indexOfFirst { it.id == id }
        if (idx >= 0) notifications[idx] = notifications[idx].copy(read = true)
    }
}
