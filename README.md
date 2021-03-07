# [WIP] Weekly Tracker
App where you can set weekly targets for work, learning etc. Helps you track your progress and get more organized.

## Features
- Add target for goals you want to keep track of.
  eg: 4 hours for studying every day.
- Targets can be in minutes or hours, daily or weekly.
- Target progress gets cleared at around midnight, daily for daily targets, and weekly on mondays for weekly targets (will add functionaly to customize which day of the week).
- Swipe targets left or right to start / pause sessions.
- General todo list page, to add unrelated tasks.
- Swipe tasks right to mark as done / not done.
- Swipe left to delete (with snackbar to undo delete).
- Basic sorting and searching in tasks list

## Built using
- Kotlin, Coroutines
- MVVM, LiveData
- Dagger Hilt
- Room
- WorkManager, AlarmManager
- Navigation Component

## Features to add before first release
- [x] List page showing all targets with progress
- [x] Target details & edit page
- [x] Tasks page
- [x] Notification when use completes a target
- [x] Live update of target progress
- [ ] Warning notifications to remind users not to miss targets
- [ ] Button to manually log time spent on a target
- [ ] Weekly performance reports

## Good to haves
- [ ] Separate Todo list for each target
- [ ] Limit maximum amount when adding new targets, based on sum of already added target amouns (theres only 24x7 hours a week)
- [ ] Settings page to customize notification timings, progress clear timings, add sleep & leisure hours
- [ ] Custom themes
- [ ] Basic sorting and searching in targets list
