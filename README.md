# Life Organizer

Money, reading, courses, prayers, Quran, habits and tasks in one offline Android app.

**v2:** Calendar & history of everything you log (heat map, day details, mood + notes), a Money section that works like a financial advisor (health score, insights, 50/30/20, budgets, goals + emergency fund, fixed costs, debts, CSV export), weekly one focus, focus timer and brain dump.
All data stays on the phone (Settings ⚙️ → Export backup to save a copy).

## Get the APK (no Android Studio needed)
1. Create a new **private** repo on GitHub.
2. Push this folder:
   git init && git add . && git commit -m "Life Organizer"
   git branch -M main
   git remote add origin https://github.com/<you>/life-organizer.git
   git push -u origin main
3. Open the repo → **Actions** tab → "Build APK" (takes ~3 min).
4. Open the finished run → **Artifacts** → download `LifeOrganizer-apk` → unzip → `app-debug.apk`.

## Or with Android Studio
Open this folder → Build → Build APK(s) → app/build/outputs/apk/debug/app-debug.apk

## Change the app
Everything is in `app/src/main/assets/index.html`. Edit it, push, and Actions builds a new APK.
Install the new APK over the old one: data is kept as long as the package name stays the same.
