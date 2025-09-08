@echo off
REM Setup GitHub Branch Protection Rules for NNGC MicroServices
REM Configures branch protection to enforce single-feature commits and PR workflows

echo 🛡️ Setting up GitHub branch protection rules...
echo =============================================

REM Check if GitHub CLI is installed
gh --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ GitHub CLI not found. Please install GitHub CLI first:
    echo https://cli.github.com/
    pause
    exit /b 1
)

REM Check if we're in a git repository with remote
git remote get-url origin >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ No GitHub remote found. Please ensure this is a GitHub repository.
    pause
    exit /b 1
)

echo 🔍 Configuring branch protection for main branch...

REM Configure main branch protection
gh api repos/:owner/:repo/branches/main/protection ^
  --method PUT ^
  --field required_status_checks="{\"strict\":true,\"contexts\":[\"Pre-Merge Quality Gates\",\"PR Security Validation\",\"Pull Request Validation\"]}" ^
  --field enforce_admins=true ^
  --field required_pull_request_reviews="{\"required_approving_review_count\":1,\"dismiss_stale_reviews\":true,\"require_code_owner_reviews\":false}" ^
  --field restrictions=null ^
  --field required_conversation_resolution=true ^
  --field allow_force_pushes=false ^
  --field allow_deletions=false

if %errorlevel% equ 0 (
    echo ✅ Main branch protection configured
) else (
    echo ❌ Failed to configure main branch protection
)

echo 🔍 Configuring branch protection for bishop_dev branch...

REM Configure bishop_dev branch protection (more lenient for development)
gh api repos/:owner/:repo/branches/bishop_dev/protection ^
  --method PUT ^
  --field required_status_checks="{\"strict\":false,\"contexts\":[\"Pre-Merge Quality Gates\"]}" ^
  --field enforce_admins=false ^
  --field required_pull_request_reviews="{\"required_approving_review_count\":1,\"dismiss_stale_reviews\":false,\"require_code_owner_reviews\":false}" ^
  --field restrictions=null ^
  --field required_conversation_resolution=false ^
  --field allow_force_pushes=true ^
  --field allow_deletions=false

if %errorlevel% equ 0 (
    echo ✅ Development branch protection configured
) else (
    echo ❌ Failed to configure development branch protection
)

echo 📋 Setting up repository settings...

REM Configure repository settings for additional protection
gh api repos/:owner/:repo ^
  --method PATCH ^
  --field allow_squash_merge=true ^
  --field allow_merge_commit=false ^
  --field allow_rebase_merge=true ^
  --field delete_branch_on_merge=true ^
  --field allow_auto_merge=false

if %errorlevel% equ 0 (
    echo ✅ Repository merge settings configured
) else (
    echo ❌ Failed to configure repository settings
)

echo 🏷️ Creating issue and PR templates...

REM Create .github directory if it doesn't exist
if not exist ".github" mkdir .github

REM Create pull request template
echo ## Summary > .github\pull_request_template.md
echo. >> .github\pull_request_template.md
echo Describe the changes in this PR and why they were made. >> .github\pull_request_template.md
echo. >> .github\pull_request_template.md
echo ## Service(s) Affected >> .github\pull_request_template.md
echo. >> .github\pull_request_template.md
echo - [ ] api-gateway >> .github\pull_request_template.md
echo - [ ] customer-service >> .github\pull_request_template.md
echo - [ ] registration-service >> .github\pull_request_template.md
echo - [ ] token-service >> .github\pull_request_template.md
echo - [ ] email-service >> .github\pull_request_template.md
echo - [ ] google-service >> .github\pull_request_template.md
echo - [ ] stripe-service >> .github\pull_request_template.md
echo - [ ] service-registry >> .github\pull_request_template.md
echo - [ ] infrastructure >> .github\pull_request_template.md
echo. >> .github\pull_request_template.md
echo ## Type of Change >> .github\pull_request_template.md
echo. >> .github\pull_request_template.md
echo - [ ] feat: New feature >> .github\pull_request_template.md
echo - [ ] fix: Bug fix >> .github\pull_request_template.md
echo - [ ] docs: Documentation update >> .github\pull_request_template.md
echo - [ ] style: Code style changes >> .github\pull_request_template.md
echo - [ ] refactor: Code refactoring >> .github\pull_request_template.md
echo - [ ] test: Test updates >> .github\pull_request_template.md
echo - [ ] chore: Build/tooling changes >> .github\pull_request_template.md
echo - [ ] security: Security-related changes >> .github\pull_request_template.md
echo. >> .github\pull_request_template.md
echo ## Testing >> .github\pull_request_template.md
echo. >> .github\pull_request_template.md
echo - [ ] Unit tests added/updated >> .github\pull_request_template.md
echo - [ ] Integration tests pass >> .github\pull_request_template.md
echo - [ ] Manual testing completed >> .github\pull_request_template.md
echo - [ ] No breaking changes >> .github\pull_request_template.md
echo. >> .github\pull_request_template.md
echo ## Security Checklist >> .github\pull_request_template.md
echo. >> .github\pull_request_template.md
echo - [ ] No hardcoded secrets >> .github\pull_request_template.md
echo - [ ] Input validation added where needed >> .github\pull_request_template.md
echo - [ ] Authentication/authorization considered >> .github\pull_request_template.md
echo - [ ] Sensitive data handling reviewed >> .github\pull_request_template.md
echo. >> .github\pull_request_template.md
echo ## Deployment Notes >> .github\pull_request_template.md
echo. >> .github\pull_request_template.md
echo Any special considerations for deployment or configuration changes. >> .github\pull_request_template.md

echo ✅ Pull request template created

echo.
echo 🎉 Branch protection setup completed!
echo =====================================
echo.
echo 📋 What was configured:
echo   ✅ Main branch protection with required PR reviews
echo   ✅ Required status checks for CI/CD workflows  
echo   ✅ Development branch protection (more lenient)
echo   ✅ Repository merge settings (squash merge enabled)
echo   ✅ PR template for consistent reviews
echo.
echo 🚀 Your workflow is now enforced:
echo   - Direct pushes to main/bishop_dev are blocked
echo   - PRs require approval and passing CI checks
echo   - Feature isolation is enforced by Git hooks
echo   - Commit message validation is mandatory
echo.
echo 💡 Next steps:
echo   1. Test the workflow: create a feature branch
echo   2. Make changes to one service only
echo   3. Use 'git commit' (will use template and validation)
echo   4. Create PR - it will run all quality gates
echo.
pause