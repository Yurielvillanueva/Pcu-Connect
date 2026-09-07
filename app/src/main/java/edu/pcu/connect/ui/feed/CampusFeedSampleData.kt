package edu.pcu.connect.ui.feed

/**
 * Seed data for the prototype. Drop the contents of sampleCampusFeedPosts() into
 * wherever AppViewModel currently seeds its announcement list.
 *
 * Includes the two examples you asked for:
 *  - a weather/exam-reschedule poll
 *  - a student council election poll
 * plus two plain announcements so the feed doesn't look poll-only.
 */
fun sampleCampusFeedPosts(): List<CampusFeedPost> = listOf(
    CampusFeedPost.Poll(
        id = "poll-weather-exam",
        authorName = "Office of the Registrar",
        authorRole = "Registrar",
        postedAt = "1h ago",
        question = "Move Thursday's Calculus II final due to the incoming storm?",
        context = "PAGASA has issued a signal for our area through Thursday afternoon. " +
            "Let us know your preference before the Dean's Office finalizes the schedule.",
        options = listOf(
            PollOption("opt-move-mon", "Yes, move it to Monday", voteCount = 214),
            PollOption("opt-keep", "No, keep the original schedule", voteCount = 58),
            PollOption("opt-online", "Move it online instead", voteCount = 76)
        ),
        closesLabel = "Voting closes Wed 6:00 PM",
        likeCount = 41,
        pinned = true
    ),
    CampusFeedPost.Poll(
        id = "poll-student-council",
        authorName = "PCU Student Council",
        authorRole = "Student Council",
        postedAt = "5h ago",
        question = "Vote for your 2026 Student Council President",
        context = "Voting is open to all currently enrolled students. One vote per account.",
        options = listOf(
            PollOption("cand-santos", "Maria Santos - BS Nursing", voteCount = 312),
            PollOption("cand-delacruz", "JR Dela Cruz - BS Computer Science", voteCount = 287),
            PollOption("cand-abstain", "Abstain", voteCount = 19)
        ),
        closesLabel = "Voting closes Fri 5:00 PM",
        likeCount = 88,
        comments = listOf(
            FeedComment("c1", "Angela R.", "Finally, election season!", "4h ago"),
            FeedComment("c2", "Marco T.", "Where's the platform video for JR?", "3h ago")
        )
    ),
    CampusFeedPost.Announcement(
        id = "ann-library-hours",
        authorName = "PCU Library",
        authorRole = "Library Services",
        postedAt = "1d ago",
        title = "Extended library hours during finals week",
        body = "The main library will be open until midnight from Dec 8-19. " +
            "Group study rooms can now be reserved through the Student Services Hub.",
        likeCount = 63,
        comments = listOf(
            FeedComment("c3", "Diego P.", "Are the 3rd floor rooms included?", "20h ago")
        )
    ),
    CampusFeedPost.Announcement(
        id = "ann-id-renewal",
        authorName = "Office of Student Affairs",
        authorRole = "Admin",
        postedAt = "2d ago",
        title = "Digital ID renewal now open",
        body = "Renew your E-Profile & Digital ID for the new semester through the app. " +
            "Bring a valid government ID to the OSA window if your photo needs updating.",
        likeCount = 27,
        shareCount = 5
    )
)

/**
 * Pure state-transform helpers, meant to be called from AppViewModel like:
 *   _feedPosts.update { posts -> posts.toggleLike("poll-weather-exam") }
 */
fun List<CampusFeedPost>.toggleLike(postId: String): List<CampusFeedPost> = map { post ->
    if (post.id != postId) return@map post
    when (post) {
        is CampusFeedPost.Announcement -> post.copy(
            likedByMe = !post.likedByMe,
            likeCount = post.likeCount + if (post.likedByMe) -1 else 1
        )
        is CampusFeedPost.Poll -> post.copy(
            likedByMe = !post.likedByMe,
            likeCount = post.likeCount + if (post.likedByMe) -1 else 1
        )
    }
}

fun List<CampusFeedPost>.addComment(postId: String, comment: FeedComment): List<CampusFeedPost> =
    map { post ->
        if (post.id != postId) return@map post
        when (post) {
            is CampusFeedPost.Announcement -> post.copy(comments = post.comments + comment)
            is CampusFeedPost.Poll -> post.copy(comments = post.comments + comment)
        }
    }

fun List<CampusFeedPost>.incrementShare(postId: String): List<CampusFeedPost> = map { post ->
    if (post.id != postId) return@map post
    when (post) {
        is CampusFeedPost.Announcement -> post.copy(shareCount = post.shareCount + 1)
        is CampusFeedPost.Poll -> post.copy(shareCount = post.shareCount + 1)
    }
}

/** Casts a vote; switching options moves the voter's tally instead of double-counting. */
fun List<CampusFeedPost>.vote(postId: String, optionId: String): List<CampusFeedPost> = map { post ->
    if (post.id != postId || post !is CampusFeedPost.Poll) return@map post
    val previous = post.votedOptionId
    if (previous == optionId) return@map post
    val updatedOptions = post.options.map { opt ->
        when (opt.id) {
            optionId -> opt.copy(voteCount = opt.voteCount + 1)
            previous -> opt.copy(voteCount = (opt.voteCount - 1).coerceAtLeast(0))
            else -> opt
        }
    }
    post.copy(options = updatedOptions, votedOptionId = optionId)
}
