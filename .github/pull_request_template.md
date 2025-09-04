## 📋 Description

<!-- Provide a brief description of the changes in this PR -->

## 🎯 Related Issue

<!-- Link to the Linear issue or GitHub issue -->
- Linear Issue: RUN-
- Fixes #(issue number)

## 🔄 Type of Change

<!-- Mark the relevant option with an "x" -->
- [ ] 🐛 Bug fix (non-breaking change which fixes an issue)
- [ ] ✨ New feature (non-breaking change which adds functionality)
- [ ] 💥 Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] 📝 Documentation update
- [ ] 🎨 Code style update (formatting, renaming)
- [ ] ♻️ Refactoring (no functional changes)
- [ ] ⚡ Performance improvement
- [ ] ✅ Test update
- [ ] 🔧 Configuration change
- [ ] 🔀 Other (please describe):

## 📸 Screenshots/Videos

<!-- If applicable, add screenshots or videos to help explain your changes -->

## ✅ Checklist

<!-- Mark completed items with an "x" -->
- [ ] My code follows the project's style guidelines
- [ ] I have performed a self-review of my code
- [ ] I have commented my code, particularly in hard-to-understand areas
- [ ] I have made corresponding changes to the documentation
- [ ] My changes generate no new warnings or errors
- [ ] I have added tests that prove my fix is effective or that my feature works
- [ ] New and existing unit tests pass locally
- [ ] Any dependent changes have been merged and published
- [ ] I have checked my code for potential security issues
- [ ] I have verified that no API keys or secrets are hardcoded

## 🧪 Testing

<!-- Describe the tests you ran to verify your changes -->
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Manual testing completed
- [ ] Tested on physical device
- [ ] Tested on emulator

### Test Configuration
- Device/Emulator:
- Android Version:
- Test scenarios covered:

## 📱 Device Compatibility

<!-- List the devices/emulators you tested on -->
- [ ] API 28 (Android 8.0)
- [ ] API 30 (Android 11)
- [ ] API 33 (Android 13)
- [ ] API 34 (Android 14)
- [ ] API 36 (Latest)

## 🚀 Deployment Notes

<!-- Any notes for deployment or things reviewers should be aware of -->

## 📚 Additional Context

<!-- Add any other context about the PR here -->

---

**Reviewer Guidelines:**
- Check for proper error handling with Result wrapper
- Verify no hardcoded API keys or secrets
- Ensure proper use of Coroutine dispatchers
- Confirm Clean Architecture principles are followed
- Validate that all DAOs operations are properly tested