package edu.pcu.connect.model

/**
 * Roles mirror the brief: "the system confirms their active status, role
 * (Student, Faculty, or Admin)". MODERATOR is added as a lighter-weight
 * variant of ADMIN for org/department staff who only moderate, not publish.
 */
enum class Role(val label: String) {
    STUDENT("Student"),
    FACULTY("Faculty"),
    ADMIN("Admin"),
    MODERATOR("Moderator"),
}

fun Role.canManageFeed() = this == Role.ADMIN || this == Role.FACULTY
fun Role.canModerateChats() = this == Role.ADMIN || this == Role.FACULTY || this == Role.MODERATOR
fun Role.canReviewRequests() = this == Role.ADMIN || this == Role.MODERATOR
fun Role.isStaff() = this != Role.STUDENT

data class User(
    val fullName: String,
    val email: String,
    val universityNumber: String,
    val role: Role,
    val program: String,
    val yearOrPosition: String,
    val validity: String,
    val avatarInitial: String = fullName.firstOrNull()?.uppercaseChar()?.toString() ?: "P",
)

enum class AnnouncementCategory(val label: String) {
    EMERGENCY("Emergency"),
    ACADEMIC("Academic"),
    EVENT("Event"),
    STUDENT_SERVICE("Student Service"),
}

data class AnnouncementComment(
    val id: String,
    val author: String,
    val body: String,
    val postedAt: String,
)

data class PollOption(
    val id: String,
    val label: String,
    val voteCount: Int,
)

data class AnnouncementPoll(
    val question: String,
    val options: List<PollOption>,
    val selectedOptionId: String? = null,
)

data class Announcement(
    val id: String,
    val office: String,
    val title: String,
    val body: String,
    val category: AnnouncementCategory,
    val postedAt: String,
    val audience: String,
    val pinned: Boolean = false,
    val updated: Boolean = false,
    val reactionCount: Int = 0,
    val reactedByMe: Boolean = false,
    val shareCount: Int = 0,
    val sharedByMe: Boolean = false,
    val comments: List<AnnouncementComment> = emptyList(),
    val poll: AnnouncementPoll? = null,
)

data class CourseTeam(
    val id: String,
    val code: String,        // e.g. "BSIT 2A - IT 203"
    val title: String,       // e.g. "Database Systems"
    val instructor: String,
    val unreadCount: Int = 0,
)

data class ChatMessage(
    val id: String,
    val teamId: String,
    val sender: String,
    val senderIsStaff: Boolean,
    val body: String,
    val time: String,
    val pinned: Boolean = false,
)

data class ScheduleItem(
    val subjectCode: String,
    val subjectTitle: String,
    val time: String,
    val room: String,
    val professor: String,
    val day: String,
)

data class DeadlineItem(
    val title: String,
    val subjectCode: String,
    val dueDate: String,
)

data class ServiceOffice(
    val id: String,
    val name: String,
    val description: String,
)

enum class RequestStatus(val label: String) {
    RECEIVED("Request received"),
    UNDER_REVIEW("Under review"),
    NEEDS_INFO("Additional information required"),
    APPROVED("Approved"),
    COMPLETED("Completed"),
}

data class ServiceRequest(
    val trackingNumber: String,
    val office: String,
    val subject: String,
    val details: String,
    val requestedBy: String,
    val status: RequestStatus,
    val submittedAt: String,
)

enum class NotificationLevel { EMERGENCY, IMPORTANT, GENERAL }

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val level: NotificationLevel,
    val time: String,
    val read: Boolean = false,
)
