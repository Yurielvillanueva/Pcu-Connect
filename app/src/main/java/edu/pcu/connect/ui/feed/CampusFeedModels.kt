package edu.pcu.connect.ui.feed

/**
 * A single feed post. Announcements are plain posts (Facebook-style like/comment/share).
 * Polls are the same, plus votable options (e.g. "move the exam?", "class rep election").
 */
sealed class CampusFeedPost {
    abstract val id: String
    abstract val authorName: String
    abstract val authorRole: String   // "Registrar", "Student Council", "Dean's Office", etc.
    abstract val postedAt: String     // pre-formatted display string, e.g. "2h ago"
    abstract val likeCount: Int
    abstract val likedByMe: Boolean
    abstract val comments: List<FeedComment>
    abstract val shareCount: Int
    abstract val pinned: Boolean

    data class Announcement(
        override val id: String,
        override val authorName: String,
        override val authorRole: String,
        override val postedAt: String,
        val title: String,
        val body: String,
        override val likeCount: Int = 0,
        override val likedByMe: Boolean = false,
        override val comments: List<FeedComment> = emptyList(),
        override val shareCount: Int = 0,
        override val pinned: Boolean = false
    ) : CampusFeedPost()

    data class Poll(
        override val id: String,
        override val authorName: String,
        override val authorRole: String,
        override val postedAt: String,
        val question: String,
        val context: String? = null,       // optional supporting text under the question
        val options: List<PollOption>,
        val votedOptionId: String? = null, // null = user hasn't voted yet
        val closesLabel: String? = null,   // e.g. "Voting closes Fri 5:00 PM"
        override val likeCount: Int = 0,
        override val likedByMe: Boolean = false,
        override val comments: List<FeedComment> = emptyList(),
        override val shareCount: Int = 0,
        override val pinned: Boolean = false
    ) : CampusFeedPost() {
        val totalVotes: Int get() = options.sumOf { it.voteCount }
    }
}

data class PollOption(
    val id: String,
    val text: String,
    val voteCount: Int = 0
)

data class FeedComment(
    val id: String,
    val authorName: String,
    val text: String,
    val postedAt: String
)
