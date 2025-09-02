# .github/ISSUE_TEMPLATE/bug_report.md
---
name: Bug Report
about: Create a report to help us improve RunIQ
title: '[BUG] '
labels: ['bug', 'triage']
assignees: ''
---

## Bug Description
A clear and concise description of what the bug is.

## Steps to Reproduce
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

## Expected Behavior
A clear and concise description of what you expected to happen.

## Actual Behavior
A clear and concise description of what actually happened.

## Screenshots
If applicable, add screenshots to help explain your problem.

## Environment
- Device: [e.g. Pixel 7]
- Android Version: [e.g. Android 14]
- App Version: [e.g. 1.0.0]
- Health Connect Version: [if applicable]

## Related Linear Issue
- Linear Issue: [RUN-XXX](link to Linear issue)

## Additional Context
Add any other context about the problem here.

---

# .github/ISSUE_TEMPLATE/feature_request.md
---
name: Feature Request
about: Suggest an idea for RunIQ
title: '[FEATURE] '
labels: ['enhancement', 'triage']
assignees: ''
---

## Feature Description
A clear and concise description of what you want to happen.

## Problem Statement
A clear and concise description of what the problem is. Ex. I'm always frustrated when [...]

## Proposed Solution
A clear and concise description of what you want to happen.

## Alternative Solutions
A clear and concise description of any alternative solutions or features you've considered.

## User Stories
- As a [user type], I want [goal] so that [benefit]
- As a [user type], I want [goal] so that [benefit]

## Acceptance Criteria
- [ ] Criterion 1
- [ ] Criterion 2
- [ ] Criterion 3

## Technical Considerations
- Impact on Health Connect integration: 
- Impact on AI coaching system:
- Impact on Spotify integration:
- Database schema changes needed:

## Related Linear Issue
- Linear Issue: [RUN-XXX](link to Linear issue)

## Additional Context
Add any other context or screenshots about the feature request here.

---

# .github/pull_request_template.md
## Summary
Brief description of what this PR does.

## Related Issues
- Closes #[issue number]
- Linear Issue: [RUN-XXX](link to Linear issue)

## Type of Change
- [ ] 🐛 Bug fix (non-breaking change which fixes an issue)
- [ ] ✨ New feature (non-breaking change which adds functionality)
- [ ] 💥 Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] 📚 Documentation update
- [ ] 🔧 Refactoring (no functional changes, no api changes)
- [ ] 🎨 Style/UI changes
- [ ] 🧪 Tests (adding missing tests or correcting existing tests)

## Architecture Impact
- [ ] Health Connect integration changes
- [ ] Room database schema changes
- [ ] Firestore structure changes
- [ ] AI coaching system changes
- [ ] Spotify integration changes
- [ ] Background service changes
- [ ] No architecture impact

## Testing
- [ ] Unit tests pass locally
- [ ] Integration tests pass locally
- [ ] Manual testing completed
- [ ] Health Connect permissions tested
- [ ] GPS/location services tested
- [ ] Spotify integration tested (if applicable)
- [ ] AI coaching tested (if applicable)

## Screenshots/Videos
Include screenshots or videos demonstrating the changes (especially for UI changes).

## Database Changes
- [ ] No database changes
- [ ] Room migration required (version bump to X)
- [ ] Firestore schema changes documented
- [ ] Data backup strategy considered

## Performance Impact
- [ ] No performance impact
- [ ] Performance improved
- [ ] Performance impact analyzed and acceptable
- [ ] Memory usage analyzed
- [ ] Battery usage analyzed

## Security Considerations
- [ ] No security implications
- [ ] API keys/secrets properly handled
- [ ] Health data permissions reviewed
- [ ] Location permissions reviewed
- [ ] Network security considered

## Code Quality
- [ ] Code follows project coding standards
- [ ] Self-review completed
- [ ] Complex logic documented
- [ ] Error handling implemented
- [ ] Logging added where appropriate

## Deployment Notes
Any special deployment considerations or migration steps.

## Reviewer Notes
Any specific areas you'd like reviewers to focus on.
