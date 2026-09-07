# Integrating the new Campus Feed UI

Three files, drop into your Compose source set (rename the package line at the
top of each to match your project, e.g. `com.pcu.connect.feed`):

- `CampusFeedModels.kt` — `CampusFeedPost` (Announcement / Poll), `PollOption`, `FeedComment`
- `CampusFeedSampleData.kt` — seed posts (weather/exam poll, student council poll,
  2 announcements) + pure functions `toggleLike`, `addComment`, `incrementShare`, `vote`
- `CampusFeedScreen.kt` — `CampusFeedScreen`, `AnnouncementCard`, `PollCard`,
  and `TeamsStyleNavRail`

## 1. AppViewModel

Replace whatever currently holds your announcement list with `CampusFeedPost`,
seeded from `sampleCampusFeedPosts()`. If you're on `StateFlow`:

```kotlin
private val _feedPosts = MutableStateFlow(sampleCampusFeedPosts())
val feedPosts: StateFlow<List<CampusFeedPost>> = _feedPosts.asStateFlow()

fun onLikePost(postId: String) = _feedPosts.update { it.toggleLike(postId) }
fun onSharePost(postId: String) = _feedPosts.update { it.incrementShare(postId) }
fun onVotePoll(postId: String, optionId: String) = _feedPosts.update { it.vote(postId, optionId) }
fun onAddComment(postId: String, text: String) = _feedPosts.update {
    it.addComment(postId, FeedComment(
        id = "c-${System.currentTimeMillis()}",
        authorName = currentUser.displayName, // swap in your session's name
        text = text,
        postedAt = "just now"
    ))
}
```

If your existing announcements have their own model/screen already, the easiest
path is a one-time mapper that turns your current announcement objects into
`CampusFeedPost.Announcement`, then adds the two `CampusFeedPost.Poll` sample
posts alongside them.

## 2. Screen wiring

```kotlin
val posts by viewModel.feedPosts.collectAsState()
CampusFeedScreen(
    posts = posts,
    onLike = viewModel::onLikePost,
    onShare = viewModel::onSharePost,
    onVote = viewModel::onVotePoll,
    onAddComment = viewModel::onAddComment
)
```

## 3. Teams-style nav

`TeamsStyleNavRail` is a `NavigationRail` (icon + label, left side, filled
with your `#27419A` primary) rather than the bottom bar Teams itself doesn't
use on mobile — Teams' actual mobile app keeps a bottom bar too, so if you
want the closest visual match to *desktop* Teams, use the rail on
tablet/landscape and keep your existing `NavigationBar` on phones:

```kotlin
if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
    // your existing bottom NavigationBar, same items/colors
} else {
    TeamsStyleNavRail(pcuNavDestinations, selectedIndex, onSelect)
}
```

Swap `pcuNavDestinations` for your real five tabs (Feed, Chat, Dashboard,
Services Hub, Admin Portal) if the icons differ.

## 4. Color scheme

Nothing new needed in `theme/Color.kt` — every composable above takes a
`primaryColor` parameter defaulting to `Color(0xFF27419A)`, your existing
seal blue. Pass `MaterialTheme.colorScheme.primary` explicitly if you'd
rather it always track the theme.

## Notes on the two example polls

- **Weather/exam poll** (`poll-weather-exam`): three options — move to
  Monday, keep as scheduled, move online — with a `closesLabel` so students
  see a deadline, matching how real PCU weather advisories read.
- **Student council poll** (`poll-student-council`): candidate options plus
  an explicit "Abstain," which is standard for campus election ballots and
  avoids implying abstaining isn't an option.

Both are seed data only — nothing here talks to a real backend, consistent
with the "self-contained prototype" framing in your README.
