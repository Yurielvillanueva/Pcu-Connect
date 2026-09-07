# PCU-Connect Android prototype

PCU-Connect is a self-contained Jetpack Compose prototype for a closed
university network, built from the PCU-Connect concept brief. This build adds
role-based accounts (Student, Faculty, Admin, Moderator), an Admin Portal,
and PCU branding on top of the original student-only demo.

## Open and run

1. Open this folder in the current stable version of Android Studio.
2. Let Gradle Sync finish. The project targets AGP 8.13.2 / Kotlin 2.3.20,
   matching your existing setup. If Android Studio prompts to create a
   Gradle wrapper (no `gradlew` is included in this delivery), accept it -
   or open with Android Studio's bundled Gradle.
3. Select an Android 8.0+ (API 26+) emulator or device, then press **Run**.
4. On the sign-in screen, enter any `@pcu.edu` email and a password with at
   least six characters - this still simulates SSO. **Use the role chips
   under the password field to sign in as Student, Faculty, Admin, or
   Moderator** - that's the fix for the "always lands on Student" issue.

## What's in this build

- **Role-based access.** Faculty/Admin/Moderator accounts get a fifth
  bottom-nav tab, **Admin Portal**, plus in-context controls elsewhere:
  a compose button on the Campus Feed, a request-review queue in the
  Student Services Hub, and pin/delete controls in PCU Chat & Teams.
  Since this prototype simulates SSO instead of hitting a real
  Registrar/HR system, the role is chosen at sign-in rather than looked
  up - call this out if you present it, since a production build would
  assign it automatically.
- **Branding.** The uploaded seal is used as the app icon, the sign-in
  mark, and on the Digital ID card. The campus photo (darkened for
  legibility) is the sign-in screen background.
- **Color scheme.** The Material3 theme is built on the blue actually
  extracted from the seal artwork (`#27419A` primary), not a stock
  Material blue - see `theme/Color.kt`.
- **All eight brief modules**: Campus Feed, PCU Chat & Teams, Academic
  Dashboard, Student Services Hub (with request tracking numbers and
  status updates), E-Profile & Digital ID (with a real scannable QR
  code via ZXing), and a Notifications Center.

## Prototype boundaries

All accounts, announcements, chats, schedules, requests, and ID data are
local sample data held in memory (`AppViewModel`) - nothing persists
between app restarts, and there is no network access. A production
deployment still needs a secured backend, institutional SSO, Registrar/HR
integration, server-generated rotating QR tokens (this build's QR encodes
a static string, not a rotating token), audit logging, encrypted storage,
real role-based authorization, and a campus-approved privacy/security
review. The application does not claim to verify identity or grant
physical access.
