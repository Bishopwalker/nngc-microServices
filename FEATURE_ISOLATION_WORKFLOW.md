# Feature Isolation Workflow

This document explains the comprehensive workflow designed to ensure **only one feature is pushed at a time**, enforcing clean git history and better code review practices in the NNGC MicroServices project.

## Overview

The workflow combines:
- **Git Hooks** for local validation
- **GitHub Actions** for CI/CD enforcement
- **Branch Protection Rules** for merge control
- **Intelligent Commit Workflow** for automated processes

## Workflow Components

### 1. Git Hooks (`/githooks/`)

#### Pre-Commit Hook (`pre-commit`)
Runs before each commit to validate:
- ✅ **Branch Protection**: Blocks direct commits to `main`/`bishop_dev`
- ✅ **Feature Isolation**: Warns when multiple services are modified
- ✅ **Security Scanning**: Detects hardcoded secrets and debug code
- ✅ **Compilation Check**: Ensures Java services compile successfully
- ✅ **Test Validation**: Checks for corresponding test files

#### Commit Message Hook (`commit-msg`)
Validates commit message format:
- ✅ **Conventional Commits**: Enforces `type(scope): description` format
- ✅ **Length Validation**: Title 10-72 characters, body wrapped at 72
- ✅ **Content Quality**: Prevents vague messages like "fix", "update"
- ✅ **Security Awareness**: Flags security-related changes
- ✅ **Breaking Changes**: Detects and flags breaking changes

### 2. GitHub Actions Workflows (`.github/workflows/`)

#### Branch Protection Workflow (`branch-protection.yml`)
Comprehensive validation for PRs and merges:

**PR Validation Job:**
- ✅ **PR Title Validation**: Conventional commit format enforcement
- ✅ **Feature Isolation Check**: Analyzes services affected by changes  
- ✅ **PR Size Analysis**: Flags large PRs for review consideration

**Pre-Merge Tests Job:**
- ✅ **Full Build**: Compiles all services for integration testing
- ✅ **Comprehensive Tests**: Runs test suites for all services
- ✅ **Integration Testing**: Starts services and validates connectivity

**Security Check Job:**
- ✅ **Secret Scanning**: Detects potential hardcoded secrets in diff
- ✅ **Security-Sensitive Files**: Flags security configuration changes

**Merge Protection Job:**
- ✅ **Force Push Detection**: Identifies bypass attempts
- ✅ **Commit Message Validation**: Validates direct push messages

### 3. Intelligent Commit Workflow (`.claude/commands/commit-and-push.md`)

Automated workflow that:
- 🤖 **Analyzes Changes**: Detects which services are modified
- 🤖 **Generates Messages**: Creates conventional commit messages
- 🤖 **Enforces Isolation**: Warns about multi-service changes
- 🤖 **Creates PRs**: Automatically creates pull requests
- 🤖 **Runs Validation**: Ensures all quality gates pass

### 4. Branch Protection Rules

Configured via `scripts/setup-branch-protection.bat`:

**Main Branch (`main`):**
- 🛡️ **Required PR Reviews**: 1 approving review required
- 🛡️ **Status Checks**: All CI workflows must pass
- 🛡️ **Dismiss Stale Reviews**: New commits dismiss old approvals
- 🛡️ **Conversation Resolution**: All discussions must be resolved
- 🛡️ **No Force Pushes**: Direct pushes and force pushes blocked
- 🛡️ **Admin Enforcement**: Rules apply to repository admins

**Development Branch (`bishop_dev`):**
- 🔧 **Relaxed Reviews**: 1 review required but not dismissed
- 🔧 **Basic Checks**: Core CI workflows must pass
- 🔧 **Force Pushes Allowed**: For development flexibility
- 🔧 **No Admin Enforcement**: More flexible for development

## Usage Guide

### Setting Up the Workflow

1. **Install Git Hooks:**
   ```bash
   scripts/setup-git-hooks.bat
   ```

2. **Configure Branch Protection:**
   ```bash
   scripts/setup-branch-protection.bat
   ```

### Daily Development Workflow

#### Option 1: Manual Process
```bash
# 1. Create feature branch
git checkout -b feature/customer-search

# 2. Make changes to ONE service only
# Edit files in customer-service/ only

# 3. Stage changes
git add customer-service/

# 4. Commit (uses template and validation)
git commit
# The hooks will:
# - Validate you're not on protected branch
# - Check feature isolation (single service)
# - Scan for security issues
# - Validate compilation
# - Guide commit message format

# 5. Push and create PR
git push -u origin feature/customer-search
gh pr create --title "feat(customer-service): add customer search functionality"
```

#### Option 2: Automated Process (Claude Command)
```bash
# Use the intelligent workflow
/commit-and-push
```
This command will:
- Analyze your changes automatically
- Generate appropriate commit message
- Create feature branch if needed
- Push changes and create PR
- Ensure all validations pass

### Multi-Service Changes (Special Cases)

When you legitimately need to modify multiple services:

1. **The hooks will warn you** and ask for confirmation
2. **CI will flag the PR** as multi-service
3. **Review process** should be more thorough
4. **Consider breaking** into smaller, focused PRs when possible

**Valid multi-service scenarios:**
- Configuration changes affecting multiple services
- Shared library updates
- Infrastructure changes
- Security patches

## Quality Gates

### Local Quality Gates (Git Hooks)
- Branch protection ✅
- Feature isolation ✅  
- Security scanning ✅
- Compilation validation ✅
- Test coverage check ✅
- Commit message format ✅

### CI Quality Gates (GitHub Actions)
- Full build validation ✅
- Comprehensive test suite ✅
- Integration testing ✅
- Security deep scan ✅
- PR size analysis ✅
- Code review requirements ✅

### Merge Quality Gates (Branch Protection)
- Required approvals ✅
- Status check requirements ✅
- Conversation resolution ✅
- No force push bypass ✅
- Admin rule enforcement ✅

## Error Scenarios & Solutions

### "Direct commits to main blocked"
**Solution:** Create a feature branch
```bash
git checkout -b feature/my-feature
git commit -m "your message"
```

### "Multiple services modified" 
**Solution:** Either continue with warning or split the commit
```bash
# Split approach:
git reset
git add customer-service/
git commit -m "feat(customer-service): add search"
git add api-gateway/
git commit -m "feat(api-gateway): update routing for search"
```

### "Hardcoded secrets detected"
**Solution:** Use environment variables or configuration
```java
// BAD
private String apiKey = "sk_live_abc123";

// GOOD  
@Value("${stripe.api.key}")
private String apiKey;
```

### "Commit message format invalid"
**Solution:** Follow conventional commit format
```bash
# BAD
git commit -m "fix stuff"

# GOOD
git commit -m "fix(customer-service): resolve null pointer in search"
```

### "Compilation failed"
**Solution:** Fix compilation errors before committing
```bash
cd customer-service
mvn compile
# Fix any errors shown
git commit
```

## Benefits

### For Development
- 🎯 **Focus**: One feature at a time reduces cognitive load
- 🐛 **Debugging**: Easier to isolate issues to specific changes  
- 🔄 **Rollback**: Clean history enables precise rollbacks
- 👥 **Collaboration**: Reduces merge conflicts between developers

### For Code Review
- 📖 **Clarity**: Reviewers can focus on one service/feature
- ⚡ **Speed**: Smaller, focused PRs review faster
- 🎯 **Quality**: Targeted reviews catch more issues
- 📝 **Context**: Clear commit messages provide better context

### For CI/CD
- 🚀 **Performance**: Focused changes = faster pipelines
- 🎯 **Testing**: Service-specific test optimization
- 🔍 **Monitoring**: Easier to trace issues to specific changes
- 📊 **Metrics**: Better deployment success rates

### For Security
- 🔒 **Scanning**: Focused security scans are more effective
- 📋 **Auditing**: Clean git history improves security audits
- 🚨 **Detection**: Easier to identify security-related changes
- 🛡️ **Response**: Faster incident response with clear change history

## Configuration Files

| File | Purpose |
|------|---------|
| `.gitmessage` | Commit message template with guidelines |
| `.githooks/pre-commit` | Pre-commit validation and enforcement |
| `.githooks/commit-msg` | Commit message format validation |
| `.github/workflows/branch-protection.yml` | CI/CD quality gates |
| `.github/pull_request_template.md` | Standardized PR format |
| `scripts/setup-git-hooks.bat` | Git hooks installation script |
| `scripts/setup-branch-protection.bat` | GitHub protection rules setup |

## Monitoring & Analytics

The workflow automatically logs:
- **Commit patterns** in `.git/commit-log.txt`
- **Service isolation** metrics in CI
- **Security scan results** in GitHub Security tab
- **Quality gate** performance in Actions

Review these regularly to identify improvement opportunities.

---

This workflow ensures **"only one feature is pushed at a time"** while maintaining development velocity and code quality. It scales from solo development to team collaboration seamlessly.